package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.utils.EventItemAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AllEventsFragment extends Fragment {

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

        // Dummy data
        //listView = view.findViewById(R.id.event_list_view);
        //ArrayList<String> userNames = new ArrayList<>();
        //userNames.add("AlphaBravo");
        //userNames.add("TestName2");
        //userNames.add("Chill");
        //ArrayList<String> userIds = new ArrayList<>();
        //userIds.add("id1");
        //userIds.add("id2");
        //userIds.add("id3");
        //ArrayList<String> eventNames = new ArrayList<>();
        //eventNames.add("Carnival");
        //eventNames.add("Delusion");
        //eventNames.add("All Saints Day");
        //EventItemAdapter adapter = new EventItemAdapter(requireContext(), userNames, userIds, eventNames);
        //listView.setAdapter(adapter);

        listView = view.findViewById(R.id.event_list_view);

// Backing lists for the adapter
        ArrayList<String> userNames = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();
        ArrayList<String> eventNames = new ArrayList<>();

// Hook up adapter first (empty list to start)
        EventItemAdapter adapter = new EventItemAdapter(
            requireContext(),
            userNames,
            userIds,
            eventNames
        );
        listView.setAdapter(adapter);

// Use the repository method we just added
        EventRepository repo = EventRepository.INSTANCE; // or new EventRepository()

        repo.loadAllEvents(new EventRepository.ListCallback() {
            @Override
            public void onLoaded(List<Event> events) {
                // Clear old data and repopulate
                userNames.clear();
                userIds.clear();
                eventNames.clear();

                for (Event e : events) {
                    // Map Event -> row fields:
                    // eventName  = title (main label)
                    // userName   = venue
                    // userId     = event id
                    String title = e.getTitle() != null ? e.getTitle() : "(Untitled event)";
                    String venue = e.getVenue() != null ? e.getVenue() : "No venue";
                    String id = e.getId() != null ? e.getId() : "";

                    userNames.add(venue);
                    userIds.add(id);
                    eventNames.add(title);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(
                    requireContext(),
                    "Failed to load events: " + e.getMessage(),
                    Toast.LENGTH_LONG
                ).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_menu_list_base, container, false);
        listView = view.findViewById(R.id.event_list_view);

        return view;
    }
}
