package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.databinding.FragHomeEventListBinding;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.activities.event.EventDetailActivity;
import com.example.helloworldproject.ui.activities.event.EventEditingActivity;
import com.example.helloworldproject.ui.activities.event.EventQRCodeScanActivity;
import com.example.helloworldproject.ui.utils.EventCardAdapter;
import com.example.helloworldproject.util.CurrentProfile;
import com.example.helloworldproject.util.EventCache;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeEventCardListFragment extends Fragment {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    ActivityResultLauncher<String> requestCamera = null;
    FragHomeEventListBinding binding;
    ArrayList<Event> eventListBackEnd = new ArrayList<>();
    private EventCardAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_home_event_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (CurrentProfile.isEntrant()) {
             requestCamera = requireActivity().registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(
                            requireContext(),
                            "Camera permission is required to scan event QR codes.",
                            Toast.LENGTH_LONG
                        ).show();
                    } else {
                        startActivity(EventQRCodeScanActivity.newIntent(requireContext()));
                    }
                }
            );
        }

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.home_event_list_menu, menu);
                MenuItem qrScanItem = menu.findItem(R.id.home_scan_button);
                qrScanItem.setVisible(!CurrentProfile.isOrganizer());
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // TODO: implement filter function later
                if (menuItem.getItemId() == R.id.home_filter_button) {

                } else if (menuItem.getItemId() == R.id.home_scan_button) {
                    if (requestCamera == null) {
                        Toast.makeText(
                            requireContext(),
                            "Camera permission is required to scan event QR codes.",
                            Toast.LENGTH_LONG
                        ).show();
                        return true;
                    } else {
                        requestCamera.launch(android.Manifest.permission.CAMERA);
                    }
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        // remove the title text in the top bar
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            if (CurrentProfile.isOrganizer()) {
                actionBar.setTitle("My Events");
            } else {
                actionBar.setTitle("Available Events");
            }
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

        FloatingActionButton addEventBtn = view.findViewById(R.id.add_event_fab);
        if (CurrentProfile.isOrganizer()) {
            addEventBtn.setVisibility(View.VISIBLE);
            addEventBtn.setOnClickListener(v -> {
                startActivity(EventEditingActivity.newIntent(getContext(), null));
            });
        } else {
            addEventBtn.setVisibility(View.GONE);
        }
    }

    private void updateAdapterFrom(List<Event> events) {
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }

    private void refreshEventList() {
        // TODO: add a loading spinner here?
        adapter.clear();
        if (CurrentProfile.isOrganizer()) {
            loadEventsForOrganizer();
        } else {

        }
    }

    /**
     * Load events created by the current organizer and update the adapter.
     */
    private void loadEventsForOrganizer() {
        String organizerName = CurrentProfile.get().getName();
        executor.execute(() -> EventCache.syncTryGetEventsCreatedBy(
            organizerName,
            new EventRepository.ListCallback() {
                @Override
                public void onLoaded(List<Event> events) {
                    // TODO: filter/sort events
                    requireActivity().runOnUiThread(() -> updateAdapterFrom(events));
                }

                @Override
                public void onError(Exception e) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(
                        requireContext(),
                        "Failed to load events created by " + organizerName + ": " + e.getClass().getCanonicalName(),
                        Toast.LENGTH_SHORT
                    ).show());
                }
            }
        ));
    }

    // Below are helper functions for Entrant view
    private void loadJoinableEvents() {
//        EventRepository.INSTANCE.loadJoinableEvents(new EventRepository.ListCallback() {
//            @Override public void onLoaded(List<Event> events) {
//                updateAdapterFrom(events);
//            }
//            @Override public void onError(Exception e) {
//                Toast.makeText(requireContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
