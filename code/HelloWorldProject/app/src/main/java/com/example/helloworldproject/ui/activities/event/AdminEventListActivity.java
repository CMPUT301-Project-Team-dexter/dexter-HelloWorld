package com.example.helloworldproject.ui.activities.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.databinding.ActivityAdminEventListBinding;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.utils.EventCardAdapter;
import com.example.helloworldproject.util.CurrentProfile;

import java.util.ArrayList;
import java.util.List;

public class AdminEventListActivity extends AppCompatActivity {
    ActivityAdminEventListBinding binding;
    ArrayList<Event> eventListBackEnd = new ArrayList<>();
    EventCardAdapter adapter;
    private final ActivityResultLauncher<Intent> eventDetailLauncher = getEventDetailLauncher();

    public static Intent newIntent(Context context) {
        return new Intent(context, AdminEventListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_event_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (!CurrentProfile.isAdmin()) {
            Toast.makeText(this, "Admin permission required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setSupportActionBar(binding.adminConsEvtToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            binding.adminConsEvtToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
            );
        }
        adapter = new EventCardAdapter(this, eventListBackEnd);
        binding.adminConsEvtList.setAdapter(adapter);
        binding.adminConsEvtList.setOnItemClickListener(
            (parent, view, position, id) -> {
                Event e = adapter.getItem(position);
                if (e != null) {
                    eventDetailLauncher.launch(
                        EventDetailActivity.newIntent(
                            AdminEventListActivity.this,
                            e.getId()
                        )
                    );
                } else {
                    Toast.makeText(
                        AdminEventListActivity.this,
                        "Error loading event details: event is null",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        );
        loadEventsForAdmin();
    }

    private void updateAdapterFrom(List<Event> events) {
        adapter.clear();
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }

    // Admin view: all events
    private void loadEventsForAdmin() {
        EventRepository.INSTANCE.loadAllEvents(
            new EventRepository.ListCallback() {
                @Override
                public void onLoaded(List<Event> events) {
                    updateAdapterFrom(events);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(
                        AdminEventListActivity.this,
                        "Failed to load events: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        );
    }

    private ActivityResultLauncher<Intent> getEventDetailLauncher() {
        return registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    System.out.println("!!!!Event detail activity result OK");
                    loadEventsForAdmin();
                }
            }
        );
    }
}
