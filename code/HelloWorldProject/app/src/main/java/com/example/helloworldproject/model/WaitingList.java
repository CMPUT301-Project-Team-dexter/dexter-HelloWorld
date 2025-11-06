package com.example.helloworldproject.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WaitingList implements Serializable {
    List<Profile> waitingList;
    Integer limit;

    public WaitingList() {
        this.waitingList = new LinkedList<>();
        this.limit = null;
    }

    public WaitingList(List<Profile> waitingList) {
        this.waitingList = waitingList;
        this.limit = null;
    }

    /**
     *
     * @param entrant
     * @return true if the addition is successful; false if not.
     */
    public boolean removeEntrant(Profile entrant) {
        boolean result = waitingList.remove(entrant);
        return result;
    }

    /**
     *
     * @param entrant
     * @return true if the addition is successful; false if not.
     */
    public boolean addEntrant(Profile entrant) {
        if (limit != null && this.getSize() >= limit) {
            return false;
        }
        waitingList.add(entrant);
        return true;
    }

    public List<Profile> getWaitingList() {
        return waitingList;
    }

    public int getSize() {
        return waitingList.size();
    }

    /**
     *
     * @param limit
     * @return true if the addition is successful; false if not.
     */
    public boolean imposeLimit(int limit) {
        if (limit < this.getSize()) {
            return false;
        }
        this.limit = limit;
        return true;
    }

    public boolean constains(Profile profile) {
        return waitingList.contains(profile);
    }
}
