package com.example.helloworldproject.ui.event;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.helloworldproject.model.Event;

/**
 * Builds human-readable lottery guidelines for the details screen.
 */
public class LotteryTextBuilder {

    public static String build(Event e, int currentWaitlistCount) {
        String method = (e.getSelectionMethod() != null) ? e.getSelectionMethod() : "FISHER_YATES";
        String seedPolicy = (e.getSeedPolicy() != null) ? e.getSeedPolicy() : "RANDOM_LOGGED";
        String duplicate = (e.getDuplicatePolicy() != null) ? e.getDuplicatePolicy() : "ONE_ENTRY_PER_PROFILE";
        boolean geo = (e.getGeoRequired() != null) ? e.getGeoRequired() : false;
        String sampleSize = (e.getPlannedSampleSize() != null) ? String.valueOf(e.getPlannedSampleSize()) : "set by organizer at draw time";

        StringBuilder sb = new StringBuilder();
        sb.append("Lottery Rules & Guidelines\n\n");
        sb.append("· Candidate pool size (current): ").append(currentWaitlistCount).append("\n");
        sb.append("· Number of slots drawn: ").append(sampleSize).append("\n");
        sb.append("· Selection method: ").append(method).append(" (fair random shuffle / equal probability)\n");
        sb.append("· Random seed policy: ").append(seedPolicy.equals("RANDOM_LOGGED") ? "seed generated randomly and recorded in draw logs" : seedPolicy).append("\n");
        sb.append("· Deduplication: ").append(duplicate.equals("ONE_ENTRY_PER_PROFILE") ? "one waitlist entry per profile" : duplicate).append("\n");
        sb.append("· Geofencing: ").append(geo ? "must be within the required area when joining the waitlist" : "no geofence restriction").append("\n");
        sb.append("\nNote: The draw happens after registration closes. If an invitee declines or times out, replacements will be drawn from the waitlist.");
        return sb.toString();
    }

    public static void showDialog(Context context, String msg) {
        new AlertDialog.Builder(context)
            .setTitle("Lottery rules")
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show();
    }
}
