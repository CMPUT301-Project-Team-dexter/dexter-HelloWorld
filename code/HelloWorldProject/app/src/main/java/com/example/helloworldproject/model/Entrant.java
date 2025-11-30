package com.example.helloworldproject.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Entrant {

    @DocumentId
    private String id;

    private String eventId;    //
    private String name;       //
    private String code;       //
    private String status;     // "PENDING" | "ACCEPTED" | "REJECTED" | "CANCELED"
    private Boolean isChosen;  // chosen or not
    private Long createdAt;    //
    private Long updatedAt;    //

    public Entrant() {
    }

    // ===== getters / setters =====
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsChosen() {
        return isChosen;
    }

    public void setIsChosen(Boolean isChosen) {
        this.isChosen = isChosen;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    //
    public EntrantStatus statusEnum() {
        return EntrantStatus.from(status);
    }
}
