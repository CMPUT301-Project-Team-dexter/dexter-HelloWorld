package com.example.helloworldproject.util;

import androidx.annotation.NonNull;

import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * <p>
     * There is no need to cache the result in the callback.
     * <p>
     * <p>
     *     Note: The function returns immediately.
     *     The result is delivered via the provided callback.
     *     Anything relying on the result must be done in the
     *     {@link EventRepository.LoadCallback onLoaded} methods.
     * </p>
     *
     * @param eventId: the ID of the event
     * @param cb:      the callback to handle the result
     */
    public static void asyncTryGetSingle(String eventId, EventRepository.LoadCallback cb) {
        if (internalCache.containsKey(eventId)) {
            cb.onLoaded(internalCache.get(eventId));
            return;
        }
        EventRepository.INSTANCE.asyncLoadById(
            eventId,
            new EventRepository.LoadCallback() {
                @Override
                public void onLoaded(Event e) {
                    cache(e);
                    cb.onLoaded(e);
                }

                @Override
                public void onNotFound() {
                    cb.onNotFound();
                }

                @Override
                public void onError(Exception e) {
                    cb.onError(e);
                }
            }
        );
    }

    /**
     * Try to get events created by a specific organizer.
     * Always attempt to load uncached events from the repository.
     * <p>
     * There is no need to cache results in the callback.
     * <p>
     * <p>
     *     Note: The function returns immediately.
     *     The result is delivered via the provided callback.
     *     Anything relying on the result must be done in the
     *     {@link EventRepository.LoadCallback onLoaded} methods.
     * </p>
     *
     * @param organizerName: the name of the organizer
     * @param cb:            the callback to handle the result
     */
    public static void asyncTryGetEventsCreatedBy(String organizerName, EventRepository.ListCallback cb) {
        ArrayList<Event> cachedEvents = internalCache.values().stream()
            .filter(e -> organizerName.equals(e.getCreator()))
            .collect(Collectors.toCollection(ArrayList::new));
        EventRepository.INSTANCE.asyncLoadUncachedEventsCreatedBy(
            organizerName,
            cachedEvents,
            new EventRepository.ListCallback() {
                @Override
                public void onLoaded(List<Event> events) {
                    events.forEach(EventCache::cache);
                    cachedEvents.addAll(events);
                    cb.onLoaded(cachedEvents);
                }

                @Override
                public void onError(Exception e) {
                    cb.onError(e);
                }
            }
        );
    }
}
