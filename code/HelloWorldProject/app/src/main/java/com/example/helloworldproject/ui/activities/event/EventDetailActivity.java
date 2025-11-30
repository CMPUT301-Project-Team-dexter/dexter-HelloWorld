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

import com.bumptech.glide.Glide;
import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.data.LotteryRepository;
import com.example.helloworldproject.data.WaitlistRepository;
import com.example.helloworldproject.databinding.ActivityEventDetailBinding;
import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.model.EventStatus;
import com.example.helloworldproject.ui.dialogues.event.QRCodeFrag;
import com.example.helloworldproject.util.CurrentProfile;
import com.example.helloworldproject.util.EventCache;
import com.google.firebase.firestore.ListenerRegistration;

public class EventDetailActivity extends AppCompatActivity {
    private static final String KEY_EVENT_ID = "key_event_id";

    public static Intent newIntent(Context context, @NonNull String eventId) {
        Intent i = new Intent(context, EventDetailActivity.class);
        i.putExtra(KEY_EVENT_ID, eventId);
        return i;
    }

    ActivityEventDetailBinding binding;
    String givenEventId;
    private final WaitlistRepository waitlistRepository = new WaitlistRepository();
    private final LotteryRepository lotteryRepository = new LotteryRepository();
    private ListenerRegistration waitlistCountListener;
    private ListenerRegistration inviteStatusListener;
    private ListenerRegistration inviteSummaryListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        setSupportActionBar(binding.evtDtlToolbar);
        givenEventId = getIntent().getStringExtra(KEY_EVENT_ID);
        if (givenEventId == null) {
            Toast.makeText(this, "Event id is null", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        EventCache.asyncTryGetSingle(
            givenEventId,
            new EventRepository.LoadCallback() {
                @Override
                public void onLoaded(Event e) {
                    onCreateContinued(e);
                }

                @Override
                public void onNotFound() {
                    Toast.makeText(
                        EventDetailActivity.this,
                        "Event with ID " + givenEventId + " not found.",
                        Toast.LENGTH_SHORT
                    ).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(
                        EventDetailActivity.this,
                        "Exception occurs when loading event " + givenEventId + ": " + e.getClass().getCanonicalName(),
                        Toast.LENGTH_SHORT
                    ).show();
                    finish();
                }
            }
        );
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

        String imgUrl = e.getImgUrl();
        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.evtDtlPosterImage);

        if (CurrentProfile.isOrganizer()) {
            binding.evtDtlOrgEditBtn.setOnClickListener(
                v -> startActivity(EventEditingActivity.newIntent(this, givenEventId))
            );
            binding.evtDtlOrgEditBtn.setVisibility(View.VISIBLE);
            setupOrganizerLottery(e);
        } else if (CurrentProfile.isAdmin()) {
            binding.evtDtlAdminDeleteBtn.setOnClickListener(v -> {
                // TODO: implement admin delete functionality
            });
            binding.evtDtlAdminDeleteBtn.setVisibility(View.VISIBLE);
        } else {
            renderLotteryGuidelines(e);
            setupEntrantButtons(e);
            observeInviteStatus();
        }
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
            startActivity(EventManageActivity.newIntent(this, givenEventId));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEntrantButtons(Event event) {
        boolean canJoin = event.getRealTimeStatus() == EventStatus.REGISTRATION_OPEN;
        binding.evtDtlJoinBtn.setVisibility(View.VISIBLE);
        binding.evtDtlLeaveBtn.setVisibility(View.GONE);
        binding.evtDtlJoinBtn.setEnabled(canJoin);

        waitlistRepository.isInWaitlist(
            givenEventId,
            CurrentProfile.get().getId(),
            new WaitlistRepository.MembershipListener() {
                @Override public void onResult(boolean isInWaitlist) {
                    runOnUiThread(() -> updateJoinLeaveVisibility(isInWaitlist, canJoin));
                }

                @Override public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(
                        EventDetailActivity.this,
                        "Failed to load waitlist status: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show());
                }
            }
        );

        binding.evtDtlJoinBtn.setOnClickListener(v -> {
            binding.evtDtlJoinBtn.setEnabled(false);
            waitlistRepository.joinWaitlist(
                givenEventId,
                CurrentProfile.get(),
                new WaitlistRepository.CompletionListener() {
                    @Override public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Joined waiting list",
                                Toast.LENGTH_SHORT
                            ).show();
                            updateJoinLeaveVisibility(true, canJoin);
                        });
                    }

                    @Override public void onError(Exception e) {
                        runOnUiThread(() -> {
                            binding.evtDtlJoinBtn.setEnabled(canJoin);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Failed to join waiting list: " + e.getMessage(),
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            );
        });

        binding.evtDtlLeaveBtn.setOnClickListener(v -> {
            binding.evtDtlLeaveBtn.setEnabled(false);
            waitlistRepository.leaveWaitlist(
                givenEventId,
                CurrentProfile.get().getId(),
                new WaitlistRepository.CompletionListener() {
                    @Override public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Left waiting list",
                                Toast.LENGTH_SHORT
                            ).show();
                            updateJoinLeaveVisibility(false, canJoin);
                        });
                    }

                    @Override public void onError(Exception e) {
                        runOnUiThread(() -> {
                            binding.evtDtlLeaveBtn.setEnabled(true);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Failed to leave waiting list: " + e.getMessage(),
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            );
        });

        waitlistCountListener = waitlistRepository.observeCount(
            givenEventId,
            new WaitlistRepository.CountListener() {
                @Override public void onCount(int total) {
                    runOnUiThread(() -> binding.evtDtlWaitListView.setText(total + " entrants on the waiting list"));
                }

                @Override public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(
                        EventDetailActivity.this,
                        "Failed to load waitlist count: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show());
                }
            }
        );
    }

    private void updateJoinLeaveVisibility(boolean isInWaitlist, boolean canJoin) {
        binding.evtDtlJoinBtn.setVisibility(isInWaitlist ? View.GONE : View.VISIBLE);
        binding.evtDtlLeaveBtn.setVisibility(isInWaitlist ? View.VISIBLE : View.GONE);
        binding.evtDtlJoinBtn.setEnabled(canJoin);
        binding.evtDtlLeaveBtn.setEnabled(true);
    }

    private void renderLotteryGuidelines(Event event) {
        String sb = "Lottery selection guidelines\n" +
            "Draw size: " + (event.getPlannedSampleSize() == null ? "Not set" : event.getPlannedSampleSize()) + "\n" +
            "Selection method: " + (event.getSelectionMethod() == null ? "Random draw" : event.getSelectionMethod()) + "\n" +
            "Seed policy: " + (event.getSeedPolicy() == null ? "Randomly seeded" : event.getSeedPolicy()) + "\n" +
            "Duplicate policy: " + (event.getDuplicatePolicy() == null ? "One entry per profile" : event.getDuplicatePolicy());
        binding.evtDtlLotteryRules.setText(sb);
    }

    private void setupOrganizerLottery(Event event) {
        binding.evtDtlOrgLotterySummary.setVisibility(View.VISIBLE);
        binding.evtDtlOrgRunDrawBtn.setVisibility(View.VISIBLE);
        binding.evtDtlOrgReplacementBtn.setVisibility(View.VISIBLE);

        binding.evtDtlOrgRunDrawBtn.setOnClickListener(v -> {
            binding.evtDtlOrgRunDrawBtn.setEnabled(false);
            int sampleSize = event.getPlannedSampleSize() == null ? 0 : event.getPlannedSampleSize();
            if (sampleSize <= 0) {
                binding.evtDtlOrgRunDrawBtn.setEnabled(true);
                Toast.makeText(this, "Set a positive draw size first", Toast.LENGTH_LONG).show();
                return;
            }
            lotteryRepository.runDraw(
                givenEventId,
                sampleSize,
                new LotteryRepository.DrawCallback() {
                    @Override public void onComplete(int invitedCount) {
                        runOnUiThread(() -> {
                            binding.evtDtlOrgRunDrawBtn.setEnabled(true);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Invited " + invitedCount + " entrant(s)",
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }

                    @Override public void onError(Exception e) {
                        runOnUiThread(() -> {
                            binding.evtDtlOrgRunDrawBtn.setEnabled(true);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Draw failed: " + e.getMessage(),
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            );
        });

        binding.evtDtlOrgReplacementBtn.setOnClickListener(v -> {
            binding.evtDtlOrgReplacementBtn.setEnabled(false);
            lotteryRepository.runDraw(
                givenEventId,
                1,
                new LotteryRepository.DrawCallback() {
                    @Override public void onComplete(int invitedCount) {
                        runOnUiThread(() -> {
                            binding.evtDtlOrgReplacementBtn.setEnabled(true);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Replacement draw invited " + invitedCount + " entrant(s)",
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }

                    @Override public void onError(Exception e) {
                        runOnUiThread(() -> {
                            binding.evtDtlOrgReplacementBtn.setEnabled(true);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Replacement draw failed: " + e.getMessage(),
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            );
        });

        inviteSummaryListener = lotteryRepository.observeInviteSummary(
            givenEventId,
            new LotteryRepository.InviteSummaryListener() {
                @Override public void onLoaded(int pending, int accepted, int declined) {
                    runOnUiThread(() -> binding.evtDtlOrgLotterySummary.setText(
                        "Invites – Pending: " + pending + ", Accepted: " + accepted + ", Declined: " + declined
                    ));
                }

                @Override public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(
                        EventDetailActivity.this,
                        "Failed to load invite summary: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show());
                }
            }
        );
    }

    private void observeInviteStatus() {
        lotteryRepository.fetchInviteStatus(
            givenEventId,
            CurrentProfile.get().getId(),
            new LotteryRepository.InviteStatusListener() {
                @Override public void onLoaded(@Nullable String status) {
                    runOnUiThread(() -> renderInviteStatus(status));
                }

                @Override public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(
                        EventDetailActivity.this,
                        "Failed to load invite status: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show());
                }
            }
        );

        inviteStatusListener = lotteryRepository.observeInviteStatus(
            givenEventId,
            CurrentProfile.get().getId(),
            new LotteryRepository.InviteStatusListener() {
                @Override public void onLoaded(@Nullable String status) {
                    runOnUiThread(() -> renderInviteStatus(status));
                }

                @Override public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(
                        EventDetailActivity.this,
                        "Failed to load invite status: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show());
                }
            }
        );

        binding.evtDtlAcceptInviteBtn.setOnClickListener(v -> {
            binding.evtDtlAcceptInviteBtn.setEnabled(false);
            lotteryRepository.acceptInvite(
                givenEventId,
                CurrentProfile.get().getId(),
                new LotteryRepository.CompletionListener() {
                    @Override public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(EventDetailActivity.this, "Invitation accepted", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override public void onError(Exception e) {
                        runOnUiThread(() -> {
                            binding.evtDtlAcceptInviteBtn.setEnabled(true);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Failed to accept invite: " + e.getMessage(),
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            );
        });

        binding.evtDtlDeclineInviteBtn.setOnClickListener(v -> {
            binding.evtDtlDeclineInviteBtn.setEnabled(false);
            lotteryRepository.declineInvite(
                givenEventId,
                CurrentProfile.get().getId(),
                new LotteryRepository.CompletionListener() {
                    @Override public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(EventDetailActivity.this, "Invitation declined", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override public void onError(Exception e) {
                        runOnUiThread(() -> {
                            binding.evtDtlDeclineInviteBtn.setEnabled(true);
                            Toast.makeText(
                                EventDetailActivity.this,
                                "Failed to decline invite: " + e.getMessage(),
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            );
        });
    }

    private void renderInviteStatus(@Nullable String status) {
        if (status == null) {
            binding.evtDtlInviteStatus.setVisibility(View.VISIBLE);
            binding.evtDtlInviteStatus.setText(
                "Not selected yet. Staying on the waiting list keeps you eligible if more spots open."
            );
            binding.evtDtlAcceptInviteBtn.setVisibility(View.GONE);
            binding.evtDtlDeclineInviteBtn.setVisibility(View.GONE);
            return;
        }

        switch (status) {
            case "PENDING":
                binding.evtDtlInviteStatus.setVisibility(View.VISIBLE);
                binding.evtDtlInviteStatus.setText("You have been selected. Please respond.");
                binding.evtDtlAcceptInviteBtn.setVisibility(View.VISIBLE);
                binding.evtDtlDeclineInviteBtn.setVisibility(View.VISIBLE);
                binding.evtDtlAcceptInviteBtn.setEnabled(true);
                binding.evtDtlDeclineInviteBtn.setEnabled(true);
                break;
            case "ACCEPTED":
                binding.evtDtlInviteStatus.setVisibility(View.VISIBLE);
                binding.evtDtlInviteStatus.setText("You accepted the invitation.");
                binding.evtDtlAcceptInviteBtn.setVisibility(View.GONE);
                binding.evtDtlDeclineInviteBtn.setVisibility(View.GONE);
                break;
            case "DECLINED":
                binding.evtDtlInviteStatus.setVisibility(View.VISIBLE);
                binding.evtDtlInviteStatus.setText("You declined the invitation.");
                binding.evtDtlAcceptInviteBtn.setVisibility(View.GONE);
                binding.evtDtlDeclineInviteBtn.setVisibility(View.GONE);
                break;
            default:
                binding.evtDtlInviteStatus.setVisibility(View.VISIBLE);
                binding.evtDtlInviteStatus.setText("Invitation status: " + status);
                binding.evtDtlAcceptInviteBtn.setVisibility(View.GONE);
                binding.evtDtlDeclineInviteBtn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (waitlistCountListener != null) {
            waitlistCountListener.remove();
        }
        if (inviteStatusListener != null) {
            inviteStatusListener.remove();
        }
        if (inviteSummaryListener != null) {
            inviteSummaryListener.remove();
        }
        super.onDestroy();
    }
}
