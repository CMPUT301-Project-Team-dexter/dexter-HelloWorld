package com.example.helloworldproject.model;

import android.graphics.Bitmap;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.helloworldproject.BR;
import com.example.helloworldproject.util.QRCodeUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Event model displayed in details screen.
 */
public class Event extends BaseObservable {
    public static final SimpleDateFormat DATE_FORMATTER =
        new SimpleDateFormat("MMM. dd, yyyy", Locale.ENGLISH);
    private String id;
    private Timestamp createdAt;
    private String creator;
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
    private List<String> interests; // e.g., ["Running", "Swimming"]
    private String imgId;
    private String imgUrl;
    private Boolean imgUrlEnable;
    @Exclude
    private Bitmap qrCodeBitmap = null;

    public Event() {
    }

    /**
     * Copy constructor.
     *
     * @param other the event to copy
     */
    public Event(Event other) {
        this.id = other.id;
        this.createdAt = other.createdAt;
        this.creator = other.creator;
        this.title = other.title;
        this.description = other.description;
        this.venue = other.venue;
        this.registrationOpenAt = other.registrationOpenAt;
        this.registrationCloseAt = other.registrationCloseAt;
        this.eventStartAt = other.eventStartAt;
        this.eventEndAt = other.eventEndAt;
        this.capacity = other.capacity;
        this.selectionMethod = other.selectionMethod;
        this.seedPolicy = other.seedPolicy;
        this.duplicatePolicy = other.duplicatePolicy;
        this.geoRequired = other.geoRequired;
        this.plannedSampleSize = other.plannedSampleSize;
        if (other.interests == null) {
            this.interests = null;
        } else {
            this.interests = new ArrayList<>();
            this.interests.addAll(other.interests);
        }
        this.imgId = other.imgId;
        this.imgUrl = other.imgUrl;
        this.imgUrlEnable = other.imgUrlEnable;
        this.qrCodeBitmap = null;
    }

    public static String formatDate(Timestamp ts) {
        if (ts == null) return "NULL";
        return DATE_FORMATTER.format(ts.toDate());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Get the real-time status of the event based on current time.
     *
     * @return the EventStatus enum representing the current status
     */
    @Exclude
    public EventStatus getRealTimeStatus() {
        Timestamp now = Timestamp.now();

        Timestamp registrationOpenAt = getRegistrationOpenAt();
        Timestamp registrationCloseAt = getRegistrationCloseAt();
        Timestamp eventStartAt = getEventStartAt();
        Timestamp eventEndAt = getEventEndAt();

        // 🔒 Null-safety for older / incomplete events:
        // If any of the key timestamps are missing, don't try to compare them.
        // Just fall back to a safe default so the UI can still render.
        if (registrationOpenAt == null ||
            registrationCloseAt == null ||
            eventStartAt == null ||
            eventEndAt == null) {

            // Using NOT_OPEN is safe for admin views.
            return EventStatus.NOT_OPEN;
        }

        if (now.compareTo(registrationOpenAt) < 0) {
            return EventStatus.NOT_OPEN;
        } else if (now.compareTo(registrationCloseAt) <= 0) {
            return EventStatus.REGISTRATION_OPEN;
        } else if (now.compareTo(eventStartAt) < 0) {
            return EventStatus.REGISTRATION_CLOSED;
        } else if (now.compareTo(eventEndAt) <= 0) {
            return EventStatus.ONGOING;
        } else {
            return EventStatus.ENDED;
        }
    }

    /**
     * Get the QR code bitmap representing the event ID.
     *
     * @return Bitmap of the QR code
     */
    @Exclude
    public Bitmap getQRCodeBitmap() {
        if (qrCodeBitmap == null) {
            qrCodeBitmap = QRCodeUtils.generate(id, 512);
        }
        return qrCodeBitmap;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
        notifyPropertyChanged(BR.venue);
    }

    @Bindable
    public Timestamp getRegistrationOpenAt() {
        return registrationOpenAt;
    }

    public void setRegistrationOpenAt(Timestamp registrationOpenAt) {
        this.registrationOpenAt = registrationOpenAt;
        notifyPropertyChanged(BR.registrationOpenAt);
    }

    @Bindable
    public Timestamp getRegistrationCloseAt() {
        return registrationCloseAt;
    }

    public void setRegistrationCloseAt(Timestamp registrationCloseAt) {
        this.registrationCloseAt = registrationCloseAt;
        notifyPropertyChanged(BR.registrationCloseAt);
    }

    @Bindable
    public Timestamp getEventStartAt() {
        return eventStartAt;
    }

    public void setEventStartAt(Timestamp eventStartAt) {
        this.eventStartAt = eventStartAt;
        notifyPropertyChanged(BR.eventStartAt);
    }

    @Bindable
    public Timestamp getEventEndAt() {
        return eventEndAt;
    }

    public void setEventEndAt(Timestamp eventEndAt) {
        this.eventEndAt = eventEndAt;
        notifyPropertyChanged(BR.eventEndAt);
    }

    @Bindable
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        notifyPropertyChanged(BR.capacity);
    }

    public String getSelectionMethod() {
        return selectionMethod;
    }

    public void setSelectionMethod(String selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    public String getSeedPolicy() {
        return seedPolicy;
    }

    public void setSeedPolicy(String seedPolicy) {
        this.seedPolicy = seedPolicy;
    }

    public String getDuplicatePolicy() {
        return duplicatePolicy;
    }

    public void setDuplicatePolicy(String duplicatePolicy) {
        this.duplicatePolicy = duplicatePolicy;
    }

    @Bindable
    public Boolean getGeoRequired() {
        return geoRequired;
    }

    public void setGeoRequired(Boolean geoRequired) {
        this.geoRequired = geoRequired;
        notifyPropertyChanged(BR.geoRequired);
    }

    @Bindable
    public Integer getPlannedSampleSize() {
        return plannedSampleSize;
    }

    public void setPlannedSampleSize(Integer plannedSampleSize) {
        this.plannedSampleSize = plannedSampleSize;
        notifyPropertyChanged(BR.plannedSampleSize);
    }

    @Bindable
    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
        notifyPropertyChanged(BR.interests);
    }

    @Bindable
    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    @Bindable
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Bindable
    public Boolean getImgUrlEnable() {
        return imgUrlEnable;
    }

    public void setImgUrlEnable(Boolean imgUrlEnable) {
        this.imgUrlEnable = imgUrlEnable;
    }
}
