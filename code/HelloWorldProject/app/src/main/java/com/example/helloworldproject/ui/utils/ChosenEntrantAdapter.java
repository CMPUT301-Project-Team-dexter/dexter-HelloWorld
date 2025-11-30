package com.example.helloworldproject.ui.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter used by the "Chosen Entrants" screen.
 *
 * <p>
 * It displays a simple two-line row for each entrant:
 * - Name (bold) on the left
 * - A short code / id on the right
 * </p>
 *
 * <p>
 * Additionally, this adapter exposes a callback for long-click actions on rows
 * so that the Activity can implement "cancel entrant" behaviour.
 * </p>
 */
public class ChosenEntrantAdapter extends RecyclerView.Adapter<ChosenEntrantAdapter.ViewHolder> {

    /**
     * Listener interface that allows the host Activity/Fragment to react
     * to user actions on a specific entrant row.
     */
    public interface OnEntrantActionListener {
        /**
         * Called when the user performs a long-click on a chosen entrant row.
         * Typical usage is to ask the organizer if they want to cancel this entrant.
         *
         * @param entrant The corresponding {@link Profile} object.
         */
        void onEntrantLongClick(Profile entrant);
    }

    // In-memory list of accepted entrants for the current event.
    private final List<Profile> entrants = new ArrayList<>();

    // Optional action listener for row events (long-click to cancel).
    private final OnEntrantActionListener actionListener;

    /**
     * Construct a new adapter.
     *
     * @param listener Optional listener for row long-clicks. May be {@code null}
     *                 if no special actions are needed.
     */
    public ChosenEntrantAdapter(OnEntrantActionListener listener) {
        this.actionListener = listener;
    }

    /**
     * Replace the current list of entrants and refresh the UI.
     *
     * @param newEntrants The latest list of accepted entrants from Firestore.
     */
    public void setEntrants(List<Profile> newEntrants) {
        entrants.clear();
        if (newEntrants != null) {
            entrants.addAll(newEntrants);
        }
        // Tell RecyclerView to redraw itself with the new data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent,
        int viewType
    ) {
        // Inflate a single row from item_entrant.xml
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
        @NonNull ViewHolder holder,
        int position
    ) {
        // Get the Profile object at the given position.
        Profile p = entrants.get(position);

        // Safely read name and id, falling back to empty strings if null.
        String name = p.getName() != null ? p.getName() : "";
        String code = p.getId() != null ? p.getId() : "";

        holder.nameView.setText(name);
        holder.codeView.setText(code);

        // Attach a long-click listener to the entire row.
        // When triggered, forward the event to the actionListener (if any).
        holder.itemView.setOnLongClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEntrantLongClick(p);
            }
            // Returning true means we have handled the long-click event.
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    /**
     * Simple ViewHolder that caches references to the TextViews in item_entrant.xml.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final TextView codeView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tvEntrantName);
            codeView = itemView.findViewById(R.id.tvEntrantCode);
        }
    }
}
