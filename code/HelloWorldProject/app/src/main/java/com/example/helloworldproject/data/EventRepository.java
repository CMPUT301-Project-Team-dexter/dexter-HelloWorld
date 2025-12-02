package com.example.helloworldproject.data;

import android.util.Log;
import android.util.Pair;

import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.util.CurrentProfile;
import com.example.helloworldproject.util.DateUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repository for managing Event data in Firestore.
 */
public class EventRepository {
    public static final EventRepository INSTANCE = new EventRepository();
    private static final int BATCH_SIZE = 30;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EventRepository() {
    }

    /**
     * Load a single event by its ID asynchronously.
     * <p>
     * Note: The function returns immediately.
     * The result is delivered via the provided callback.
     * Anything relying on the result must be done in the {@link LoadCallback onLoaded} methods.
     * </p>
     *
     * @param eventId: the ID of the event to load
     * @param cb:      the callback to handle the result
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
     *
     * @param e:  the event to save or update
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

    /**
     * Load events created by a specific organizer, excluding those already cached.
     * <p>
     * Note: The function returns immediately.
     * The result is delivered via the provided callback.
     * Anything relying on the result must be done in the {@link ListCallback onLoaded} methods.
     * </p>
     *
     * @param organizerName: the name of the organizer
     * @param cachedEvents:  the list of already cached events
     * @param cb:            the callback to handle the result
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
                    if (e.getRegistrationOpenAt() == null || e.getRegistrationCloseAt() == null)
                        continue;
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

    public void loadRegisterHistoryEvents(final ListCallback cb) {
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
                            if (thisDeviceId.equals(profileId)) {
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

    /**
     * Delete an event and clean up its subcollections (waitlist + invites).
     *
     * @param event the event to delete (must have its id set)
     * @param cb    callback for completion / error
     */
    public void deleteEvent(Event event, final CompleteCallback cb) {
        if (event == null || event.getId() == null) {
            cb.onError(new IllegalArgumentException("Event or event ID is null"));
            return;
        }

        String eventId = event.getId();
        DocumentReference eventRef = db.collection("events").document(eventId);

        // Load subcollections first
        Task<QuerySnapshot> waitlistTask = eventRef.collection("waitlist").get();
        Task<QuerySnapshot> invitesTask = eventRef.collection("invites").get();

        Tasks.whenAllComplete(waitlistTask, invitesTask)
            .addOnSuccessListener(tasks -> {
                if (!waitlistTask.isSuccessful()) {
                    cb.onError(waitlistTask.getException());
                    return;
                }
                if (!invitesTask.isSuccessful()) {
                    cb.onError(invitesTask.getException());
                    return;
                }

                WriteBatch batch = db.batch();

                // Delete waitlist docs
                for (DocumentSnapshot doc : waitlistTask.getResult().getDocuments()) {
                    batch.delete(doc.getReference());
                }

                // Delete invite docs
                for (DocumentSnapshot doc : invitesTask.getResult().getDocuments()) {
                    batch.delete(doc.getReference());
                }

                // Delete the event document itself
                batch.delete(eventRef);

                batch.commit()
                    .addOnSuccessListener(unused -> cb.onComplete())
                    .addOnFailureListener(cb::onError);
            })
            .addOnFailureListener(cb::onError);
    }

    /**
     * Fetches a list of events from Firestore based on specified filters
     * Method queries the "events" collection and filters the results by date and/or interests
     * <p>
     * If a date is provided (not equal to 0), the query will filter events that start within the specified day
     * The date is converted into a start and end timestamp for the query
     * <p>
     * If a list of selected interests is provided (not null or empty), the query will filter events
     * that have at least one of the specified interests in their "interests" array field.
     * <p>
     *
     * @param date              The date to filter events by, represented as millisecond
     *                          If date = 0, the date filter is not applied (no specific dates chosen)
     * @param selectedInterests A list of strings representing the interests / categories to filter by
     *                          If it is null or empty, the interest filter is not applied
     */
    public void loadFilteredEvents(long date, List<String> selectedInterests, ListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("events");

        Pair<Long, Long> range = DateUtils.getDayRange(date);

        // filtering by dates
        if (date != 0) {
            Timestamp startTimestamp = new Timestamp(new Date(range.first));
            Timestamp endTimestamp = new Timestamp(new Date(range.second));

            // For debugging in logcat
            Log.d("FILTER_DEBUG", "Filtering for Date Range: " + startTimestamp + " to " + endTimestamp);
            if (selectedInterests != null) {
                Log.d("FILTER_DEBUG", "Filtering for Interests: " + selectedInterests);
            } else {
                Log.d("FILTER_DEBUG", "Interests filter is NULL/Empty");
            }

            query = query.whereGreaterThanOrEqualTo("eventStartAt", startTimestamp)
                .whereLessThanOrEqualTo("eventStartAt", endTimestamp);
        }

        // if interest is selected to filter by
        if (selectedInterests != null && !selectedInterests.isEmpty()) {
            query = query.whereArrayContainsAny("interests", selectedInterests);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // for debugging what is returned after querying (check logcat)
            Log.d("FILTER_DEBUG", "Query Finished. Total Documents Found: " + queryDocumentSnapshots.size());

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                // Print the ID and Title of every match found
                Log.d("FILTER_DEBUG", "MATCH: ID=" + doc.getId() + ", Title=" + doc.get("title"));
            }

            // updates the adapter with the list of filtered events
            List<Event> events = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                events.add(doc.toObject(Event.class));
            }
            callback.onLoaded(events);
        }).addOnFailureListener(e -> {
            // for debugging errors from query (check logcat)
            Log.e("FILTER_DEBUG", "Query FAILED: " + e.getMessage());
            callback.onError(e);
        });
    }

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
}
