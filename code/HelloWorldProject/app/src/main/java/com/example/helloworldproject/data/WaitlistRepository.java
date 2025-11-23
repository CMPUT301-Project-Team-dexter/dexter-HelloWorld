package com.example.helloworldproject.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

import com.example.helloworldproject.model.Profile;

/** Firestore repository for event waitlist counts. */
public class WaitlistRepository {

    public interface CountListener {
        void onCount(int total);
        void onError(Exception e);
    }

    public interface MembershipListener {
        void onResult(boolean isInWaitlist);
        void onError(Exception e);
    }

    public interface CompletionListener {
        void onSuccess();
        void onError(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Observe waitlist size in real time for a given event. */
    public ListenerRegistration observeCount(String eventId, final CountListener listener) {
        return db.collection("events")
                .document(eventId)
                .collection("waitlist")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            listener.onError(error);
                            return;
                        }
                        int size = (snapshot == null) ? 0 : snapshot.size();
                        listener.onCount(size);
                    }
                });
    }

    /** Check if a profile is already on the waitlist for an event. */
    public void isInWaitlist(String eventId, String profileId, final MembershipListener listener) {
        db.collection("events")
                .document(eventId)
                .collection("waitlist")
                .document(profileId)
                .get()
                .addOnSuccessListener((DocumentSnapshot snapshot) -> listener.onResult(snapshot.exists()))
                .addOnFailureListener(listener::onError);
    }

    /** Add the current profile to an event's waitlist. */
    public void joinWaitlist(String eventId, @NonNull Profile profile, final CompletionListener listener) {
        Map<String, Object> data = new HashMap<>();
        data.put("profileId", profile.getId());
        data.put("name", profile.getName());
        data.put("email", profile.getEmail());
        data.put("phone", profile.getPhone());
        data.put("joinedAt", Timestamp.now());

        db.collection("events")
                .document(eventId)
                .collection("waitlist")
                .document(profile.getId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
    }

    /** Remove the current profile from an event's waitlist. */
    public void leaveWaitlist(String eventId, String profileId, final CompletionListener listener) {
        db.collection("events")
                .document(eventId)
                .collection("waitlist")
                .document(profileId)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
    }
}
