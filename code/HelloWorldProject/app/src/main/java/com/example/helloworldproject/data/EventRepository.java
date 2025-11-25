package com.example.helloworldproject.data;

import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.util.DateUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import android.util.Log;
import android.util.Pair;

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

    public void loadFilteredEvents(long date, List<String> selectedInterests, ListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("events");

        Pair<Long, Long> range = DateUtils.getDayRange(date);
        if (date != 0) {
            Timestamp startTimestamp = new Timestamp(new Date(range.first));
            Timestamp endTimestamp = new Timestamp(new Date(range.second));

            // DEBUG LOGS
            Log.d("FILTER_DEBUG", "Filtering for Date Range: " + startTimestamp + " to " + endTimestamp);
            if (selectedInterests != null) {
                Log.d("FILTER_DEBUG", "Filtering for Interests: " + selectedInterests.toString());
            } else {
                Log.d("FILTER_DEBUG", "Interests filter is NULL/Empty");
            }

            query = query.whereGreaterThanOrEqualTo("eventStartAt", startTimestamp)
                    .whereLessThanOrEqualTo("eventStartAt", endTimestamp);
        }

        if (selectedInterests != null && !selectedInterests.isEmpty()) {
            query = query.whereArrayContainsAny("interests", selectedInterests);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            // for debugging returns of query
            Log.d("FILTER_DEBUG", "Query Finished. Total Documents Found: " + queryDocumentSnapshots.size());

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                // Print the ID and Title of every match found
                Log.d("FILTER_DEBUG", "MATCH: ID=" + doc.getId() + ", Title=" + doc.get("title"));
            }


            List<Event> events = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                events.add(doc.toObject(Event.class));
            }
            callback.onLoaded(events);
        }).addOnFailureListener(e -> {

            // for debugging errors from query
            Log.e("FILTER_DEBUG", "Query FAILED: " + e.getMessage());

            callback.onError(e);
        });
    }
}
