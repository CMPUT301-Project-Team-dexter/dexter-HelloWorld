package com.example.helloworldproject.ui.fragments;
import com.example.helloworldproject.model.UserGroup;
import androidx.appcompat.app.AlertDialog;

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
import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.ui.utils.UserItemAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


public class AllUsersFragment extends Fragment {

    private ListView listView;
    private UserItemAdapter adapter;
    private final ArrayList<String> userNames = new ArrayList<>();
    private final ArrayList<String> userIds = new ArrayList<>();
    private final ArrayList<Profile> profiles = new ArrayList<>();
    private final ProfileRepository profileRepository = new ProfileRepository();


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


        listView = view.findViewById(R.id.event_list_view);

        adapter = new UserItemAdapter(
                requireContext(),
                userNames,
                userIds,
                position -> {
                    // Safety check
                    if (position < 0 || position >= profiles.size()) {
                        return;
                    }

                    Profile toDelete = profiles.get(position);
                    if (toDelete == null) return;

                    UserGroup group = toDelete.getUserGroup();
                    if (group == UserGroup.ADMIN) {
                        Toast.makeText(
                                requireContext(),
                                "Admin profiles cannot be deleted.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    String name = toDelete.getName();
                    if (name == null || name.isEmpty()) {
                        name = "this profile";
                    }

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Delete profile")
                            .setMessage("Are you sure you want to delete " + name + "?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                profileRepository.deleteProfile(toDelete, new ProfileRepository.CompleteCallback() {
                                    @Override
                                    public void onComplete() {
                                        requireActivity().runOnUiThread(() -> {
                                            Toast.makeText(
                                                    requireContext(),
                                                    "Profile deleted.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                            loadProfiles();   // refresh list
                                        });
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        requireActivity().runOnUiThread(() -> {
                                            Toast.makeText(
                                                    requireContext(),
                                                    "Failed to delete profile: " + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        });
                                    }
                                });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
        );
        listView.setAdapter(adapter);

        loadProfiles();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_menu_list_base, container, false);
        listView = view.findViewById(R.id.event_list_view);

        return view;
    }
    private void loadProfiles() {
        profileRepository.loadAllProfiles(new ProfileRepository.ListCallback() {
            @Override
            public void onLoaded(List<Profile> loadedProfiles) {
                requireActivity().runOnUiThread(() -> {
                    profiles.clear();
                    userNames.clear();
                    userIds.clear();

                    for (Profile p : loadedProfiles) {
                        profiles.add(p);   // keep full object

                        String name = p.getName() == null ? "(Unnamed)" : p.getName();

                        UserGroup group = p.getUserGroup();
                        String roleLabel;
                        if (group == null) {
                            roleLabel = "Unknown";
                        } else {
                            switch (group) {
                                case ADMIN:
                                    roleLabel = "Admin";
                                    break;
                                case ORGANIZER:
                                    roleLabel = "Organizer";
                                    break;
                                case ENTRANT:
                                    roleLabel = "Entrant";
                                    break;
                                default:
                                    roleLabel = group.name();
                            }
                        }

                        userNames.add(name);
                        userIds.add(roleLabel);
                    }

                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(
                                requireContext(),
                                "Failed to load profiles: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }


}
