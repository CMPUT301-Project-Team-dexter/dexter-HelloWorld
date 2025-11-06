package com.example.helloworldproject.model;

import java.util.ArrayList;
import java.util.List;

public enum AttendeeState {
    ACCEPTED, DECLINED, CANCELLED;

    private static final ArrayList<String> nameCache = new ArrayList<>();

    public static List<String> getNameList() {
        if (nameCache.isEmpty()) {
            for (UserGroup p : UserGroup.values()) {
                nameCache.add(p.name());
            }
        }
        return nameCache;
    }
}
