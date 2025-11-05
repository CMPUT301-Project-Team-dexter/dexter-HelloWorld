package com.example.helloworldproject.model;

import java.io.Serializable;

public class Profile implements Serializable {
    private String id;        // document ID = deviceId
    private String deviceId;  // device identifier
    private String name;
    private String email;
    private String phone;     // optional
    private Boolean notificationOptOut; // reserved for notification opt-out

    /** No-arg constructor required by Firestore. */
    public Profile() {}

    public Profile(String id, String deviceId, String name, String email, String phone) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.notificationOptOut = Boolean.FALSE;
    }

    public String getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Boolean getNotificationOptOut() { return notificationOptOut; }

    public void setId(String id) { this.id = id; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setNotificationOptOut(Boolean notificationOptOut) { this.notificationOptOut = notificationOptOut; }
}
