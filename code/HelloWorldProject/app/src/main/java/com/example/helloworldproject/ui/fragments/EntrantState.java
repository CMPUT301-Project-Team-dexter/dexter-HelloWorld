package com.example.helloworldproject.ui.fragments;

import com.example.helloworldproject.model.UserGroup;

import java.util.ArrayList;
import java.util.List;

public enum EntrantState
{
    UNRELATED, WAITLISTED, INVITED, CANCELLED, ACCEPTED;

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
