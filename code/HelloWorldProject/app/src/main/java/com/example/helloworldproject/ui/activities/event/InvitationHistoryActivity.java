package com.example.helloworldproject.ui.activities.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.LotteryRepository;
import com.example.helloworldproject.model.InvitationRecord;
import com.example.helloworldproject.ui.utils.InvitationHistoryAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity that shows the invitation history for a specific event.
 *
 * <p>
 * It lists all entrants that have been invited (pending, accepted, declined, cancelled).
 * The filter button can be used to narrow the list down to only rejected/cancelled users.
 * </p>
 */
public class InvitationHistoryActivity extends AppCompatActivity {

    private static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";

    /**
     * Helper method to construct an Intent for starting this Activity.
     *
     * @param context The caller context, usually another Activity.
     * @param eventId The id of the event whose invitation history will be displayed.
     */
    public static Intent newIntent(@NonNull Context context, @NonNull String eventId) {
        Intent i = new Intent(context, InvitationHistoryActivity.class);
        i.putExtra(EXTRA_EVENT_ID, eventId);
        return i;
    }

    // Repository that talks to Firestore.
    private final LotteryRepository lotteryRepository = new LotteryRepository();

    // Firestore listener for the invitation history snapshot.
    @Nullable
    private ListenerRegistration invitationListener = null;

    // RecyclerView adapter showing the invitations.
    private InvitationHistoryAdapter adapter;

    // Event id passed from the EventManageActivity.
    private String eventId;

    /**
     * Optional filter on invitation status.
     * When null or empty, all invitations are shown.
     * When set to ["DECLINED","CANCELLED"], only rejected/cancelled invites are shown.
     */
    @Nullable
    private List<String> currentStatusFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation_history);

        // Extract event id from the Intent.
        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        if (eventId == null) {
            Toast.makeText(this, "Event id is missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Setup toolbar and navigation back arrow.
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Configure RecyclerView.
        RecyclerView recyclerView = findViewById(R.id.recyclerInvitations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvitationHistoryAdapter();
        recyclerView.setAdapter(adapter);

        // Setup filter button behaviour.
        ImageButton filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterDialog());

        // NOTE: btnSendNotification exists in the layout but is not wired yet.
        // The notification feature can be implemented later using the same repository.
    }

    @Override
    protected void onStart() {
        super.onStart();
        attachInvitationListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachInvitationListener();
    }

    /**
     * Attach a Firestore snapshot listener using the current status filter.
     * If there is an existing listener, it will be removed first.
     */
    private void attachInvitationListener() {
        detachInvitationListener();

        invitationListener = lotteryRepository.observeInvitationHistory(
            eventId,
            currentStatusFilter,
            new LotteryRepository.InvitationHistoryListener() {
                @Override
                public void onLoaded(List<InvitationRecord> invitations) {
                    // Update adapter with the latest snapshot.
                    adapter.setInvitations(invitations);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(
                        InvitationHistoryActivity.this,
                        "Failed to load invitation history: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        );
    }

    /**
     * Remove the Firestore listener if it is currently attached.
     */
    private void detachInvitationListener() {
        if (invitationListener != null) {
            invitationListener.remove();
            invitationListener = null;
        }
    }

    /**
     * Show a simple dialog that lets the organizer choose how to filter the list.
     *
     * <p>The UX is intentionally minimal:
     * either show all invitations, or only the rejected/cancelled ones.</p>
     */
    private void showFilterDialog() {
        final String[] options = new String[]{
            "Show all invitations",
            "Show only rejected / cancelled"
        };

        // Determine the currently selected option.
        int checkedItem = (currentStatusFilter == null || currentStatusFilter.isEmpty()) ? 0 : 1;

        new AlertDialog.Builder(this)
            .setTitle("Filter invitations")
            .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                dialog.dismiss();

                if (which == 0) {
                    // Show all invitations.
                    currentStatusFilter = null;
                } else {
                    // Only rejected / cancelled invitations.
                    currentStatusFilter = new ArrayList<>(
                        Arrays.asList("DECLINED", "CANCELLED")
                    );
                }

                // Re-attach the listener with the new filter.
                attachInvitationListener();
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }
}
