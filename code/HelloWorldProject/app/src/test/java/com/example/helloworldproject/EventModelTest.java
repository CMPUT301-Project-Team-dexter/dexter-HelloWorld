package com.example.helloworldproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.helloworldproject.model.Event;
import com.google.firebase.Timestamp;

import org.junit.Test;

public class EventModelTest {

    @Test
    public void settersAndGetters_work() {
        Event e = new Event();

        e.setId("evt1");
        e.setTitle("Swim Lessons");
        e.setDescription("Beginners class");
        e.setVenue("Rec Centre");
        Timestamp open = new Timestamp(1, 0);
        Timestamp close = new Timestamp(2, 0);
        e.setRegistrationOpenAt(open);
        e.setRegistrationCloseAt(close);
        e.setCapacity(20);

        e.setSelectionMethod("FISHER_YATES");
        e.setSeedPolicy("RANDOM_LOGGED");
        e.setDuplicatePolicy("ONE_ENTRY_PER_PROFILE");
        e.setGeoRequired(Boolean.TRUE);
        e.setPlannedSampleSize(10);

        assertEquals("evt1", e.getId());
        assertEquals("Swim Lessons", e.getTitle());
        assertEquals("Beginners class", e.getDescription());
        assertEquals("Rec Centre", e.getVenue());
        assertEquals(open, e.getRegistrationOpenAt());
        assertEquals(close, e.getRegistrationCloseAt());
        assertEquals(Integer.valueOf(20), e.getCapacity());

        assertEquals("FISHER_YATES", e.getSelectionMethod());
        assertEquals("RANDOM_LOGGED", e.getSeedPolicy());
        assertEquals("ONE_ENTRY_PER_PROFILE", e.getDuplicatePolicy());
        assertTrue(e.getGeoRequired());
        assertEquals(Integer.valueOf(10), e.getPlannedSampleSize());
    }
}
