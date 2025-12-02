package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.utils.AllImagesFragmentAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class AllImagesActivity extends AppCompatActivity {
    private final ArrayList<Event> imageEvents = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllImagesFragmentAdapter adapter;
    private final ActivityResultLauncher<Intent> imageDetailLauncher = getImageDetailLauncher();

    public static Intent newIntent(Context context) {
        return new Intent(context, AllImagesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_images);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
            );
        }

        recyclerView = findViewById(R.id.recyclerview);

        adapter = new AllImagesFragmentAdapter(
            AllImagesActivity.this,
            imageEvents,
            event -> {
                String url = event.getImgUrl();
                if ((url == null || url.trim().isEmpty()) && event.getImgId() != null) {
                    url = event.getImgId();
                }

                if (url == null || url.trim().isEmpty()) {
                    Toast.makeText(
                        AllImagesActivity.this,
                        "This event has no image.",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                imageDetailLauncher.launch(
                    ImageDetailActivity.newIntent(
                        AllImagesActivity.this,
                        event.getId(),
                        url
                    )
                );
            }
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, 0, 0, 0);

        loadImages();
    }

    private void loadImages() {
        EventRepository.INSTANCE.loadAllEvents(new EventRepository.ListCallback() {
            @Override
            public void onLoaded(java.util.List<Event> events) {
                runOnUiThread(() -> {
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
                runOnUiThread(() ->
                    Toast.makeText(
                        AllImagesActivity.this,
                        "Failed to load images: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show()
                );
            }
        });
    }

    private ActivityResultLauncher<Intent> getImageDetailLauncher() {
        return registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadImages();
                }
            }
        );
    }
}
