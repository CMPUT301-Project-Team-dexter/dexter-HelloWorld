package com.example.helloworldproject.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;

import java.util.HashMap;

public class EventCache {
    private static final EventRepository repo = new EventRepository();
    private static final HashMap<String, Event> internalCache = new HashMap<>(64);

    public static void cache(@NonNull Event e) {
        if (!internalCache.containsKey(e.getId())) {
            internalCache.put(e.getId(), e);
        }
    }

    @Nullable
    public static Event tryGet(String eventId, Context context) {
        if (internalCache.containsKey(eventId)) {
            return internalCache.get(eventId);
        }
        final Event[] loadedEvent = {null};
        repo.loadById(eventId, new EventRepository.LoadCallback() {
            @Override
            public void onLoaded(Event e) {
                loadedEvent[0] = e;
            }

            @Override
            public void onNotFound() {  }

            @Override
            public void onError(Exception e) {
                Toast.makeText(
                    context,
                    "Exception occurs when loading event: " + e.getMessage(),
                    Toast.LENGTH_SHORT
                ).show();
            }
        });
        Event result = loadedEvent[0];
        if (result != null) {
            cache(result);
        }
        return result;
    }

    public static boolean tryUpload(@NonNull Event e, Context context) {
        final boolean[] result = {false};
        repo.saveOrUpdate(e, new EventRepository.CompleteCallback() {
            @Override
            public void onComplete() {
                result[0] = true;
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Event upload failed. Cause: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        if (result[0]) {
            cache(e);
        }
        return result[0];
    }
}
