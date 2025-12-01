package com.example.helloworldproject.model;

public enum EntrantStatus {
    REJECTED,
    CANCELED,
    PENDING,
    ACCEPTED;

    public static EntrantStatus from(String s) {
        if (s == null) return PENDING;
        try {
            return EntrantStatus.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }

    public String display() {
        switch (this) {
            case REJECTED:
                return "Rejected";
            case CANCELED:
                return "Canceled";
            case ACCEPTED:
                return "Accepted";
            default:
                return "Pending";
        }
    }
}
