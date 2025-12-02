package com.example.helloworldproject.util;

import com.google.firebase.installations.FirebaseInstallations;

/**
 * Utility class to get the device's Firebase Installation ID.
 */
public class DeviceId {

    /**
     * Get the Firebase Installation ID asynchronously.
     *
     * @param callback: Callback to receive the device ID or error.
     */
    public static void get(final DeviceIdCallback callback) {
        FirebaseInstallations.getInstance().getId()
            .addOnSuccessListener(callback::onSuccess)
            .addOnFailureListener(callback::onError);
    }

    public interface DeviceIdCallback {
        void onSuccess(String deviceId);

        void onError(Exception e);
    }
}