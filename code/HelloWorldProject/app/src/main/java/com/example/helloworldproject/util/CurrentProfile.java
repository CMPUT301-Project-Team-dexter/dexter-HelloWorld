package com.example.helloworldproject.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.model.UserGroup;

public class CurrentProfile {
    private CurrentProfile() {  }

    private static Profile currentUser = null;

    public static boolean isInitialized() {
        return currentUser != null;
    }

    public static void init(@NonNull Profile p) {
        currentUser = p;
    }

    @Nullable
    public static Profile get() {
        return currentUser;
    }

    public static boolean isOrganizer() {
        return currentUser != null && currentUser.getUserGroup() == UserGroup.ORGANIZER;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getUserGroup() == UserGroup.ADMIN;
    }

    public static boolean isEntrant() {
        return currentUser != null && currentUser.getUserGroup() == UserGroup.ENTRANT;
    }
}
