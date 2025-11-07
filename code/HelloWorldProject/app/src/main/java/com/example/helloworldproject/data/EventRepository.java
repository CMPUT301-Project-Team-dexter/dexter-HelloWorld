package com.example.helloworldproject.data;

import androidx.annotation.NonNull;

import com.example.helloworldproject.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;


/** Firestore repository for Event. */
public class EventRepository {

    public interface LoadCallback {
        void onLoaded(Event e);
        void onNotFound();
        void onError(Exception e);
    }

    public interface ListCallback {
        void onLoaded(List<Event> events);
        void onError(Exception e);
    }


    public interface CompleteCallback {
        void onComplete();
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

    public void saveOrUpdate(Event e, final CompleteCallback cb) {
        String eventId = e.getId();
        db.collection("events")
            .document(eventId)
            .set(e, SetOptions.merge())
            .addOnSuccessListener(unused -> cb.onComplete())
            .addOnFailureListener(cb::onError);
    }

    /** Joinable events time for entrants: open <= now < close (soonest closing first). */
    /** Joinable events: (openAt <= now) AND (closeAt > now)
     *  No custom index needed; we sort client-side.
     */
    public void loadJoinableEvents(final ListCallback cb) {
        Timestamp now = Timestamp.now();
        db.collection("events")
                .whereGreaterThan("registrationCloseAt", now)   // single inequality on one field
                .get()
                .addOnSuccessListener(snap -> {
                    List<Event> out = new ArrayList<>();
                    for (QueryDocumentSnapshot d : snap) {
                        Event e = d.toObject(Event.class);
                        if (e == null) continue;
                        e.setId(d.getId());
                        if (e.getRegistrationOpenAt() == null || e.getRegistrationCloseAt() == null) continue;
                        // client-side second predicate: openAt <= now
                        if (e.getRegistrationOpenAt().compareTo(now) <= 0) {
                            out.add(e);
                        }
                    }
                    // client-side sort by closeAt ascending
                    out.sort((a, b) -> a.getRegistrationCloseAt().compareTo(b.getRegistrationCloseAt()));
                    cb.onLoaded(out);
                })
                .addOnFailureListener(cb::onError);
    }




}
