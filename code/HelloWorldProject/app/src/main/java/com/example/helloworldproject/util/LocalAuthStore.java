package com.example.helloworldproject.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Small helper class that stores the currently selected profile id
 * in SharedPreferences for auto-login.
 *
 * <p>
 * This does not perform any network or Firestore operations. It simply
 * remembers which profile should be used on the next app launch.
 * </p>
 */
public class LocalAuthStore {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_PROFILE_ID = "profile_id";

    /**
     * Save the profile id for auto-login on this device.
     *
     * @param context   Any context, for example an Activity.
     * @param profileId The id of the profile that should be restored next time.
     */
    public static void saveProfileId(@NonNull Context context, @NonNull String profileId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_PROFILE_ID, profileId)
                .apply();
    }

    /**
     * Load the profile id that was previously saved for auto-login.
     *
     * @param context Any context, for example an Activity.
     * @return The saved profile id, or {@code null} if none is stored.
     */
    @Nullable
    public static String getSavedProfileId(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PROFILE_ID, null);
    }

    /**
     * Remove any stored profile id from local storage.
     *
     * <p>
     * After calling this method, the app will no longer auto-login using
     * the saved profile id on this device. It may still use "login by device"
     * logic based on Firebase Installations.
     * </p>
     *
     * @param context Any context, for example an Activity.
     */
    public static void clear(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_PROFILE_ID)
                .apply();
    }
}
