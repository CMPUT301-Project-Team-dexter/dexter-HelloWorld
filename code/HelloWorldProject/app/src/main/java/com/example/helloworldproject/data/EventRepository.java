package com.example.helloworldproject.data;

import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.util.CurrentProfile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
     * Load a single event by its ID asynchronously.
     * <p>
     *     Note: The function returns immediately.
     *     The result is delivered via the provided callback.
     *     Anything relying on the result must be done in the {@link LoadCallback onLoaded} methods.
     * </p>
     *
     * @param eventId: the ID of the event to load
     * @param cb: the callback to handle the result
     */
    public void asyncLoadById(String eventId, final LoadCallback cb) {
        db.collection("events").document(eventId).get()
            .addOnSuccessListener((ds) -> {
                if (ds != null && ds.exists()) {
                    Event e = ds.toObject(Event.class);
                    if (e != null) e.setId(ds.getId());
                    cb.onLoaded(e);
                } else {
                    cb.onNotFound();
                }
            })
            .addOnFailureListener(cb::onError);
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
     *     Note: The function returns immediately.
     *     The result is delivered via the provided callback.
     *     Anything relying on the result must be done in the {@link ListCallback onLoaded} methods.
     * </p>
     *
     * @param organizerName: the name of the organizer
     * @param cachedEvents: the list of already cached events
     * @param cb: the callback to handle the result
     */
    public void asyncLoadUncachedEventsCreatedBy(
        String organizerName,
        ArrayList<Event> cachedEvents,
        final ListCallback cb
    ) {
        if (cachedEvents.isEmpty()) {
            db.collection("events")
                .whereEqualTo("creator", organizerName).get()
                .addOnSuccessListener((ds) -> {
                    List<Event> out = new ArrayList<>();
                    for (QueryDocumentSnapshot d : ds) {
                        Event e = d.toObject(Event.class);
                        e.setId(d.getId());
                        out.add(e);
                    }
                    cb.onLoaded(out);
                })
                .addOnFailureListener(cb::onError);
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
        Tasks.whenAllSuccess(tasks)
            .addOnSuccessListener(results -> {
                List<Event> out = new ArrayList<>();
                for (Object result : results) {
                    QuerySnapshot qs = (QuerySnapshot) result;
                    for (QueryDocumentSnapshot d : qs) {
                        Event e = d.toObject(Event.class);
                        e.setId(d.getId());
                        out.add(e);
                    }
                }
                cb.onLoaded(out);
            })
            .addOnFailureListener(cb::onError);
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

    public void loadAllEvents(final ListCallback cb) {
        db.collection("events")
                .get()
                .addOnSuccessListener(snap -> {
                    List<Event> out = new ArrayList<>();
                    for (QueryDocumentSnapshot d : snap) {
                        if (d != null) {
                            Event e = d.toObject(Event.class);
                            e.setId(d.getId());
                            out.add(e);
                        }
                    }

                    // Optional: sort by registrationCloseAt ascending (soonest deadlines first)
                    out.sort(Comparator.comparing(Event::getRegistrationCloseAt,
                            Comparator.nullsLast(Timestamp::compareTo)));

                    cb.onLoaded(out);
                })
                .addOnFailureListener(cb::onError);
    }

    public void loadRegisterHistoryEvents (final ListCallback cb) {
        String thisDeviceId = CurrentProfile.get().getDeviceId();
        db.collection("events")
                .get()
                .addOnSuccessListener(snap -> {
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    List<QueryDocumentSnapshot> parentEvents = new ArrayList<>(); // keep track of parent events

                    for (QueryDocumentSnapshot d : snap) {
                        parentEvents.add(d); // save the parent event
                        tasks.add(d.getReference().collection("waitlist").get());
                        tasks.add(d.getReference().collection("invites").get());
                    }

                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                        List<Event> out = new ArrayList<>();
                        Set<String> addedEventIds = new HashSet<>();

                        for (int i = 0; i < results.size(); i++) {
                            QuerySnapshot subSnap = (QuerySnapshot) results.get(i);
                            QueryDocumentSnapshot parentEvent = parentEvents.get(i / 2);

                            for (QueryDocumentSnapshot dd : subSnap) {
                                String profileId = dd.getString("profileId");
                                if (CurrentProfile.get().getDeviceId().equals(profileId)) {
                                    Event e = parentEvent.toObject(Event.class);
                                    e.setId(parentEvent.getId());
                                    if (!addedEventIds.contains(parentEvent.getId())) {
                                        addedEventIds.add(parentEvent.getId());
                                        out.add(e);
                                    }
                                }
                            }
                        }

                        cb.onLoaded(out);
                    });
                })
                .addOnFailureListener(cb::onError);
    }

}
