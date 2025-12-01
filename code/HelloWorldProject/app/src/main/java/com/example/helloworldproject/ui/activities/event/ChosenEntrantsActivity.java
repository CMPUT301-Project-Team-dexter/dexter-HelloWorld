package com.example.helloworldproject.ui.activities.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.LotteryRepository;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.ui.utils.ChosenEntrantAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/**
 * Activity that shows the "Chosen Entrants" for a specific event.
 *
 * <p>
 * It displays all profiles whose invite status is "ACCEPTED".
 * If every invite for the event is accepted, this effectively becomes
 * the final list of enrolled entrants for the event.
 * </p>
 *
 * <p>
 * Long-pressing on a row allows the organizer to cancel that entrant,
 * which updates the invitation status to "CANCELLED" and removes them
 * from this list.
 * </p>
 */
public class ChosenEntrantsActivity extends AppCompatActivity {

    private static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";

    /**
     * Helper method to build an Intent for starting this Activity.
     *
     * @param context The caller context (usually another Activity).
     * @param eventId The id of the event whose chosen entrants we want to display.
     */
    public static Intent newIntent(@NonNull Context context, @NonNull String eventId) {
        Intent i = new Intent(context, ChosenEntrantsActivity.class);
        i.putExtra(EXTRA_EVENT_ID, eventId);
        return i;
    }

    // Repository used for all lottery / invitation operations.
    private final LotteryRepository lotteryRepository = new LotteryRepository();

    // Firestore listener for accepted entrants.
    @Nullable
    private ListenerRegistration acceptedListener = null;

    private String eventId;
    private ChosenEntrantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chosen_entrants);

        // Read the eventId passed from EventManageActivity
        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        if (eventId == null) {
            // Without an event id there is nothing meaningful to show.
            Toast.makeText(this, "Event id is missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Setup the top app bar (toolbar)
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Enable the back arrow in the toolbar so users can return to the previous screen.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Configure RecyclerView with a simple vertical list layout.
        RecyclerView recyclerView = findViewById(R.id.recyclerChosen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Attach our adapter to the RecyclerView.
        // We pass in a listener so that long-clicks on rows can trigger "cancel entrant".
        // When the user long-presses a row, ask for confirmation first.
        adapter = new ChosenEntrantAdapter(this::showCancelConfirmDialog);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Start listening to Firestore for the accepted entrants list.
        acceptedListener = lotteryRepository.observeAcceptedEntrants(
            eventId,
            new LotteryRepository.AcceptedEntrantsListener() {
                @Override
                public void onLoaded(List<Profile> entrants) {
                    // Update the adapter whenever Firestore tells us the list has changed.
                    adapter.setEntrants(entrants);
                }

                @Override
                public void onError(Exception e) {
                    // Show a user-friendly error message.
                    Toast.makeText(
                        ChosenEntrantsActivity.this,
                        "Failed to load chosen entrants: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        );
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Detach the Firestore listener when the screen is no longer visible
        // in order to avoid memory leaks and unnecessary network usage.
        if (acceptedListener != null) {
            acceptedListener.remove();
            acceptedListener = null;
        }
    }

    /**
     * Show a confirmation dialog asking the organizer whether they really want
     * to cancel a given accepted entrant.
     *
     * <p>
     * If the organizer confirms, the invitation status is updated to "CANCELLED".
     * As a result, the entrant will disappear from this list but will still be visible
     * in the "Invitation History" screen under the "cancelled" status.
     * </p>
     *
     * @param entrant The entrant that the organizer wants to cancel.
     */
    private void showCancelConfirmDialog(@NonNull Profile entrant) {
        String name = entrant.getName() != null ? entrant.getName() : "this entrant";

        new AlertDialog.Builder(this)
            .setTitle("Cancel entrant")
            .setMessage("Cancel \"" + name + "\" from this event?\n\n" +
                "They will be removed from the chosen list and marked as CANCELLED.")
            .setPositiveButton("Cancel entrant", (dialog, which) -> {
                performCancelEntrant(entrant);
            })
            .setNegativeButton(android.R.string.no, null)
            .show();
    }

    /**
     * Perform the Firestore update that cancels the given entrant.
     *
     * @param entrant The profile to cancel.
     */
    private void performCancelEntrant(@NonNull Profile entrant) {
        String profileId = entrant.getId();
        if (profileId == null) {
            // This should not normally happen, but we guard against null just in case.
            Toast.makeText(
                this,
                "Unable to cancel entrant: profile id is missing.",
                Toast.LENGTH_LONG
            ).show();
            return;
        }

        lotteryRepository.cancelAcceptedEntrant(
            eventId,
            profileId,
            new LotteryRepository.CompletionListener() {
                @Override
                public void onSuccess() {
                    // No extra work is needed here: because the ChosenEntrantsActivity
                    // listens only to "ACCEPTED" invitations, the cancelled entrant
                    // will automatically disappear from the list after Firestore updates.
                    Toast.makeText(
                        ChosenEntrantsActivity.this,
                        "Entrant cancelled successfully.",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(
                        ChosenEntrantsActivity.this,
                        "Failed to cancel entrant: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        );
    }
}
