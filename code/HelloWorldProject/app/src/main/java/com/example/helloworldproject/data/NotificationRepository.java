package com.example.helloworldproject.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworldproject.model.NotificationRecord;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for working with in-app notifications stored under:
 * <p>
 * profiles/{profileId}/notifications/{notificationId}
 */
public class NotificationRepository {

    private static final String COLLECTION_PROFILES = "profiles";
    private static final String SUBCOLLECTION_NOTIFICATIONS = "notifications";
    private static final String TAG = "NotificationRepo";

    // Type constant used for "lottery not chosen" notifications.
    public static final String TYPE_LOTTERY_NOT_CHOSEN = "LOTTERY_NOT_CHOSEN";
    // Type constant used for "lottery chosen" notifications.
    public static final String TYPE_LOTTERY_CHOSEN = "LOTTERY_CHOSEN";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Listener that receives updates for a user's notification list.
     */
    public interface NotificationListListener {
        void onLoaded(List<NotificationRecord> notifications);

        void onError(Exception e);
    }

    /**
     * Observe all notifications for the given profile, ordered by most recent.
     *
     * @param profileId The id of the profile (same as Profile document id).
     * @param listener  Callback that receives notification list updates.
     * @return ListenerRegistration which should be removed when no longer needed.
     */
    public ListenerRegistration observeNotifications(
            @NonNull String profileId,
            @NonNull final NotificationListListener listener
    ) {
        backfillChosenNotifications(profileId);

        Query query = db.collection(COLLECTION_PROFILES)
                .document(profileId)
                .collection(SUBCOLLECTION_NOTIFICATIONS)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        return query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    listener.onError(error);
                    return;
                }

                List<NotificationRecord> list = new ArrayList<>();

                if (snapshots != null) {
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        NotificationRecord record = new NotificationRecord();
                        record.setId(doc.getId());
                        record.setType(doc.getString("type"));
                        record.setEventId(doc.getString("eventId"));
                        record.setEventTitle(doc.getString("eventTitle"));
                        Boolean read = doc.getBoolean("read");
                        record.setRead(read != null && read);
                        Timestamp ts = doc.getTimestamp("createdAt");
                        record.setCreatedAt(ts);

                        list.add(record);
                    }
                }

                listener.onLoaded(list);
            }
        });
    }

    /**
     * Create a "lottery not chosen" notification for the given profile.
     *
     * <p>
     * This method is typically called by the lottery logic when a user
     * was not selected in the draw for a specific event.
     * </p>
     *
     * @param profileId  Id of the entrant profile.
     * @param eventId    Id of the event.
     * @param eventTitle Human friendly event title.
     */
    public void createLotteryNotChosenNotification(
            @NonNull String profileId,
            @NonNull String eventId,
            @NonNull String eventTitle
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", TYPE_LOTTERY_NOT_CHOSEN);
        data.put("eventId", eventId);
        data.put("eventTitle", eventTitle);
        data.put("read", false);
        data.put("createdAt", Timestamp.now());

        createNotificationRespectingOptOut(profileId, data);
    }

    /**
     * Create a "lottery chosen" notification for the given profile.
     *
     * @param profileId  Id of the entrant profile.
     * @param eventId    Id of the event.
     * @param eventTitle Human friendly event title.
     */
    public void createLotteryChosenNotification(
            @NonNull String profileId,
            @NonNull String eventId,
            @NonNull String eventTitle
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", TYPE_LOTTERY_CHOSEN);
        data.put("eventId", eventId);
        data.put("eventTitle", eventTitle);
        data.put("read", false);
        data.put("createdAt", Timestamp.now());

        createNotificationRespectingOptOut(profileId, data);
    }

    /**
     * Backfill notifications for any existing invites where the entrant was chosen but
     * no notification document was ever created (e.g., invites created before the
     * notifications feature existed). This keeps the Notifications screen from appearing
     * empty for entrants who were already selected.
     */
    private void backfillChosenNotifications(@NonNull String profileId) {
        db.collectionGroup("invites")
                .whereEqualTo("profileId", profileId)
                .get()
                .addOnSuccessListener(invites -> {
                    for (DocumentSnapshot inviteDoc : invites.getDocuments()) {
                        String status = inviteDoc.getString("status");
                        if ("CANCELLED".equals(status)) {
                            continue;
                        }

                        DocumentReference eventRef = inviteDoc.getReference()
                                .getParent()
                                .getParent();
                        if (eventRef == null) continue;

                        String eventId = eventRef.getId();

                        db.collection(COLLECTION_PROFILES)
                                .document(profileId)
                                .collection(SUBCOLLECTION_NOTIFICATIONS)
                                .whereEqualTo("type", TYPE_LOTTERY_CHOSEN)
                                .whereEqualTo("eventId", eventId)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(existing -> {
                                    if (!existing.isEmpty()) return;

                                    eventRef.get()
                                            .addOnSuccessListener(eventSnapshot -> {
                                                String eventTitle = eventSnapshot.getString("title");
                                                createLotteryChosenNotification(
                                                        profileId,
                                                        eventId,
                                                        eventTitle == null ? "this event" : eventTitle
                                                );
                                            })
                                            .addOnFailureListener(e -> Log.w(TAG, "Failed to load event for backfill", e));
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Failed to check existing notifications", e));
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Failed to backfill invites", e));
    }

    private void createNotificationRespectingOptOut(
            @NonNull String profileId,
            @NonNull Map<String, Object> data
    ) {
        DocumentReference profileRef = db.collection(COLLECTION_PROFILES)
                .document(profileId);

        profileRef.get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot == null || !snapshot.exists()) {
                        Log.w(TAG, "Profile " + profileId + " missing; skip notification");
                        return;
                    }

                    Boolean optOut = snapshot.getBoolean("notificationOptOut");
                    if (optOut != null && optOut) {
                        Log.i(TAG, "Profile " + profileId + " opted out of notifications");
                        return;
                    }

                    profileRef.collection(SUBCOLLECTION_NOTIFICATIONS)
                            .document()
                            .set(data)
                            .addOnFailureListener(e -> Log.w(TAG, "Failed to write notification for " + profileId, e));
                })
                .addOnFailureListener(e -> Log.w(TAG, "Failed to load profile " + profileId + " for notifications", e));
    }
}