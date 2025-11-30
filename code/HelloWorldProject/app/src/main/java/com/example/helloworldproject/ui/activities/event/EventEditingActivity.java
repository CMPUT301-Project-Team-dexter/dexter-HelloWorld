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
import com.example.helloworldproject.databinding.ActivityEventEditingBinding;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.dialogues.event.editing.DetailFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.EventBeginFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.EventCapacityFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.EventEndFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.LocationFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.RegistrationBeginFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.RegistrationEndFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.TitleFrag;
import com.example.helloworldproject.ui.dialogues.event.editing.WaitingListCapacityFrag;
import com.example.helloworldproject.util.CurrentProfile;
import com.example.helloworldproject.util.EventCache;
import com.google.firebase.Timestamp;


import java.util.Date;

public class EventEditingActivity extends AppCompatActivity implements EventEditListener {
    private static final String KEY_EVENT_ID = "key_event_id";

    public static Intent newIntent(Context context, @Nullable String eventId) {
        Intent i = new Intent(context, EventEditingActivity.class);
        i.putExtra(KEY_EVENT_ID, eventId);
        return i;
    }

    ActivityEventEditingBinding binding;
    String givenEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_editing);
        setSupportActionBar(binding.evtEdToolbar);
        givenEventId = getIntent().getStringExtra(KEY_EVENT_ID);
        // givenEventId being null means that we are creating new event
        if (givenEventId != null) {
            EventCache.asyncTryGetSingle(
                givenEventId,
                new EventRepository.LoadCallback() {
                    @Override
                    public void onLoaded(Event e) {
                        onCreateContinued(e);
                    }

                    @Override
                    public void onNotFound() {
                        runOnUiThread(() -> Toast.makeText(
                            EventEditingActivity.this,
                            "Event with ID " + givenEventId + " not found.",
                            Toast.LENGTH_SHORT
                        ).show());
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> Toast.makeText(
                            EventEditingActivity.this,
                            "Exception occurs when loading event " + givenEventId + ": " + e.getClass().getCanonicalName(),
                            Toast.LENGTH_SHORT
                        ).show());
                    }
                }
            );
        } else {
            onCreateContinued(CurrentProfile.get().createBlankEvent());
        }
    }

    private void onCreateContinued(@Nullable Event e) {
        if (e == null) {
            Toast.makeText(
                this,
                "Event " + givenEventId + " is null",
                Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }
        binding.setEventModel(e);
        // region Title
        if (givenEventId == null) {
            binding.getEventModel().setTitle("Tap to edit title...");
        }
        binding.evtEdTitleView.setOnClickListener(
            v -> new TitleFrag().show(
                getSupportFragmentManager(), "EditTitle"
            )
        );
        // endregion
        // region Location
        if (givenEventId == null) {
            binding.getEventModel().setVenue("N/A");
        }
        binding.evtEdLocationView.setOnClickListener(
            v -> new LocationFrag().show(
                getSupportFragmentManager(), "EditLoc"
            )
        );
        // endregion
        // region dates
        if (givenEventId == null) {
            Date now = new Date();
            Event event = binding.getEventModel();
            event.setRegistrationOpenAt(new Timestamp(now));
            event.setRegistrationCloseAt(new Timestamp(now));
            event.setEventStartAt(new Timestamp(now));
            event.setEventEndAt(new Timestamp(now));
        }
        binding.evtEdRegStartView.setOnClickListener(
            v -> new RegistrationBeginFrag(
                binding.evtEdRegStartView.getText().toString()
            ).show(getSupportFragmentManager(), "RegDateBegin")
        );
        binding.evtEdRegEndView.setOnClickListener(
            v -> new RegistrationEndFrag(
                binding.evtEdRegEndView.getText().toString()
            ).show(getSupportFragmentManager(), "RegDateEnd")
        );
        binding.evtEdEventStartView.setOnClickListener(
            v -> new EventBeginFrag(
                binding.evtEdEventStartView.getText().toString()
            ).show(getSupportFragmentManager(), "EventDateBegin")
        );
        binding.evtEdEventEndView.setOnClickListener(
            v -> new EventEndFrag(
                binding.evtEdEventEndView.getText().toString()
            ).show(getSupportFragmentManager(), "EventDateEnd")
        );
        // endregion
        // region geolocationRequiredBox
        if (givenEventId == null) {
            binding.getEventModel().setGeoRequired(false);
        }
        binding.evtEdGeoLimitSwitch.setOnClickListener(
            v -> binding.getEventModel().setGeoRequired(
                binding.evtEdGeoLimitSwitch.isChecked()
            )
        );
        // endregion
        // region eventCapacityView
        if (givenEventId == null) {
            binding.getEventModel().setCapacity(20);
        }
        binding.evtEdCapacityView.setOnClickListener(
            v -> new EventCapacityFrag()
                .show(getSupportFragmentManager(), "EventCap")
        );
        // endregion
        // region waitingListCapacityView
        if (givenEventId == null) {
            binding.getEventModel().setPlannedSampleSize(30);
        }
        binding.evtEdWaitListLimitView.setOnClickListener(
            v -> new WaitingListCapacityFrag()
                .show(getSupportFragmentManager(), "WaitingListCap")
        );
        // endregion
        // region descTextView
        if (givenEventId == null) {
            binding.getEventModel().setDescription("Tap to edit event details...");
        }
        View.OnClickListener descClickCallback = v -> new DetailFrag().show(
            getSupportFragmentManager(), "EventDetails"
        );
        binding.evtEdDescTitleView.setOnClickListener(descClickCallback);
        binding.evtEdDescTextView.setOnClickListener(descClickCallback);
        // endregion
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editing_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_button) {
            EventRepository.INSTANCE.saveOrUpdate(
                binding.getEventModel(),
                new EventRepository.CompleteCallback() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(
                            EventEditingActivity.this,
                            "Event uploaded successfully",
                            Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(
                            EventEditingActivity.this,
                            "Event upload failed. Cause: " + e.getMessage(),
                            Toast.LENGTH_LONG
                        ).show();
                    }
                }
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateTitle(String newTitle) {
        binding.getEventModel().setTitle(newTitle);
    }

    @Override
    public void updateLocation(String newLocation) {
        binding.getEventModel().setVenue(newLocation);
    }

    private void autoUpdateRegEndDate() {
        Date regBeginDate = binding.getEventModel().getRegistrationOpenAt().toDate();
        Date regEndDate = binding.getEventModel().getRegistrationCloseAt().toDate();
        if (regEndDate.before(regBeginDate)) {
            binding.getEventModel().setRegistrationCloseAt(new Timestamp(regBeginDate));
        }
        autoUpdateEventStartDate();
    }

    private void autoUpdateEventStartDate() {
        Date regEndDate = binding.getEventModel().getRegistrationCloseAt().toDate();
        Date eventStartDate = binding.getEventModel().getEventStartAt().toDate();
        if (eventStartDate.before(regEndDate)) {
            binding.getEventModel().setEventStartAt(new Timestamp(regEndDate));
        }
        autoUpdateEventEndDate();
    }

    private void autoUpdateEventEndDate() {
        Date eventStartDate = binding.getEventModel().getEventStartAt().toDate();
        Date eventEndDate = binding.getEventModel().getEventEndAt().toDate();
        if (eventEndDate.before(eventStartDate)) {
            binding.getEventModel().setEventEndAt(new Timestamp(eventStartDate));
        }
    }

    @Override
    public void updateRegBeginDate(long dateInMilli) {
        binding.getEventModel().setRegistrationOpenAt(new Timestamp(new Date(dateInMilli)));
        autoUpdateRegEndDate();
    }

    @Override
    public void updateRegEndDate(long dateInMilli) {
        Date regEndDate = new Date(dateInMilli);
        if (regEndDate.before(binding.getEventModel().getRegistrationOpenAt().toDate())) {
            Toast.makeText(
                this,
                "Registration end date must be later than start date",
                Toast.LENGTH_SHORT
            ).show();
            return;
        }
        binding.getEventModel().setRegistrationCloseAt(new Timestamp(regEndDate));
        autoUpdateEventStartDate();
    }

    @Override
    public void updateEventBeginDate(long dateInMilli) {
        Date eventStartDate = new Date(dateInMilli);
        if (eventStartDate.before(binding.getEventModel().getRegistrationCloseAt().toDate())) {
            Toast.makeText(
                this,
                "Event start date must be later than registration ends",
                Toast.LENGTH_SHORT
            ).show();
            return;
        }
        binding.getEventModel().setEventStartAt(new Timestamp(eventStartDate));
        autoUpdateEventEndDate();
    }

    @Override
    public void updateEventEndDate(long dateInMilli) {
        Date eventEndDate = new Date(dateInMilli);
        if (eventEndDate.before(binding.getEventModel().getEventStartAt().toDate())) {
            Toast.makeText(
                this,
                "Event end date must be later than start date",
                Toast.LENGTH_SHORT
            ).show();
            return;
        }
        binding.getEventModel().setEventEndAt(new Timestamp(eventEndDate));
    }

    @Override
    public void updateEventCapacity(int newCapacity) {
        binding.getEventModel().setCapacity(newCapacity);
        if (binding.getEventModel().getPlannedSampleSize() < newCapacity) {
            binding.getEventModel().setPlannedSampleSize(newCapacity);
            Toast.makeText(this, "Waiting list capacity is updated automatically.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateWaitingListCapacity(int newCapacity) {
        if (newCapacity < binding.getEventModel().getCapacity()) {
            Toast.makeText(this, "Waiting list capacity can not be less than event capacity", Toast.LENGTH_SHORT).show();
        } else {
            binding.getEventModel().setPlannedSampleSize(newCapacity);
        }
    }

    @Override
    public void updateDetail(String newDetail) {
        binding.getEventModel().setDescription(newDetail);
    }
}
