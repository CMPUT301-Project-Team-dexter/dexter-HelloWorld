package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.dialogues.event.EventEditDetailFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditEventBeginFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditEventCapacityFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditEventEndFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditLocFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditRegBeginFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditRegEndFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditTitleFrag;
import com.example.helloworldproject.ui.dialogues.event.EventEditWaitingListCapFrag;
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

    Event event = null;
    String givenEventId = null;

    TextView titleView;
    TextView regPeriodBeginDateView;
    TextView regPeriodEndDateView;
    TextView eventBeginDateView;
    TextView eventEndDateView;
    TextView locView;
    CheckBox geolocationRequiredBox;
    TextView eventCapacityView;
    TextView waitingListCapacityView;
    TextView descTitleView;
    TextView descTextView;

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
        // givenEventId being null means that we are creating new event
        givenEventId = getIntent().getStringExtra(KEY_EVENT_ID);
        if (givenEventId != null) {
            event = EventCache.tryGet(givenEventId, this);
            if (event == null) {
                Toast.makeText(this, "Event is null", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            event = CurrentProfile.get().createBlankEvent();
        }
        // region Title
        titleView = findViewById(R.id.event_title_view);
        if (givenEventId != null) {
            titleView.setText(event.getTitle());
        } else {
            event.setTitle("Not given");
        }
        titleView.setOnClickListener(
            v -> new EventEditTitleFrag().show(
                getSupportFragmentManager(), "EditTitle"
            )
        );
        // endregion
        // region Location
        locView = findViewById(R.id.location_view);
        if (givenEventId != null) {
            locView.setText(event.getVenue());
        } else {
            event.setVenue("N/A");
        }
        locView.setOnClickListener(
            v -> new EventEditLocFrag().show(
                getSupportFragmentManager(), "EditLoc"
            )
        );
        // endregion
        // region regPeriodBeginDateView
        regPeriodBeginDateView = findViewById(R.id.reg_period_from_date_view);
        if (givenEventId == null) {
            Date now = new Date();
            event.setRegistrationOpenAt(new Timestamp(now));
            String currentTimestamp = Event.DATE_FORMATTER.format(now);
            regPeriodBeginDateView.setText(currentTimestamp);
        } else {
            regPeriodBeginDateView.setText(
                Event.DATE_FORMATTER.format(
                    event.getRegistrationOpenAt().toDate()
                )
            );
        }
        regPeriodBeginDateView.setOnClickListener(
            v -> new EventEditRegBeginFrag(
                regPeriodBeginDateView.getText().toString()
            ).show(getSupportFragmentManager(), "RegDateBegin")
        );
        // endregion
        // region regPeriodEndDateView
        regPeriodEndDateView = findViewById(R.id.reg_period_to_date_view);
        if (givenEventId == null) {
            Date now = new Date();
            event.setRegistrationCloseAt(new Timestamp(now));
            String currentTimestamp = Event.DATE_FORMATTER.format(now);
            regPeriodEndDateView.setText(currentTimestamp);
        } else {
            regPeriodEndDateView.setText(
                Event.DATE_FORMATTER.format(
                    event.getRegistrationCloseAt().toDate()
                )
            );
        }
        regPeriodEndDateView.setOnClickListener(
            v -> new EventEditRegEndFrag(
                regPeriodEndDateView.getText().toString()
            ).show(getSupportFragmentManager(), "RegDateEnd")
        );
        // endregion
        // region eventBeginDateView
        eventBeginDateView = findViewById(R.id.event_period_from_date_view);
        if (givenEventId == null) {
            Date now = new Date();
            event.setEventStartAt(new Timestamp(now));
            String currentTimestamp = Event.DATE_FORMATTER.format(now);
            eventBeginDateView.setText(currentTimestamp);
        } else {
            eventBeginDateView.setText(
                Event.DATE_FORMATTER.format(
                    event.getEventStartAt().toDate()
                )
            );
        }
        eventBeginDateView.setOnClickListener(
            v -> new EventEditEventBeginFrag(
                eventBeginDateView.getText().toString()
            ).show(getSupportFragmentManager(), "EventDateBegin")
        );
        // endregion
        // region eventEndDateView
        eventEndDateView = findViewById(R.id.event_period_to_date_view);
        if (givenEventId == null) {
            Date now = new Date();
            event.setEventEndAt(new Timestamp(now));
            String currentTimestamp = Event.DATE_FORMATTER.format(now);
            eventEndDateView.setText(currentTimestamp);
        } else {
            eventEndDateView.setText(
                Event.DATE_FORMATTER.format(
                    event.getEventEndAt().toDate()
                )
            );
        }
        eventEndDateView.setOnClickListener(
            v -> new EventEditEventEndFrag(
                eventEndDateView.getText().toString()
            ).show(getSupportFragmentManager(), "EventDateEnd")
        );
        // endregion
        // region geolocationRequiredBox
        geolocationRequiredBox = findViewById(R.id.geo_loc_check_box);
        if (givenEventId != null) {
            geolocationRequiredBox.setChecked(event.getGeoRequired());
        } else {
            geolocationRequiredBox.setChecked(false);
            event.setGeoRequired(false);
        }
        geolocationRequiredBox.setOnClickListener(
            v -> {
                event.setGeoRequired(
                    geolocationRequiredBox.isChecked()
                );
                System.out.println(geolocationRequiredBox.isChecked());
            }
        );
        // endregion
        // region eventCapacityView
        eventCapacityView = findViewById(R.id.capacity_view);
        if (givenEventId != null) {
            eventCapacityView.setText(String.valueOf(event.getCapacity()));
        } else {
            eventCapacityView.setText("20");
            event.setCapacity(20);
        }
        eventCapacityView.setOnClickListener(
            v -> new EventEditEventCapacityFrag()
                .show(getSupportFragmentManager(), "EventCap")
        );
        // endregion
        // region waitingListCapacityView
        waitingListCapacityView = findViewById(R.id.wl_limit_view);
        if (givenEventId != null) {
            waitingListCapacityView.setText(String.valueOf(event.getPlannedSampleSize()));
        } else {
            waitingListCapacityView.setText("30");
            event.setPlannedSampleSize(30);
        }
        waitingListCapacityView.setOnClickListener(
            v -> new EventEditWaitingListCapFrag()
                .show(getSupportFragmentManager(), "WaitingListCap")
        );
        // endregion
        // region descTextView
        descTitleView = findViewById(R.id.desc_title_view);
        descTextView = findViewById(R.id.desc_text_view);
        if (givenEventId != null) {
            descTextView.setText(event.getDescription());
        } else {
            descTextView.setText("N/A");
            event.setDescription("N/A");
        }
        View.OnClickListener descClickCallback = v -> {
            new EventEditDetailFrag()
                .show(getSupportFragmentManager(), "EventDetails");
        };
        descTitleView.setOnClickListener(descClickCallback);
        descTextView.setOnClickListener(descClickCallback);
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
            if (EventCache.tryUpload(event, this)) {
                //TODO: exit event editing activity
                Toast.makeText(this, "Event uploaded successfully", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateTitle(String newTitle) {
        titleView.setText(newTitle);
        event.setTitle(newTitle);
    }

    @Override
    public void updateLocation(String newLocation) {
        locView.setText(newLocation);
        event.setVenue(newLocation);
    }

    @Override
    public void updateRegBeginDate(long dateInMilli) {
        regPeriodBeginDateView.setText(
            Event.DATE_FORMATTER.format(
                new Date(dateInMilli)
            )
        );
        event.setRegistrationOpenAt(new Timestamp(new Date(dateInMilli)));
    }

    @Override
    public void updateRegEndDate(long dateInMilli) {
        if (dateInMilli < event.getRegistrationCloseAt().toDate().getTime()) {
            Toast.makeText(
                this,
                "Registration end date must be later than start date",
                Toast.LENGTH_SHORT
            ).show();
        } else {
            regPeriodEndDateView.setText(
                Event.DATE_FORMATTER.format(
                    new Date(dateInMilli)
                )
            );
            event.setRegistrationCloseAt(new Timestamp(new Date(dateInMilli)));
        }
    }

    @Override
    public void updateEventBeginDate(long dateInMilli) {
        if (dateInMilli < event.getRegistrationCloseAt().toDate().getTime()) {
            Toast.makeText(
                this,
                "Event start date must be later than registration ends",
                Toast.LENGTH_SHORT
            ).show();
        } else {
            eventBeginDateView.setText(
                Event.DATE_FORMATTER.format(
                    new Date(dateInMilli)
                )
            );
            event.setEventStartAt(new Timestamp(new Date(dateInMilli)));
        }
    }

    @Override
    public void updateEventEndDate(long dateInMilli) {
        if (dateInMilli < event.getEventStartAt().toDate().getTime()) {
            Toast.makeText(
                this,
                "Event end date must be later than start date",
                Toast.LENGTH_SHORT
            ).show();
        } else {
            eventEndDateView.setText(
                Event.DATE_FORMATTER.format(
                    new Date(dateInMilli)
                )
            );
            event.setEventEndAt(new Timestamp(new Date(dateInMilli)));
        }
    }

    private void updateWaitingListCapInternal(int newCapacity) {
        waitingListCapacityView.setText(String.valueOf(newCapacity));
        event.setPlannedSampleSize(newCapacity);
    }

    @Override
    public void updateEventCapacity(int newCapacity) {
        eventCapacityView.setText(String.valueOf(newCapacity));
        event.setCapacity(newCapacity);
        if (event.getPlannedSampleSize() < newCapacity) {
            updateWaitingListCapInternal(newCapacity);
            Toast.makeText(this, "Waiting list capacity is updated automatically.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateWaitingListCapacity(int newCapacity) {
        if (newCapacity < event.getCapacity()) {
            Toast.makeText(this, "Waiting list capacity can not be less than event capacity", Toast.LENGTH_SHORT).show();
        } else {
            updateWaitingListCapInternal(newCapacity);
        }
    }

    @Override
    public void updateDetail(String newDetail) {
        descTextView.setText(newDetail);
        event.setDescription(newDetail);
    }
}
