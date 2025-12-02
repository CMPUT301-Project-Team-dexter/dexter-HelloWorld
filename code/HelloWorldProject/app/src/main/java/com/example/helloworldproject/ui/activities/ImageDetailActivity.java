package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;
import com.google.android.material.appbar.MaterialToolbar;

public class ImageDetailActivity extends AppCompatActivity {
    private static final String ARG_EVENT_ID = "arg_event_id";
    private static final String ARG_IMAGE_URL = "arg_image_url";
    private String eventId;
    private String imageUrl;

    public static Intent newIntent(Context context, @Nullable String eventId, @Nullable String imageUrl) {
        Intent i = new Intent(context, ImageDetailActivity.class);
        i.putExtra(ARG_EVENT_ID, eventId);
        i.putExtra(ARG_IMAGE_URL, imageUrl);
        return i;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        Intent intent = getIntent();
        eventId = intent.getStringExtra(ARG_EVENT_ID);
        imageUrl = intent.getStringExtra(ARG_IMAGE_URL);

        MaterialToolbar toolbar = findViewById(R.id.img_dtl_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageView imageView = findViewById(R.id.image);
        Button deleteButton = findViewById(R.id.delete_button);

        // Show the image
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(imageView);

        // Wire the delete button
        deleteButton.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(this,
                    "Event ID is missing, cannot delete image.",
                    Toast.LENGTH_SHORT
                ).show();
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("Remove image")
                .setMessage("Are you sure you want to remove this image from the event?")
                .setPositiveButton("Delete", (dialog, which) -> performDelete())
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    private void performDelete() {
        EventRepository.INSTANCE.asyncLoadById(eventId, new EventRepository.LoadCallback() {
            @Override
            public void onLoaded(Event e) {
                if (e == null) {
                    runOnUiThread(() ->
                        Toast.makeText(
                            ImageDetailActivity.this,
                            "Event not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    );
                    return;
                }

                // Clear all poster-related fields
                e.setImgId(null);
                e.setImgUrl(null);
                e.setImgUrlEnable(false);

                EventRepository.INSTANCE.saveOrUpdate(e, new EventRepository.CompleteCallback() {
                    @Override
                    public void onComplete() {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                ImageDetailActivity.this,
                                "Image removed.",
                                Toast.LENGTH_SHORT
                            ).show();
                            setResult(RESULT_OK);
                            finish();
                        });
                    }

                    @Override
                    public void onError(Exception ex) {
                        runOnUiThread(() ->
                            Toast.makeText(
                                ImageDetailActivity.this,
                                "Failed to remove image: " + ex.getMessage(),
                                Toast.LENGTH_LONG
                            ).show()
                        );
                    }
                });
            }

            @Override
            public void onNotFound() {
                runOnUiThread(() ->
                    Toast.makeText(
                        ImageDetailActivity.this,
                        "Event not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                );
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                    Toast.makeText(
                        ImageDetailActivity.this,
                        "Failed to load event: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show()
                );
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_OK);
        finish();
        return true;
    }
}
