package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.helloworldproject.ui.activities.EventEditingActivity;
import com.example.helloworldproject.ui.utils.EventCardAdapter;
import com.example.helloworldproject.ui.utils.EventCardView;
import com.example.helloworldproject.R;
import com.example.helloworldproject.util.CurrentProfile;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;

import java.text.DateFormat;
import java.util.Date;


public class HomeEventCardListFragment extends Fragment {
    private EventRepository eventRepo;
    private EventCardAdapter adapter;
    private final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    private ListView listView;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // This part is to minimally associate menu/filter_menu.xml with register_history_fragment.xml.
        // So that the menu on this page can function properly.
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.filter_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);


        // This part is to remove the title text in the top bar
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }



        // TODO: update event list according to user group of the current logged-in user
        // TODO: e.g. for an ORGANIZER, show the list of events that are created by the organizer
        // ENTRANT path only for now, still need to add organizer/admin later
        listView = view.findViewById(R.id.listview);
        adapter = new EventCardAdapter(requireContext(), new ArrayList<>());
        listView.setAdapter(adapter);
        eventRepo = new EventRepository();
        // Uncomment the dummy list for test
        //items.add(new EventCardView("Event 1", "Archive", R.drawable.debug_card_image));
        //items.add(new EventCardView("Event 2", "In Progress\n Archive", R.drawable.debug_card_image2));
        //items.add(new EventCardView("Event 3", "Jover", R.drawable.debug_card_image2));
        //items.add(new EventCardView("Event 67", "123", R.drawable.debug_card_image));
        loadJoinableEvents();



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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_event_list, container, false);
        listView = view.findViewById(R.id.listview);

        return view;
    }
    // Below are helper functions for Entrant view
    private void loadJoinableEvents() {
        eventRepo.loadJoinableEvents(new EventRepository.ListCallback() {
            @Override public void onLoaded(List<Event> events) {
                updateAdapterFrom(events);
            }
            @Override public void onError(Exception e) {
                Toast.makeText(requireContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateAdapterFrom(List<Event> events) {
        List<EventCardView> items = new ArrayList<>();
        if (events != null) {
            for (Event e : events) {
                String title = safe(e.getTitle());
                String status = computeStatus(e);   // “Upcoming / Open / Closed” with dates
                int img = R.drawable.debug_card_image; // placeholder poster for now
                items.add(new EventCardView(title, status, img));
            }
        }
        adapter = new EventCardAdapter(requireContext(), items);
        listView.setAdapter(adapter);
    }

    private String computeStatus(Event e) {
        if (e.getRegistrationOpenAt() == null || e.getRegistrationCloseAt() == null) return "";
        long now = System.currentTimeMillis();
        long open = e.getRegistrationOpenAt().toDate().getTime();
        long close = e.getRegistrationCloseAt().toDate().getTime();

        if (now < open) {
            return "Upcoming\nOpens " + df.format(new Date(open));
        } else if (now >= open && now < close) {
            return "Open\nCloses " + df.format(new Date(close));
        } else {
            return "Closed\nEnded " + df.format(new Date(close));
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }

}
