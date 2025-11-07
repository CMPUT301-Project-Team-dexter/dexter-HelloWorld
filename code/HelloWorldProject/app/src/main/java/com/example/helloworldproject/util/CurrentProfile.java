package com.example.helloworldproject.util;

import androidx.annotation.NonNull;

import com.example.helloworldproject.model.Profile;

public class CurrentProfile {
    private CurrentProfile() {  }

    private static Profile currentUser = null;

    public static void init(@NonNull Profile p) {
        currentUser = p;
    }

    @NonNull
    public static Profile get() {
        return currentUser;
    }
}
