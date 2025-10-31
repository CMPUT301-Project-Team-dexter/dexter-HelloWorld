package com.example.helloworldproject;

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
import androidx.constraintlayout.helper.widget.Grid;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AllImagesFragment extends Fragment {

    private RecyclerView recyclerView;

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
                menuInflater.inflate(R.menu.all_images_fragment_menu, menu);
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


        recyclerView = view.findViewById(R.id.recyclerview);
        ArrayList<Integer> items = new ArrayList<>();
        items.add(R.drawable.debug_card_image);
        items.add(R.drawable.debug_card_image2);
        items.add(R.drawable.debug_card_image);
        items.add(R.drawable.debug_card_image2);

        AllImagesFragmentAdapter adapter = new AllImagesFragmentAdapter(requireContext(), items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, 0, 0, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_images_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);

        return view;
    }
}
