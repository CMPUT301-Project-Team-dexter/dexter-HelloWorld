package com.example.helloworldproject.ui.entrants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AcceptedEntrantsFragment extends Fragment {

    private AcceptedEntrantsViewModel vm;
    private ChosenEntrantsAdapter adapter; //

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chosen_entrants, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        Toolbar topAppBar = v.findViewById(R.id.topAppBar);
        if (topAppBar != null) {
            topAppBar.setTitle(R.string.accepted_entrants_title);
        }

        RecyclerView rv = v.findViewById(R.id.recyclerChosen);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ChosenEntrantsAdapter();
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                EntrantRepository repo = new EntrantRepository();
                //noinspection unchecked
                return (T) new AcceptedEntrantsViewModel(repo);
            }
        }).get(AcceptedEntrantsViewModel.class);

        vm.entrants().observe(getViewLifecycleOwner(), adapter::submitList);
        vm.error().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        String eventId = requireArguments().getString("eventId");
        vm.setEventId(eventId);


    }
}

