package com.example.helloworldproject.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.databinding.AccountFragmentBinding;
import com.example.helloworldproject.ui.activities.LoginActivity;
import com.example.helloworldproject.ui.activities.NotificationListActivity;
import com.example.helloworldproject.ui.activities.ProfileEditActivity;
import com.example.helloworldproject.ui.activities.RegisterHistoryActivity;
import com.example.helloworldproject.ui.activities.SettingsActivity;
import com.example.helloworldproject.util.CurrentProfile;

public class AccountFragment extends Fragment {

    private final ProfileRepository profileRepo = new ProfileRepository();
    private AccountFragmentBinding binding;
    private final ActivityResultLauncher<Intent> profileLauncher = getProfileLauncher();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AccountFragmentBinding.inflate(inflater, container, false);

        binding.profileButton.setOnClickListener(v ->
            profileLauncher.launch(new Intent(requireContext(), ProfileEditActivity.class))
        );

        binding.notificationsButton.setOnClickListener(v ->
            startActivity(new Intent(requireContext(), NotificationListActivity.class))
        );

        binding.settingsButton.setOnClickListener(v ->
            startActivity(new Intent(requireContext(), SettingsActivity.class))
        );

        showRegisterHistoryButton();

        binding.regHistoryBtn.setOnClickListener(v ->
            startActivity(RegisterHistoryActivity.newIntent(requireContext()))
        );

        binding.logoutButton.setOnClickListener(v -> {
                Intent intent = LoginActivity.newIntent(requireContext());
                intent.putExtra("skip_auto_login", true);
                startActivity(intent);
                requireActivity().finish();
            }
        );

        binding.deleteAccountButton.setOnClickListener(v ->
            deleteProfileButtonFunction()
        );

        return binding.getRoot();
    }

    private void deleteProfileButtonFunction() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are you sure you would like to delete this profile permanently?")
            .setTitle("Profile Deletion Warning")
            .setPositiveButton(
                "Yes",
                (dialog, id) ->
                    profileRepo.deleteProfile(
                        CurrentProfile.get(),
                        new ProfileRepository.CompleteCallback() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(requireContext(), "Profile successfully deleted.", Toast.LENGTH_LONG).show();
                                startActivity(LoginActivity.newIntent(requireContext()));
                                requireActivity().finish();
                            }

                            @Override
                            public void onError(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    )
            ).setNegativeButton(
                "Cancel", (dialog, id) -> dialog.dismiss()
            )
            .setIcon(android.R.drawable.ic_dialog_alert).create().show();
    }

    private void showRegisterHistoryButton() {
        if (CurrentProfile.isEntrant()) {
            binding.regHistoryBtn.setVisibility(View.VISIBLE);
            binding.regHistorySeparator.setVisibility(View.VISIBLE);
        } else {
            binding.regHistoryBtn.setVisibility(View.GONE);
            binding.regHistorySeparator.setVisibility(View.GONE);
        }
    }

    private ActivityResultLauncher<Intent> getProfileLauncher() {
        return registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    showRegisterHistoryButton();
                }
            }
        );
    }
}
