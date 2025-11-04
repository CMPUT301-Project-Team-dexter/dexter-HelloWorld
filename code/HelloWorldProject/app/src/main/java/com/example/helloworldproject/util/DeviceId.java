package com.example.helloworldproject.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;

import androidx.annotation.NonNull;

/** Helper to retrieve the Firebase Installation ID for device identification. */
public class DeviceId {

    public interface DeviceIdCallback {
        void onSuccess(String deviceId);
        void onError(Exception e);
    }

    public static void getOrFetch(Context context, final DeviceIdCallback callback) {
        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess(task.getResult());
                        } else {
                            Exception e = task.getException();
                            Log.e("DeviceId", "Failed to get installation id", e);
                            callback.onError(e);
                        }
                    }
                });
    }
}