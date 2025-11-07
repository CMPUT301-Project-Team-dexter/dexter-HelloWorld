package com.example.helloworldproject.ui.entrants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EntrantRepository;
import com.example.helloworldproject.model.Entrant;
import com.google.android.gms.tasks.Tasks;

public class ChosenEntrantsFragment extends Fragment {

    private ChosenEntrantsViewModel vm;
    private ChosenEntrantsAdapter adapter;
    private EntrantRepository repo;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chosen_entrants, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        Toolbar topAppBar = v.findViewById(R.id.topAppBar);
        if (topAppBar != null) {
            topAppBar.setTitle(R.string.chosen_entrants_title);
        }

        RecyclerView rv = v.findViewById(R.id.recyclerChosen);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ChosenEntrantsAdapter();
        rv.setAdapter(adapter);

        repo = new EntrantRepository();
        vm = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                //noinspection unchecked
                return (T) new ChosenEntrantsViewModel(repo);
            }
        }).get(ChosenEntrantsViewModel.class);

        vm.entrants().observe(getViewLifecycleOwner(), adapter::submitList);
        vm.error().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        // click, menu pop up
        adapter.setOnItemClickListener((itemView, entrant) -> showActionsMenu(itemView, entrant));

        String eventId = requireArguments().getString("eventId");
        vm.setEventId(eventId);
    }

    private void showActionsMenu(View anchor, Entrant e) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        pm.getMenu().add("Invite (set PENDING)");
        pm.getMenu().add("Cancel (set CANCELED)");
        pm.getMenu().add("Mark Accepted");

        String eventId = requireArguments().getString("eventId");

        pm.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.startsWith("Invite")) {
                repo.invite(eventId, e.getId())
                        .addOnSuccessListener(unused -> toast("Invited -> PENDING"))
                        .addOnFailureListener(err -> toast("Invite failed: " + err.getMessage()));
            } else if (title.startsWith("Cancel")) {
                repo.cancel(eventId, e.getId())
                        .addOnSuccessListener(unused -> toast("Canceled"))
                        .addOnFailureListener(err -> toast("Cancel failed: " + err.getMessage()));
            } else if (title.startsWith("Mark Accepted")) {
                repo.markAccepted(eventId, e.getId())
                        .addOnSuccessListener(unused -> toast("Marked ACCEPTED"))
                        .addOnFailureListener(err -> toast("Accept failed: " + err.getMessage()));
            }
            return true;
        });
        pm.show();
    }

    private void toast(String s) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
    }
}

