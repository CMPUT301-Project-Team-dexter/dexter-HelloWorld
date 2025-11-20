package com.example.helloworldproject.ui.activities.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.databinding.ActivityEventDetailBinding;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.dialogues.event.QRCodeFrag;
import com.example.helloworldproject.util.CurrentProfile;
import com.example.helloworldproject.util.EventCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventDetailActivity extends AppCompatActivity {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String KEY_EVENT_ID = "key_event_id";

    public static Intent newIntent(Context context, @NonNull String eventId) {
        Intent i = new Intent(context, EventDetailActivity.class);
        i.putExtra(KEY_EVENT_ID, eventId);
        return i;
    }

    ActivityEventDetailBinding binding;
    String givenEventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        setSupportActionBar(binding.evtDtlToolbar);
        executor.execute(() -> {
            givenEventId = getIntent().getStringExtra(KEY_EVENT_ID);
            final Event[] eventPseudoArr = new Event[] { null };
            EventCache.syncTryGetSingle(
                givenEventId,
                new EventRepository.LoadCallback() {
                    @Override
                    public void onLoaded(Event e) {
                        eventPseudoArr[0] = e;
                    }

                    @Override
                    public void onNotFound() {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Event with ID " + givenEventId + " not found.",
                                Toast.LENGTH_SHORT
                            ).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Exception occurs when loading event " + givenEventId + ": " + e.getClass().getCanonicalName(),
                                Toast.LENGTH_SHORT
                            ).show();
                            finish();
                        });
                    }
                }
            );
            runOnUiThread(() -> {
                if (eventPseudoArr[0] == null) {
                    Toast.makeText(
                        this,
                        "Event " + givenEventId + " is null",
                        Toast.LENGTH_SHORT
                    ).show();
                    finish();
                    return;
                }
                binding.setEventModel(eventPseudoArr[0]);
                if (CurrentProfile.isOrganizer()) {
                    binding.evtDtlOrgEditBtn.setOnClickListener(v -> {
                        startActivity(EventEditingActivity.newIntent(this, givenEventId));
                    });
                    binding.evtDtlOrgEditBtn.setVisibility(View.VISIBLE);
                } else if (CurrentProfile.isAdmin()) {
                    binding.evtDtlAdminDeleteBtn.setOnClickListener(v -> {
                        // TODO: implement admin delete functionality
                    });
                    binding.evtDtlAdminDeleteBtn.setVisibility(View.VISIBLE);
                } else {
                    // TODO: show buttons under different circumstances
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_detail_menu, menu);
        if (
            CurrentProfile.isOrganizer() &&
            CurrentProfile.get().getName()
                .contentEquals(binding.getEventModel().getCreator())
        ) {
            MenuItem qrCodeGen = menu.findItem(R.id.evt_dtl_qr_gen_btn);
            qrCodeGen.setVisible(true);
            MenuItem eventManageItem = menu.findItem(R.id.evt_dtl_manage_btn);
            eventManageItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.evt_dtl_qr_gen_btn) {
            new QRCodeFrag(binding.getEventModel())
                .show(getSupportFragmentManager(), "EventQRCodeDialog");
            return true;
        } else if (item.getItemId() == R.id.evt_dtl_manage_btn) {
//            startActivity(EventManagementActivity.newIntent(this, givenEventId));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
