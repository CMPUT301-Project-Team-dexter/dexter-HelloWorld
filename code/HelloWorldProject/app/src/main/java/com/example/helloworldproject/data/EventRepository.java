package com.example.helloworldproject.data;

import androidx.annotation.NonNull;

import com.example.helloworldproject.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/** Firestore repository for Event. */
public class EventRepository {

    public interface LoadCallback {
        void onLoaded(Event e);
        void onNotFound();
        void onError(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadById(String eventId, final LoadCallback cb) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(ds -> {
                    if (ds.exists()) {
                        Event e = ds.toObject(Event.class);
                        if (e != null) e.setId(ds.getId());
                        cb.onLoaded(e);
                    } else {
                        cb.onNotFound();
                    }
                })
                .addOnFailureListener(cb::onError);
    }
}
