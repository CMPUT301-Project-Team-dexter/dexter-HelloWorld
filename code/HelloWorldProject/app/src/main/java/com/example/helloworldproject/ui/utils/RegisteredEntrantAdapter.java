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

public class RegisteredEntrantAdapter extends RecyclerView.Adapter<RegisteredEntrantAdapter.ViewHolder> {

    private List<Profile> entrants = new ArrayList<>();

    public void setEntrants(List<Profile> newEntrants) {
        this.entrants = newEntrants;
        notifyDataSetChanged();
    }

    public void addEntrant(Profile p) {
        this.entrants.add(p);
        notifyItemInserted(this.entrants.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile p = entrants.get(position);
        holder.nameView.setText(p.getName());
        holder.emailView.setText(p.getEmail());
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, emailView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.text_entrant_name);
            emailView = itemView.findViewById(R.id.text_entrant_email);
        }
    }

    public List<Profile> getEntrants() {
        return entrants;
    }
}