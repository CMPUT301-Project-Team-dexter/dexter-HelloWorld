package com.example.helloworldproject;

import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.event.LotteryTextBuilder;

import org.junit.Test;

import static org.junit.Assert.*;

public class LotteryTextBuilderTest {

    @Test
    // Tests the text we show when an Event has null fields
    public void build_usesDefaults_whenFieldsNull() {
        Event e = new Event(); // all null
        String msg = LotteryTextBuilder.build(e, 5);

        assertTrue(msg.contains("Candidate pool size (current): 5"));
        assertTrue(msg.contains("Selection method: FISHER_YATES"));
        assertTrue(msg.contains("seed"));
        assertTrue(msg.contains("one waitlist entry per profile"));
        assertTrue(msg.contains("no geofence restriction"));
        assertTrue(msg.contains("Number of slots drawn: set by organizer at draw time"));
    }

    @Test
    // Tests the text when specific fields are set
    public void build_includesGeoAndSampleSize() {
        Event e = new Event();
        e.setGeoRequired(true);
        e.setPlannedSampleSize(20);
        e.setSelectionMethod("FISHER_YATES");
        e.setSeedPolicy("RANDOM_LOGGED");
        e.setDuplicatePolicy("ONE_ENTRY_PER_PROFILE");

        String msg = LotteryTextBuilder.build(e, 42);

        assertTrue(msg.contains("Candidate pool size (current): 42"));
        assertTrue(msg.contains("Number of slots drawn: 20"));
        assertTrue(msg.contains("Geofencing: must be within the required area"));
    }
}
