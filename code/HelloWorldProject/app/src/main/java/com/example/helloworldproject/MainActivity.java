package com.example.helloworldproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.ui.event.EventDetailActivity;
import com.example.helloworldproject.ui.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etEventId;
    private Button btnOpenProfile, btnOpenEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEventId = findViewById(R.id.et_event_id);
        btnOpenProfile = findViewById(R.id.btn_open_profile);
        btnOpenEvent = findViewById(R.id.btn_open_event);

        btnOpenProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        btnOpenEvent.setOnClickListener(v -> {
            String eventId = etEventId.getText().toString().trim();
            if (eventId.isEmpty()) {
                eventId = "demo1"; // default for quick testing
                Toast.makeText(this, "Using default eventId: demo1", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(this, EventDetailActivity.class)
                    .putExtra(EventDetailActivity.EXTRA_EVENT_ID, eventId));
        });

        // This is the fragment to be displayed upon opening the app.
        // If you want to try other fragments, replace the corresponding keywords.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterHistoryFragment())
                    .commit();
        }
    }
}
