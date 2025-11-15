package com.example.helloworldproject.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.helloworldproject.BR;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Event model displayed in details screen.
 */
public class Event extends BaseObservable {
    public static final SimpleDateFormat DATE_FORMATTER =
        new SimpleDateFormat("MMM. dd, yyyy", Locale.ENGLISH);

    public static String formatDate(Timestamp ts) {
        if (ts == null) return "NULL";
        return DATE_FORMATTER.format(ts.toDate());
    }

    private String id;
    private Timestamp createdAt;
    private String title;
    private String description;
    private String venue;
    private Timestamp registrationOpenAt;
    private Timestamp registrationCloseAt;
    private Timestamp eventStartAt;
    private Timestamp eventEndAt;
    private Integer capacity;

    // Fields for US 01.05.05 (lottery explanation)
    private String selectionMethod;    // e.g., "FISHER_YATES"
    private String seedPolicy;         // e.g., "RANDOM_LOGGED"
    private String duplicatePolicy;    // e.g., "ONE_ENTRY_PER_PROFILE"
    private Boolean geoRequired;       // true/false
    private Integer plannedSampleSize; // e.g., 20

    public Event() {
    }

    public String getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    @Bindable
    public String getVenue() {
        return venue;
    }

    @Bindable
    public Timestamp getRegistrationOpenAt() {
        return registrationOpenAt;
    }

    @Bindable
    public Timestamp getRegistrationCloseAt() {
        return registrationCloseAt;
    }

    @Bindable
    public Timestamp getEventStartAt() {
        return eventStartAt;
    }

    @Bindable
    public Timestamp getEventEndAt() {
        return eventEndAt;
    }

    @Bindable
    public Integer getCapacity() {
        return capacity;
    }

    public String getSelectionMethod() {
        return selectionMethod;
    }

    public String getSeedPolicy() {
        return seedPolicy;
    }

    public String getDuplicatePolicy() {
        return duplicatePolicy;
    }

    @Bindable
    public Boolean getGeoRequired() {
        return geoRequired;
    }

    @Bindable
    public Integer getPlannedSampleSize() {
        return plannedSampleSize;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    public void setVenue(String venue) {
        this.venue = venue;
        notifyPropertyChanged(BR.venue);
    }

    public void setRegistrationOpenAt(Timestamp registrationOpenAt) {
        this.registrationOpenAt = registrationOpenAt;
        notifyPropertyChanged(BR.registrationOpenAt);
    }

    public void setRegistrationCloseAt(Timestamp registrationCloseAt) {
        this.registrationCloseAt = registrationCloseAt;
        notifyPropertyChanged(BR.registrationCloseAt);
    }

    public void setEventStartAt(Timestamp eventStartAt) {
        this.eventStartAt = eventStartAt;
        notifyPropertyChanged(BR.eventStartAt);
    }
    public void setEventEndAt(Timestamp eventEndAt) {
        this.eventEndAt = eventEndAt;
        notifyPropertyChanged(BR.eventEndAt);
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        notifyPropertyChanged(BR.capacity);
    }

    public void setSelectionMethod(String selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    public void setSeedPolicy(String seedPolicy) {
        this.seedPolicy = seedPolicy;
    }

    public void setDuplicatePolicy(String duplicatePolicy) {
        this.duplicatePolicy = duplicatePolicy;
    }

    public void setGeoRequired(Boolean geoRequired) {
        this.geoRequired = geoRequired;
        notifyPropertyChanged(BR.geoRequired);
    }

    public void setPlannedSampleSize(Integer plannedSampleSize) {
        this.plannedSampleSize = plannedSampleSize;
        notifyPropertyChanged(BR.plannedSampleSize);
    }
}
