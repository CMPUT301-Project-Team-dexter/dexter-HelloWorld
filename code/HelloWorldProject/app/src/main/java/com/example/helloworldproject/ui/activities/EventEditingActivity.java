package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.helloworldproject.R;
import com.example.helloworldproject.ui.dialogues.event.EventEditListener;
import com.example.helloworldproject.ui.dialogues.event.EventEditLocFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditTitleFrag;

public class EventEditingActivity extends AppCompatActivity implements EventEditListener {
    private static final String KEY_EVENT_ID = "key_event_id";

    public static Intent newIntent(Context context, @Nullable String eventId) {
        Intent i = new Intent(context, EventEditingActivity.class);
        i.putExtra(KEY_EVENT_ID, eventId);
        return i;
    }

    TextView titleView;
    TextView locView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_editing_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        titleView = findViewById(R.id.event_title_view);
        titleView.setOnClickListener(
            v -> new EventEditTitleFrag().show(
                getSupportFragmentManager(), "EditTitle"
            )
        );
        locView = findViewById(R.id.location_view);
        locView.setOnClickListener(
            v -> new EventEditLocFrag().show(
                getSupportFragmentManager(), "EditLoc"
            )
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editing_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_button) {
            // TODO save event
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateTitle(String newTitle) {
        titleView.setText(newTitle);
    }

    @Override
    public void updateLocation(String newLocation) {
        locView.setText(newLocation);
    }
}