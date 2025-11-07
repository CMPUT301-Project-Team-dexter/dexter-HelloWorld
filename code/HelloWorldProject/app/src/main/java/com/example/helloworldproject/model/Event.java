package com.example.helloworldproject.model;

import com.google.firebase.Timestamp;

/** Event model displayed in details screen. */
public class Event {
    private String id;
    private String title;
    private String description;
    private String venue;
    private Timestamp registrationOpenAt;
    private Timestamp registrationCloseAt;
    private Integer capacity;

    // Fields for US 01.05.05 (lottery explanation)
    private String selectionMethod;    // e.g., "FISHER_YATES"
    private String seedPolicy;         // e.g., "RANDOM_LOGGED"
    private String duplicatePolicy;    // e.g., "ONE_ENTRY_PER_PROFILE"
    private Boolean geoRequired;       // true/false
    private Integer plannedSampleSize; // e.g., 20

    public Event() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVenue() { return venue; }
    public Timestamp getRegistrationOpenAt() { return registrationOpenAt; }
    public Timestamp getRegistrationCloseAt() { return registrationCloseAt; }
    public Integer getCapacity() { return capacity; }

    public String getSelectionMethod() { return selectionMethod; }
    public String getSeedPolicy() { return seedPolicy; }
    public String getDuplicatePolicy() { return duplicatePolicy; }
    public Boolean getGeoRequired() { return geoRequired; }
    public Integer getPlannedSampleSize() { return plannedSampleSize; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setRegistrationOpenAt(Timestamp registrationOpenAt) { this.registrationOpenAt = registrationOpenAt; }
    public void setRegistrationCloseAt(Timestamp registrationCloseAt) { this.registrationCloseAt = registrationCloseAt; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public void setSelectionMethod(String selectionMethod) { this.selectionMethod = selectionMethod; }
    public void setSeedPolicy(String seedPolicy) { this.seedPolicy = seedPolicy; }
    public void setDuplicatePolicy(String duplicatePolicy) { this.duplicatePolicy = duplicatePolicy; }
    public void setGeoRequired(Boolean geoRequired) { this.geoRequired = geoRequired; }
    public void setPlannedSampleSize(Integer plannedSampleSize) { this.plannedSampleSize = plannedSampleSize; }
}
