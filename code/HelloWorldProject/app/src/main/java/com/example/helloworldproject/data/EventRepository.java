package com.example.helloworldproject.data;

import com.example.helloworldproject.model.Event;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Repository for managing Event data in Firestore.
 */
public class EventRepository {
    private EventRepository() {  }

    public static final EventRepository INSTANCE = new EventRepository();

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

    /**
     * Load a single event by its ID.
     * <p>
     * Warning: this method blocks the calling thread until completion.
     * Do NOT call this method on the main/UI thread.
     *
     * @param eventId: the ID of the event to load
     * @param cb: the callback to handle the result
     */
    public void syncLoadById(String eventId, final LoadCallback cb) {
        Task<DocumentSnapshot> task =
            db.collection("events").document(eventId).get();
        try {
            Tasks.await(task, 10, TimeUnit.SECONDS);
            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                if (ds != null && ds.exists()) {
                    Event e = ds.toObject(Event.class);
                    if (e != null) e.setId(ds.getId());
                    cb.onLoaded(e);
                } else {
                    cb.onNotFound();
                }
            } else {
                cb.onError(task.getException());
            }
        } catch (Exception e) {
            cb.onError(e);
        }
    }

    /**
     * Save or update an event.
     * @param e: the event to save or update
     * @param cb: the callback to handle completion
     */
    public void saveOrUpdate(Event e, final CompleteCallback cb) {
        String eventId = e.getId();
        db.collection("events")
            .document(eventId)
            .set(e, SetOptions.merge())
            .addOnSuccessListener(unused -> cb.onComplete())
            .addOnFailureListener(cb::onError);
    }

    private static final int BATCH_SIZE = 30;

    /**
     * Load events created by a specific organizer, excluding those already cached.
     * <p>
     * Warning: this method blocks the calling thread until completion.
     * Do NOT call this method on the main/UI thread.
     *
     * @param organizerName: the name of the organizer
     * @param cachedEvents: the list of already cached events
     * @param cb: the callback to handle the result
     */
    public void syncLoadUncachedEventsCreatedBy(
        String organizerName,
        ArrayList<Event> cachedEvents,
        final ListCallback cb
    ) {
        if (cachedEvents.isEmpty()) {
            Task<QuerySnapshot> task = db.collection("events")
                .whereEqualTo("creator", organizerName)
                .get();
            try {
                Tasks.await(task, 10, TimeUnit.SECONDS);
                if (task.isSuccessful()) {
                    QuerySnapshot snap = task.getResult();
                    List<Event> out = new ArrayList<>();
                    for (QueryDocumentSnapshot d : snap) {
                        Event e = d.toObject(Event.class);
                        e.setId(d.getId());
                        out.add(e);
                    }
                    cb.onLoaded(out);
                } else {
                    cb.onError(task.getException());
                }
            } catch (Exception e) {
                cb.onError(e);
            }
            return;
        }
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        List<String> cachedIds = cachedEvents.stream()
            .map(Event::getId)
            .collect(Collectors.toList());
        for (int i = 0; i < cachedIds.size(); i += BATCH_SIZE) {
            List<String> batchIds = cachedIds.subList(i, Math.min(i + BATCH_SIZE, cachedIds.size()));
            Task<QuerySnapshot> subtask = db.collection("events")
                .whereEqualTo("creator", organizerName)
                .whereNotIn(FieldPath.documentId(), batchIds)
                .get();
            tasks.add(subtask);
        }
        Task<List<QuerySnapshot>> task = Tasks.whenAllSuccess(tasks);
        try {
            Tasks.await(task, 10, TimeUnit.SECONDS);
            if (task.isSuccessful()) {
                List<Event> out = new ArrayList<>();
                for (QuerySnapshot qs : task.getResult()) {
                    for (QueryDocumentSnapshot d : qs) {
                        Event e = d.toObject(Event.class);
                        e.setId(d.getId());
                        out.add(e);
                    }
                }
                cb.onLoaded(out);
            } else {
                cb.onError(task.getException());
            }
        } catch (Exception e) {
            cb.onError(e);
        }
    }

    /**
     * technically unusable for now
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
                        e.setId(d.getId());
                        if (e.getRegistrationOpenAt() == null || e.getRegistrationCloseAt() == null) continue;
                        // client-side second predicate: openAt <= now
                        if (e.getRegistrationOpenAt().compareTo(now) <= 0) {
                            out.add(e);
                        }
                    }
                    // client-side sort by closeAt ascending
                    out.sort(Comparator.comparing(Event::getRegistrationCloseAt));
                    cb.onLoaded(out);
                })
                .addOnFailureListener(cb::onError);
    }
}
