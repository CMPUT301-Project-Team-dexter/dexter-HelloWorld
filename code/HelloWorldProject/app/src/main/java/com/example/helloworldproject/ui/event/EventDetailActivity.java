package com.example.helloworldproject.ui.event;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.data.WaitlistRepository;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.ui.fragments.EntrantState;
import com.example.helloworldproject.ui.fragments.EventDetailFragment;
import com.example.helloworldproject.ui.fragments.EventDetailViewModel;
import com.google.firebase.firestore.ListenerRegistration;

/** Event details screen showing waitlist size and lottery rules. */
public class EventDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "extra_event_id";

    private TextView tvTitle, tvDesc, tvVenue, tvWaitlistCount;
    private Button btnLotteryRules;

    private EventRepository eventRepo;
    private WaitlistRepository waitlistRepo;
    private ListenerRegistration countReg;
    private String eventId;
    private Event currentEvent;
    private int currentWaitlistCount = 0;



    public EventDetailViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        viewModel = new ViewModelProvider(this).get(EventDetailViewModel.class);
        viewModel.loadState(EntrantState.UNRELATED);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventDetailFragment())
                    .commit();
        }


        eventRepo = new EventRepository();
        waitlistRepo = new WaitlistRepository();

//        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        eventId = "cw_debug"; // FIXME: Connect this to an actual ID
        if (eventId == null) {
            Toast.makeText(this, "Missing eventId", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        eventRepo.loadById(eventId, new EventRepository.LoadCallback() {
            @Override public void onLoaded(Event e) {
                currentEvent = e;
                viewModel.setEvent(e);
//                tvTitle.setText(e.getTitle());
//                tvDesc.setText(e.getDescription());
//                tvVenue.setText(e.getVenue());
            }
            @Override public void onNotFound() {
                Toast.makeText(EventDetailActivity.this, "Event not found", Toast.LENGTH_LONG).show();
                finish();
            }
            @Override public void onError(Exception e) {
                Toast.makeText(EventDetailActivity.this, "Failed to load event: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Observe waitlist count (US 01.05.04).
        countReg = waitlistRepo.observeCount(eventId, new WaitlistRepository.CountListener() {
            @Override public void onCount(int total) {
                currentWaitlistCount = total;
                viewModel.setCurrentWaitlistCount(currentWaitlistCount);
//                tvWaitlistCount.setText("Waitlist size: " + total);
            }
            @Override public void onError(Exception e) {
                Toast.makeText(EventDetailActivity.this, "Failed to read waitlist size: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

//        // Show lottery rules (US 01.05.05).
//        btnLotteryRules.setOnClickListener(v -> {
//            if (currentEvent == null) return;
//            String msg = LotteryTextBuilder.build(currentEvent, currentWaitlistCount);
//            LotteryTextBuilder.showDialog(EventDetailActivity.this, msg);
//        });

        viewModel.getJoinWaitlistFlag().observe(this, flag -> {
            if (flag) {
//                addToWaitlist(CurrentProfile.get().getId()); FIXME: replace this with currentProfile once connected
                addToWaitlist("dD3yej7OQvyU57atJgH9cJ");
                viewModel.resetFlag();
            }
        } );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countReg != null) {
            countReg.remove();
            countReg = null;
        }
    }

    private void addToWaitlist(String userId) {
        if (eventId == null) {
            Toast.makeText(this, "Missing eventId", Toast.LENGTH_SHORT).show();
            return;
        }

        waitlistRepo.addToWaitlist(eventId, userId, new WaitlistRepository.CountListener() {
            @Override
            public void onCount(int total) {
                currentWaitlistCount = total;
//                tvWaitlistCount.setText("Waitlist size: " + total);
                Toast.makeText(EventDetailActivity.this, "Added to waitlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EventDetailActivity.this, "Failed to add to waitlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
