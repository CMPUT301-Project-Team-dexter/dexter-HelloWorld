package com.example.helloworldproject.data;

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

    // Type constant used for "lottery not chosen" notifications.
    public static final String TYPE_LOTTERY_NOT_CHOSEN = "LOTTERY_NOT_CHOSEN";

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
        DocumentReference ref = db.collection(COLLECTION_PROFILES)
            .document(profileId)
            .collection(SUBCOLLECTION_NOTIFICATIONS)
            .document();

        Map<String, Object> data = new HashMap<>();
        data.put("type", TYPE_LOTTERY_NOT_CHOSEN);
        data.put("eventId", eventId);
        data.put("eventTitle", eventTitle);
        data.put("read", false);
        data.put("createdAt", Timestamp.now());

        ref.set(data);
    }
}
