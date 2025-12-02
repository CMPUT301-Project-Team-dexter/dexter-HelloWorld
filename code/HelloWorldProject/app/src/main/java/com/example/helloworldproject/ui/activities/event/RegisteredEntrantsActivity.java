package com.example.helloworldproject.ui.activities.event;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.ui.utils.RegisteredEntrantAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.OutputStream;
import java.util.List;

public class RegisteredEntrantsActivity extends AppCompatActivity {

    private static final String KEY_EVENT_ID = "id";
    private RegisteredEntrantAdapter adapter;
    private String eventId;

    public static Intent newIntent(Context context, String eventId) {
        Intent i = new Intent(context, RegisteredEntrantsActivity.class);
        i.putExtra(KEY_EVENT_ID, eventId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_entrants);

        eventId = getIntent().getStringExtra(KEY_EVENT_ID);
        if (eventId == null) {
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
            );
        }

        View btnExport = findViewById(R.id.btn_export_csv);
        if (btnExport != null) {
            btnExport.setOnClickListener(v -> saveToDownloads());
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RegisteredEntrantAdapter();
        recyclerView.setAdapter(adapter);

        loadAcceptedEntrants();
    }

    private void loadAcceptedEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).collection("invites")
            .whereEqualTo("status", "ACCEPTED")
            .get()
            .addOnSuccessListener(querySnapshots -> {
                if (querySnapshots.isEmpty()) {
                    Toast.makeText(this, "No registered entrants found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DocumentSnapshot doc : querySnapshots) {
                    String userId = doc.getId();
                    loadProfileForUser(userId);
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void loadProfileForUser(String userId) {
        FirebaseFirestore.getInstance().collection("profiles").document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Profile p = documentSnapshot.toObject(Profile.class);
                    if (p != null) {
                        adapter.addEntrant(p);
                    }
                }
            });
    }

    private void saveToDownloads() {
        List<Profile> list = adapter.getEntrants();
        if (list.isEmpty()) {
            Toast.makeText(this, "List is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Name,Email,Phone\n");
        for (Profile p : list) {
            String safeName = p.getName() != null ? p.getName().replace(",", " ") : "Unknown";
            String safeEmail = p.getEmail() != null ? p.getEmail() : "";
            String safePhone = p.getPhone() != null ? p.getPhone() : "";
            csvContent.append(safeName).append(",")
                .append(safeEmail).append(",")
                .append(safePhone).append("\n");
        }

        try {
            String fileName = "entrants_" + System.currentTimeMillis() + ".csv";

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    outputStream.write(csvContent.toString().getBytes());
                    outputStream.close();

                    Toast.makeText(this, "Saved to Downloads: " + fileName, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}