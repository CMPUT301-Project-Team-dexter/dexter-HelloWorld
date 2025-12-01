package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.utils.AllImagesFragmentAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class AllImagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllImagesFragmentAdapter adapter;
    private final ArrayList<Event> imageEvents = new ArrayList<>();

    private void loadImages() {
        EventRepository.INSTANCE.loadAllEvents(new EventRepository.ListCallback() {
            @Override
            public void onLoaded(java.util.List<Event> events) {
                requireActivity().runOnUiThread(() -> {
                    imageEvents.clear();
                    for (Event e : events) {
                        if (e == null) continue;

                        Boolean enable = e.getImgUrlEnable();
                        String url;
                        if (enable != null && enable) {
                            url = e.getImgUrl();
                        } else {
                            url = e.getImgId();
                        }

                        if (url != null && !url.trim().isEmpty()) {
                            imageEvents.add(e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(
                                requireContext(),
                                "Failed to load images: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.all_images_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // toolbar + menu
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

        // hide title text in top bar
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        recyclerView = view.findViewById(R.id.recyclerview);

        adapter = new AllImagesFragmentAdapter(
                requireContext(),
                imageEvents,
                event -> {
                    String url = event.getImgUrl();
                    if ((url == null || url.trim().isEmpty()) && event.getImgId() != null) {
                        url = event.getImgId();
                    }

                    if (url == null || url.trim().isEmpty()) {
                        Toast.makeText(
                                requireContext(),
                                "This event has no image.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    ImageDetailFragment fragment =
                            ImageDetailFragment.newInstance(event.getId(), url);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, 0, 0, 0);

        loadImages();
    }
}
