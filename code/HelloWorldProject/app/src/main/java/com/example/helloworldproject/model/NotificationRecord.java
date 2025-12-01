package com.example.helloworldproject.model;

import com.google.firebase.Timestamp;

/**
 * Simple in-app notification model that is stored under:
 * <p>
 * profiles/{profileId}/notifications/{notificationId}
 * <p>
 * It is used to notify entrants about lottery results
 * (e.g. "you were not chosen for this event").
 */
public class NotificationRecord {

    // Firestore document id (can be null until loaded from Firestore).
    private String id;

    // Type of notification, e.g. "LOTTERY_NOT_CHOSEN".
    private String type;

    // ID of the event related to this notification.
    private String eventId;

    // Human friendly event title for display.
    private String eventTitle;

    // Whether the user has seen or dismissed this notification.
    private boolean read;

    // Server timestamp indicating when this notification was created.
    private Timestamp createdAt;

    public NotificationRecord() {
        // Default constructor required for Firestore
    }

    public NotificationRecord(String id, String type, String eventId,
                              String eventTitle, boolean read, Timestamp createdAt) {
        this.id = id;
        this.type = type;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.read = read;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
