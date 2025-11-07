package com.example.helloworldproject.data;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/** Firestore repository for event waitlist counts. */
public class WaitlistRepository {

    public interface CountListener {
        void onCount(int total);
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

    public void addToWaitlist(String eventId, String userId, CountListener listener) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("userId", userId);

        // Use userId as document ID to prevent duplicates
        db.collection("events")
                .document(eventId)
                .collection("waitlist")
                .document(userId)
                .set(entry)
                .addOnSuccessListener(aVoid -> {
                    // Fetch new count after adding
                    db.collection("events")
                            .document(eventId)
                            .collection("waitlist")
                            .get()
                            .addOnSuccessListener(snapshot -> listener.onCount(snapshot.size()))
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

}
