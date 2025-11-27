package com.example.helloworldproject.model;

/**
 * Simple data model representing a single invitation sent to an entrant
 * for a specific event.
 *
 * <p>This class mirrors the structure of documents stored under:</p>
 *
 * <pre>
 *     events/{eventId}/invites/{profileId}
 * </pre>
 *
 * where each document typically contains fields such as:
 * profileId, name, email, phone, status, and invitedAt.
 */
public class InvitationRecord {

    // Unique id of the profile that was invited (same as the Firestore document id).
    private String profileId;

    // Display name of the invited entrant.
    private String name;

    // Short code or username used in the UI.
    private String code;

    // Raw invitation status from Firestore: "PENDING", "ACCEPTED", "DECLINED", "CANCELLED", ...
    private String status;

    public InvitationRecord() {
        // Default empty constructor required for Firestore / data binding.
    }

    public InvitationRecord(String profileId, String name, String code, String status) {
        this.profileId = profileId;
        this.name = name;
        this.code = code;
        this.status = status;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * A short identifier shown on the right side of the row.
     * This can be the same as the profile id or any other user code.
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Current invitation status value exactly as stored in Firestore.
     * Typical values: "PENDING", "ACCEPTED", "DECLINED", "CANCELLED".
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
