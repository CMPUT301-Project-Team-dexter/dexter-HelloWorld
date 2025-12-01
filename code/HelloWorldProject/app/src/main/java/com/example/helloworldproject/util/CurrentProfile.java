package com.example.helloworldproject.util;

import androidx.annotation.NonNull;

import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.model.UserGroup;

public class CurrentProfile {
    private static Profile currentUser = null;

    private CurrentProfile() {
    }

    public static boolean isInitialized() {
        return currentUser != null;
    }

    public static void init(@NonNull Profile p) {
        currentUser = p;
    }

    @NonNull
    public static Profile get() {
        return currentUser;
    }

    public static boolean isOrganizer() {
        return currentUser.getUserGroup() == UserGroup.ORGANIZER;
    }

    public static boolean isAdmin() {
        return currentUser.getUserGroup() == UserGroup.ADMIN;
    }

    public static boolean isEntrant() {
        return currentUser.getUserGroup() == UserGroup.ENTRANT;
    }
}
