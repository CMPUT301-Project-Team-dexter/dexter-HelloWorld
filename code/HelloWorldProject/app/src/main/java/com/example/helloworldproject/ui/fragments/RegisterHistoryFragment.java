package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.databinding.FragHomeEventListBinding;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.activities.event.EventDetailActivity;
import com.example.helloworldproject.ui.utils.EventCardAdapter;

import java.util.ArrayList;
import java.util.List;

public class RegisterHistoryFragment extends Fragment {
    FragHomeEventListBinding binding;
    ArrayList<Event> eventListBackEnd = new ArrayList<>();
    private EventCardAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_home_event_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);

        // Top bar title
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Register History");
            actionBar.setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(
                v -> requireActivity().getOnBackPressedDispatcher().onBackPressed()
            );
        }

        adapter = new EventCardAdapter(requireContext(), eventListBackEnd);
        binding.eventListView.setAdapter(adapter);
        binding.eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            Event e = adapter.getItem(position);
            if (e != null) {
                startActivity(EventDetailActivity.newIntent(requireActivity(), e.getId()));
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error loading event details: event is null",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        refreshEventList();

        binding.addEventFab.setVisibility(View.GONE);
    }

    private void updateAdapterFrom(List<Event> events) {
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }

    private void refreshEventList() {
        loadJoinedEvents();
    }

    // Entrant view: only joinable events
    private void loadJoinedEvents() {
        EventRepository.INSTANCE.loadRegisterHistoryEvents(
            new EventRepository.ListCallback() {
                @Override
                public void onLoaded(List<Event> events) {
                    updateAdapterFrom(events);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load events: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        );
    }
}
