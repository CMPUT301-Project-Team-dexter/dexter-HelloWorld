package com.example.helloworldproject.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AttendeeList implements Serializable {
    Map<Profile, AttendeeState> map;

    public AttendeeList() {
        this.map = new HashMap<>();
    }

    public boolean addAttendee(Profile entrant, AttendeeState state) {
        if (!map.containsKey(entrant)) {
            map.put(entrant, state);
            return true;
        }
        return false;
    }

    public boolean updateAttendee(Profile entrant, AttendeeState state) {
        if (map.containsKey(entrant)) {
            map.put(entrant, state);
            return true;
        }
        return false;
    }

    public List<Profile> getAcceptedEntrants() {
        List<Profile> acceptedEntrants = new LinkedList<>();

        for (Map.Entry<Profile, AttendeeState> entry : map.entrySet()) {
            if (entry.getValue().equals(AttendeeState.ACCEPTED)) {
                acceptedEntrants.add(entry.getKey());
            }
        }
        return acceptedEntrants;
    }

    public boolean contains(Profile profile) {
        return map.containsKey(profile);
    }
}
