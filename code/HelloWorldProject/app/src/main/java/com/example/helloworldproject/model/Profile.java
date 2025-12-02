package com.example.helloworldproject.model;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Profile implements Serializable {
    private String id;        // document ID = deviceId
    private String deviceId;  // device identifier
    private String name;
    private String email;
    private String phone;     // optional
    private UserGroup userGroup;

    // region setting
    private Boolean notificationOptOut; // reserved for notification opt-out
    // endregion

    /**
     * No-arg constructor required by Firestore.
     */
    public Profile() {
    }

    public Profile(String id, String deviceId, String name, String email, String phone, UserGroup userGroup) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.notificationOptOut = Boolean.FALSE;
        this.userGroup = userGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Boolean getNotificationOptOut() {
        return notificationOptOut;
    }

    public void setNotificationOptOut(Boolean notificationOptOut) {
        this.notificationOptOut = notificationOptOut;
    }

    /**
     * Create a blank event with default settings.
     *
     * @return a new Event object with default values
     */
    public Event createBlankEvent() {
        Event e = new Event();
        Ulid u = UlidCreator.getMonotonicUlid();
        e.setId(u.toString());
        e.setCreatedAt(Timestamp.now());
        e.setCreator(name);
        // TODO: decide things regarding to sample methods
        e.setSelectionMethod("FISHER_YATES");
        e.setSeedPolicy("RANDOM_LOGGED");
        e.setDuplicatePolicy("ONE_ENTRY_PER_PROFILE");
        return e;
    }
}
