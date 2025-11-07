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

import com.example.helloworldproject.ui.utils.EventItemAdapter;
import com.example.helloworldproject.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

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


        listView = view.findViewById(R.id.listview);
        ArrayList<String> userNames = new ArrayList<>();
        userNames.add("AlphaBravo");
        userNames.add("TestName2");
        userNames.add("Chill");

        ArrayList<String> userIds = new ArrayList<>();
        userIds.add("id1");
        userIds.add("id2");
        userIds.add("id3");

        ArrayList<String> eventNames = new ArrayList<>();
        eventNames.add("Carnival");
        eventNames.add("Delusion");
        eventNames.add("All Saints Day");

        EventItemAdapter adapter = new EventItemAdapter(requireContext(), userNames, userIds, eventNames);
        listView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_menu_list_base, container, false);
        listView = view.findViewById(R.id.listview);

        return view;
    }
}
