package com.example.helloworldproject.ui.utils;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.InvitationRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for the "Invitation History" screen.
 *
 * <p>Each row shows:</p>
 * <ul>
 *     <li>The entrant's display name</li>
 *     <li>A short code / id</li>
 *     <li>The invitation status, colour coded</li>
 * </ul>
 */
public class InvitationHistoryAdapter extends RecyclerView.Adapter<InvitationHistoryAdapter.ViewHolder> {

    // In-memory list of invitations for the current event.
    private final List<InvitationRecord> invitations = new ArrayList<>();

    /**
     * Replace the current invitation list with a new one from Firestore.
     *
     * @param newInvitations Most recent snapshot of invitations for the event.
     */
    public void setInvitations(List<InvitationRecord> newInvitations) {
        invitations.clear();
        if (newInvitations != null) {
            invitations.addAll(newInvitations);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_invitation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        InvitationRecord record = invitations.get(position);

        // Safely extract values with sensible fallbacks.
        String name = record.getName() != null ? record.getName() : "";
        String code = record.getCode() != null ? record.getCode() : "";
        String status = record.getStatus() != null ? record.getStatus() : "PENDING";

        holder.nameView.setText(name);
        holder.codeView.setText(code);

        // Map the raw Firestore status values to user-friendly labels and colours.
        String displayStatus;
        int color;

        switch (status) {
            case "ACCEPTED":
                displayStatus = "Accepted";
                color = Color.parseColor("#4CAF50"); // green
                break;
            case "DECLINED":
                displayStatus = "Rejected";
                color = Color.parseColor("#F44336"); // red
                break;
            case "CANCELLED":
                displayStatus = "Cancelled";
                color = Color.parseColor("#F44336"); // red
                break;
            default:
                displayStatus = "Pending";
                color = Color.parseColor("#9E9E9E"); // grey
                break;
        }

        holder.statusView.setText(displayStatus);
        holder.statusView.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    /**
     * Simple ViewHolder that stores references to the TextViews in item_invitation.xml.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView nameView;
        final TextView codeView;
        final TextView statusView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tvName);
            codeView = itemView.findViewById(R.id.tvCode);
            statusView = itemView.findViewById(R.id.tvStatus);
        }
    }
}
