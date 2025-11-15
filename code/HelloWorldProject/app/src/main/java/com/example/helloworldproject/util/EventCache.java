package com.example.helloworldproject.util;

import androidx.annotation.NonNull;

import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class EventCache {
    private static final HashMap<String, Event> internalCache = new HashMap<>(64);

    /**
     * Cache the given event locally.
     * @param e: the event to be cached
     */
    public static void cache(@NonNull Event e) {
        if (!internalCache.containsKey(e.getId())) {
            internalCache.put(e.getId(), e);
        }
    }

    /**
     * Try to get a single event by its ID. If cached, return from cache immediately.
     *
     * @param eventId: the ID of the event
     * @param cb:      the callback to handle the result
     */
    public static void tryGetSingle(String eventId, EventRepository.LoadCallback cb) {
        if (internalCache.containsKey(eventId)) {
            cb.onLoaded(internalCache.get(eventId));
        }
        EventRepository.INSTANCE.loadById(eventId, cb);
    }

    /**
     * Try to get events created by a specific organizer.
     * Always attempt to load uncached events from the repository.
     *
     * @param organizerName: the name of the organizer
     * @param cb:            the callback to handle the result
     */
    public static void tryGetEventsCreatedBy(String organizerName, EventRepository.ListCallback cb) {
        ArrayList<Event> cachedEvents = internalCache.values().stream()
            .filter(e -> organizerName.equals(e.getCreator()))
            .collect(Collectors.toCollection(ArrayList::new));
        EventRepository.INSTANCE.loadUncachedEventsCreatedBy(organizerName, cachedEvents, cb);
    }

    /**
     * Try to upload a single event to the repository.
     * @param e: the event to be uploaded
     * @param cb: the callback to handle completion
     */
    public static void tryUploadSingle(@NonNull Event e, EventRepository.CompleteCallback cb) {
        EventRepository.INSTANCE.saveOrUpdate(e, cb);
    }
}
