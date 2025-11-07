package com.example.helloworldproject.ui.entrants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Entrant;

public class ChosenEntrantsAdapter extends ListAdapter<Entrant, ChosenEntrantsAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(View itemView, Entrant entrant);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    public ChosenEntrantsAdapter() { super(DIFF); }

    private static final DiffUtil.ItemCallback<Entrant> DIFF =
            new DiffUtil.ItemCallback<Entrant>() {
                @Override public boolean areItemsTheSame(@NonNull Entrant o, @NonNull Entrant n) {
                    String a = o.getId(), b = n.getId();
                    return (a == b) || (a != null && a.equals(b));
                }
                @Override public boolean areContentsTheSame(@NonNull Entrant o, @NonNull Entrant n) {
                    return safeEq(o.getName(), n.getName())
                            && safeEq(o.getCode(), n.getCode())
                            && safeEq(o.getStatus(), n.getStatus());
                }
                private boolean safeEq(Object a, Object b) { return (a == b) || (a != null && a.equals(b)); }
            };

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Entrant e = getItem(pos);
        h.name.setText(e.getName());
        h.code.setText(e.getCode());
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(v, e);
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, code;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvEntrantName);
            code = itemView.findViewById(R.id.tvEntrantCode);
        }
    }
}
