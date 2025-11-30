package com.example.helloworldproject.data;

import androidx.annotation.NonNull;

import com.example.helloworldproject.model.Entrant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Firestore repository for Entrant.
 */
public class EntrantRepository {

    public interface ListenCallback {
        void onChanged(List<Entrant> entrants);

        void onError(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration currentReg;

    /**
     * (PENDING)  —— US 02.06.01
     */
    public void listenChosenPending(@NonNull String eventId, @NonNull final ListenCallback cb) {
        stop();
        Query q = db.collection("events").document(eventId)
            .collection("entrants")
            .whereEqualTo("isChosen", true)
            .whereEqualTo("status", "PENDING")
            .orderBy("createdAt", Query.Direction.DESCENDING);

        currentReg = q.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                cb.onError(e);
                return;
            }
            if (snapshots == null) {
                cb.onChanged(new ArrayList<>());
                return;
            }
            List<Entrant> list = new ArrayList<>();
            snapshots.getDocuments().forEach(doc -> {
                Entrant en = doc.toObject(Entrant.class);
                if (en != null) {
                    en.setId(doc.getId());
                    list.add(en);
                }
            });
            cb.onChanged(list);
        });
    }

    /**
     * ACCEPTED —— US 02.06.04
     */
    public void listenAccepted(@NonNull String eventId, @NonNull final ListenCallback cb) {
        stop();
        Query q = db.collection("events").document(eventId)
            .collection("entrants")
            .whereEqualTo("status", "ACCEPTED")
            .orderBy("createdAt", Query.Direction.DESCENDING);

        currentReg = q.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                cb.onError(e);
                return;
            }
            if (snapshots == null) {
                cb.onChanged(new ArrayList<>());
                return;
            }
            List<Entrant> list = new ArrayList<>();
            snapshots.getDocuments().forEach(doc -> {
                Entrant en = doc.toObject(Entrant.class);
                if (en != null) {
                    en.setId(doc.getId());
                    list.add(en);
                }
            });
            cb.onChanged(list);
        });
    }

    /**
     * invite (set as PENDING) —— US 02.06.02
     */
    public Task<Void> invite(@NonNull String eventId, @NonNull String entrantId) {
        DocumentReference ref = db.collection("events").document(eventId)
            .collection("entrants").document(entrantId);
        return ref.update("status", "PENDING");
    }

    /**
     * cancel (CANCELED) —— US 02.06.03
     */
    public Task<Void> cancel(@NonNull String eventId, @NonNull String entrantId) {
        DocumentReference ref = db.collection("events").document(eventId)
            .collection("entrants").document(entrantId);
        return ref.update("status", "CANCELED");
    }

    /**
     * (ACCEPTED) —— (02.06.04 demo)
     */
    public Task<Void> markAccepted(@NonNull String eventId, @NonNull String entrantId) {
        DocumentReference ref = db.collection("events").document(eventId)
            .collection("entrants").document(entrantId);
        return ref.update("status", "ACCEPTED");
    }

    public void stop() {
        if (currentReg != null) {
            currentReg.remove();
            currentReg = null;
        }
    }
}
