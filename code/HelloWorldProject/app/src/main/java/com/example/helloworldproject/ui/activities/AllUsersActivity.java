package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.model.UserGroup;
import com.example.helloworldproject.ui.utils.UserItemAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


public class AllUsersActivity extends AppCompatActivity {
    private final ArrayList<String> userNames = new ArrayList<>();
    private final ArrayList<String> userIds = new ArrayList<>();
    private final ArrayList<Profile> profiles = new ArrayList<>();
    private final ProfileRepository profileRepository = new ProfileRepository();
    private ListView listView;
    private UserItemAdapter adapter;

    public static Intent newIntent(Context context) {
        return new Intent(context, AllUsersActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_menu_list_base);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("All Users");
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
            );
        }


        listView = findViewById(R.id.event_list_view);

        adapter = new UserItemAdapter(
            this,
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
                        this,
                        "Admin profiles cannot be deleted.",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                String name = toDelete.getName();
                if (name == null || name.isEmpty()) {
                    name = "this profile";
                }

                new AlertDialog.Builder(this)
                    .setTitle("Delete profile")
                    .setMessage("Are you sure you want to delete " + name + "?")
                    .setPositiveButton("Delete",
                        (dialog, which) ->
                            profileRepository.deleteProfile(toDelete, new ProfileRepository.CompleteCallback() {
                                @Override
                                public void onComplete() {
                                    runOnUiThread(() -> {
                                        Toast.makeText(
                                            AllUsersActivity.this,
                                            "Profile deleted.",
                                            Toast.LENGTH_SHORT
                                        ).show();
                                        loadProfiles();   // refresh list
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(
                                        () -> Toast.makeText(
                                            AllUsersActivity.this,
                                            "Failed to delete profile: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                        ).show());
                                }
                            })
                    )
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        );
        listView.setAdapter(adapter);
        loadProfiles();
    }


    private void loadProfiles() {
        profileRepository.loadAllProfiles(new ProfileRepository.ListCallback() {
            @Override
            public void onLoaded(List<Profile> loadedProfiles) {
                runOnUiThread(() -> {
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
                runOnUiThread(() ->
                    Toast.makeText(
                        AllUsersActivity.this,
                        "Failed to load profiles: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show()
                );
            }
        });
    }
}
