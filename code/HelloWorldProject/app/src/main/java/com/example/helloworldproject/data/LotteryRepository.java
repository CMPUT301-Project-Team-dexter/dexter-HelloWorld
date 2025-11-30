package com.example.helloworldproject.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworldproject.model.InvitationRecord;
import com.example.helloworldproject.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Repository for lottery draws and invite status.
 */
public class LotteryRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface DrawCallback {
        void onComplete(int invitedCount);

        void onError(Exception e);
    }

    public interface InviteStatusListener {
        void onLoaded(@Nullable String status);

        void onError(Exception e);
    }

    public interface InviteSummaryListener {
        void onLoaded(int pending, int accepted, int declined);

        void onError(Exception e);
    }

    public interface CompletionListener {
        void onSuccess();

        void onError(Exception e);
    }

    public interface AcceptedEntrantsListener {
        /**
         * Called whenever the accepted entrants list is loaded or updated.
         *
         * @param entrants A list of Profile objects corresponding to all invites
         *                 whose status is "ACCEPTED" for the given event.
         */
        void onLoaded(List<Profile> entrants);

        /**
         * Called if there is any error while talking to Firestore.
         *
         * @param e The underlying exception from the Firestore SDK.
         */
        void onError(Exception e);
    }

    public interface InvitationHistoryListener {
        /**
         * Called when a new snapshot of invitations has been loaded.
         *
         * @param invitations The full list of invitations matching the current filter.
         */
        void onLoaded(List<InvitationRecord> invitations);

        /**
         * Called if an error happens while fetching data from Firestore.
         *
         * @param e The underlying exception.
         */
        void onError(Exception e);
    }

    /**
     * Fetch the current invite status once. This is useful for cases where a snapshot listener might
     * not have fired yet (e.g., immediately after a draw on a slow connection) but the UI needs to
     * render the latest known state.
     */
    public void fetchInviteStatus(
        String eventId,
        String profileId,
        final InviteStatusListener listener
    ) {
        db.collection("events")
            .document(eventId)
            .collection("invites")
            .document(profileId)
            .get()
            .addOnSuccessListener(snapshot -> {
                if (snapshot == null || !snapshot.exists()) {
                    listener.onLoaded(null);
                } else {
                    listener.onLoaded(snapshot.getString("status"));
                }
            })
            .addOnFailureListener(listener::onError);
    }

    /**
     * Run a lottery draw using the requested sample size. Invites are created for new profiles that were
     * not already invited. Drawing is done client-side with a simple shuffle.
     */
    public void runDraw(String eventId, int requestedSampleSize, final DrawCallback cb) {
        executor.execute(() -> {
            try {
                Task<DocumentSnapshot> eventTask = db.collection("events")
                    .document(eventId)
                    .get();
                Task<QuerySnapshot> waitlistTask = db.collection("events")
                    .document(eventId)
                    .collection("waitlist")
                    .get();
                Task<QuerySnapshot> invitesTask = db.collection("events")
                    .document(eventId)
                    .collection("invites")
                    .get();
                Tasks.await(eventTask, 10, TimeUnit.SECONDS);
                Tasks.await(waitlistTask, 10, TimeUnit.SECONDS);
                Tasks.await(invitesTask, 10, TimeUnit.SECONDS);

                if (!eventTask.isSuccessful()) {
                    cb.onError(eventTask.getException());
                    return;
                }
                if (!waitlistTask.isSuccessful()) {
                    cb.onError(waitlistTask.getException());
                    return;
                }
                if (!invitesTask.isSuccessful()) {
                    cb.onError(invitesTask.getException());
                    return;
                }

                // Determine remaining capacity. If capacity is missing, fall back to requested size.
                DocumentSnapshot eventDoc = eventTask.getResult();
                int capacity = Integer.MAX_VALUE;
                if (eventDoc != null && eventDoc.exists()) {
                    Number capNumber = eventDoc.getLong("capacity");
                    if (capNumber != null && capNumber.intValue() > 0) {
                        capacity = capNumber.intValue();
                    }
                }

                int pendingCount = 0;
                int acceptedCount = 0;

                Set<String> alreadyInvited = new HashSet<>();
                for (DocumentSnapshot doc : invitesTask.getResult().getDocuments()) {
                    alreadyInvited.add(doc.getId());
                    String status = doc.getString("status");
                    if ("ACCEPTED".equals(status)) {
                        acceptedCount++;
                    } else if (!"DECLINED".equals(status)) {
                        // Treat null/unknown as pending to avoid over-inviting.
                        pendingCount++;
                    }
                }

                int availableSlots = capacity == Integer.MAX_VALUE
                    ? requestedSampleSize
                    : Math.max(0, capacity - acceptedCount - pendingCount);
                int drawSize = Math.min(requestedSampleSize, availableSlots);
                if (drawSize <= 0) {
                    cb.onComplete(0);
                    return;
                }

                List<Profile> candidates = new ArrayList<>();
                for (DocumentSnapshot doc : waitlistTask.getResult().getDocuments()) {
                    String id = doc.getId();
                    if (alreadyInvited.contains(id)) continue;
                    Profile p = new Profile();
                    p.setId(id);
                    p.setName(doc.getString("name"));
                    p.setEmail(doc.getString("email"));
                    p.setPhone(doc.getString("phone"));
                    candidates.add(p);
                }

                if (candidates.isEmpty()) {
                    cb.onComplete(0);
                    return;
                }

                Collections.shuffle(candidates);
                int inviteCount = Math.min(drawSize, candidates.size());

                WriteBatch batch = db.batch();
                Timestamp now = Timestamp.now();
                for (int i = 0; i < inviteCount; i++) {
                    Profile p = candidates.get(i);
                    Map<String, Object> invite = new HashMap<>();
                    invite.put("profileId", p.getId());
                    invite.put("name", p.getName());
                    invite.put("email", p.getEmail());
                    invite.put("phone", p.getPhone());
                    invite.put("status", "PENDING");
                    invite.put("invitedAt", now);
                    batch.set(db.collection("events")
                        .document(eventId)
                        .collection("invites")
                        .document(p.getId()), invite);
                }
                Task<Void> commit = batch.commit();
                Tasks.await(commit, 10, TimeUnit.SECONDS);
                if (commit.isSuccessful()) {
                    cb.onComplete(inviteCount);
                } else {
                    cb.onError(commit.getException());
                }
            } catch (Exception e) {
                cb.onError(e);
            }
        });
    }

    /**
     * Accept an invite by setting status to ACCEPTED and removing the entrant from the waitlist.
     */
    public void acceptInvite(String eventId, String profileId, final CompletionListener listener) {
        Map<String, Object> patch = new HashMap<>();
        patch.put("status", "ACCEPTED");
        patch.put("respondedAt", Timestamp.now());

        WriteBatch batch = db.batch();
        batch.set(db.collection("events")
                .document(eventId)
                .collection("invites")
                .document(profileId),
            patch, com.google.firebase.firestore.SetOptions.merge());
        batch.delete(db.collection("events")
            .document(eventId)
            .collection("waitlist")
            .document(profileId));

        batch.commit()
            .addOnSuccessListener(unused -> listener.onSuccess())
            .addOnFailureListener(listener::onError);
    }

    /**
     * Decline an invite by setting status to DECLINED and removing the entrant from the waitlist.
     */
    public void declineInvite(String eventId, String profileId, final CompletionListener listener) {
        Map<String, Object> patch = new HashMap<>();
        patch.put("status", "DECLINED");
        patch.put("respondedAt", Timestamp.now());

        WriteBatch batch = db.batch();
        batch.set(db.collection("events")
                .document(eventId)
                .collection("invites")
                .document(profileId),
            patch, com.google.firebase.firestore.SetOptions.merge());
        batch.delete(db.collection("events")
            .document(eventId)
            .collection("waitlist")
            .document(profileId));

        batch.commit()
            .addOnSuccessListener(unused -> listener.onSuccess())
            .addOnFailureListener(listener::onError);
    }

    public void cancelAcceptedEntrant(
        @NonNull String eventId,
        @NonNull String profileId,
        @Nullable final CompletionListener listener
    ) {
        DocumentReference ref = db.collection("events")
            .document(eventId)
            .collection("invites")
            .document(profileId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "CANCELLED");
        updates.put("cancelledAt", FieldValue.serverTimestamp());

        ref.update(updates)
            .addOnSuccessListener(unused -> {
                if (listener != null) {
                    listener.onSuccess();
                }
            })
            .addOnFailureListener(e -> {
                if (listener != null) {
                    listener.onError(e);
                }
            });
    }

    /**
     * Observe invite status for a single profile.
     */
    public ListenerRegistration observeInviteStatus(
        String eventId,
        String profileId,
        final InviteStatusListener listener
    ) {
        return db.collection("events")
            .document(eventId)
            .collection("invites")
            .document(profileId)
            .addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    listener.onError(error);
                    return;
                }
                if (snapshot == null || !snapshot.exists()) {
                    listener.onLoaded(null);
                    return;
                }
                listener.onLoaded(snapshot.getString("status"));
            });
    }

    /**
     * Observe invite summary counts for organizers.
     */
    public ListenerRegistration observeInviteSummary(String eventId, final InviteSummaryListener listener) {
        return db.collection("events")
            .document(eventId)
            .collection("invites")
            .addSnapshotListener((snapshots, error) -> {
                if (error != null) {
                    listener.onError(error);
                    return;
                }
                int pending = 0, accepted = 0, declined = 0;
                if (snapshots != null) {
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        String status = doc.getString("status");
                        if ("ACCEPTED".equals(status)) {
                            accepted++;
                        } else if ("DECLINED".equals(status)) {
                            declined++;
                        } else {
                            pending++;
                        }
                    }
                }
                listener.onLoaded(pending, accepted, declined);
            });
    }

    public ListenerRegistration observeAcceptedEntrants(
        String eventId,
        final AcceptedEntrantsListener listener
    ) {
        return db.collection("events")
            .document(eventId)
            .collection("invites")
            .whereEqualTo("status", "ACCEPTED")
            .addSnapshotListener((snapshots, error) -> {
                if (error != null) {
                    listener.onError(error);
                    return;
                }

                List<Profile> entrants = new ArrayList<>();
                if (snapshots != null) {
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        String profileId = doc.getId();

                        Profile p = new Profile();
                        p.setId(profileId);
                        p.setName(doc.getString("name"));
                        p.setEmail(doc.getString("email"));
                        p.setPhone(doc.getString("phone"));

                        entrants.add(p);
                    }
                }

                listener.onLoaded(entrants);
            });
    }

    public ListenerRegistration observeInvitationHistory(
        String eventId,
        @Nullable List<String> statusFilter,
        final InvitationHistoryListener listener
    ) {
        Query query = db.collection("events")
            .document(eventId)
            .collection("invites")
            .orderBy("invitedAt", Query.Direction.DESCENDING);

        if (statusFilter != null && !statusFilter.isEmpty()) {
            query = query.whereIn("status", statusFilter);
        }

        return query.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                listener.onError(error);
                return;
            }

            List<InvitationRecord> records = new ArrayList<>();

            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    String profileId = doc.getId();
                    String name = doc.getString("name");
                    String code = doc.getString("profileId");
                    if (code == null) {
                        code = profileId;
                    }
                    String status = doc.getString("status");

                    InvitationRecord record = new InvitationRecord(
                        profileId,
                        name,
                        code,
                        status
                    );
                    records.add(record);
                }
            }

            listener.onLoaded(records);
        });
    }
}
