package com.example.helloworldproject.ui.activities.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.helloworldproject.R;
import com.example.helloworldproject.databinding.ActivityEventManagementBinding;

public class EventManageActivity extends AppCompatActivity {
    private static final String KEY_EVENT_ID = "KEY_EVENT_ID";

    public static Intent newIntent(Context context, @NonNull String eventId) {
        Intent i = new Intent(context, EventManageActivity.class);
        i.putExtra(KEY_EVENT_ID, eventId);
        return i;
    }
    ActivityEventManagementBinding binding;
    String givenEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.evtMgrToolbar);
        givenEventId = getIntent().getStringExtra(KEY_EVENT_ID);
        if (givenEventId == null) {
            Toast.makeText(this, "Event id is null", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // TODO: add code to jump
        binding.evtMgrChosenEntrants.setOnClickListener(
            v -> {
                Toast.makeText(EventManageActivity.this, "for test only: " + givenEventId, Toast.LENGTH_SHORT).show();
            }
        );
    }
}
