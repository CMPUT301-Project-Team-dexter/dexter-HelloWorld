package com.example.helloworldproject.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworldproject.R;
import com.example.helloworldproject.data.ProfileRepository;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.model.UserGroup;
import com.example.helloworldproject.util.CurrentProfile;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone;
    private Spinner userGroupSpinner;
    private Button btnSave;
    private ProgressBar progress;

    private ProfileRepository repo;
    private ArrayAdapter<String> spinnerAdapter;
    private Profile workingProfile;
    private String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        userGroupSpinner = findViewById(R.id.user_group_spinner);
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_text_item, UserGroup.getNameList());
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_text_item);
        userGroupSpinner.setAdapter(spinnerAdapter);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setText("Save");
        progress = findViewById(R.id.progress);

        repo = new ProfileRepository();

        try {
            deviceId = CurrentProfile.get().getDeviceId();
        } catch (Exception e) {
            Toast.makeText(this, "No current profile. Please log in first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setLoading(true);
        repo.loadByDeviceId(deviceId, new ProfileRepository.LoadCallback() {
            @Override public void onLoaded(Profile p) {
                setLoading(false);
                if (p == null) {
                    Toast.makeText(ProfileEditActivity.this, "Profile is null.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                workingProfile = p;
                etName.setText(p.getName());
                etEmail.setText(p.getEmail());
                etPhone.setText(p.getPhone());
                if (p.getUserGroup() != null) {
                    userGroupSpinner.setSelection(p.getUserGroup().ordinal());
                }
            }
            @Override public void onNotFound() {
                setLoading(false);
                workingProfile = new Profile(deviceId, deviceId, "", "", null, UserGroup.ENTRANT);
            }
            @Override public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(ProfileEditActivity.this, "Failed to load: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Please enter your name");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Please enter your email");
                return;
            }
            if (workingProfile == null) {
                Toast.makeText(this, "Profile not loaded.", Toast.LENGTH_LONG).show();
                return;
            }
            setLoading(true);
            workingProfile.setId(deviceId);
            workingProfile.setDeviceId(deviceId);
            workingProfile.setName(name);
            workingProfile.setEmail(email);
            workingProfile.setPhone(TextUtils.isEmpty(phone) ? null : phone);
            workingProfile.setUserGroup(UserGroup.valueOf(spinnerAdapter.getItem(userGroupSpinner.getSelectedItemPosition())));
            repo.saveOrUpdate(workingProfile, new ProfileRepository.CompleteCallback() {
                @Override public void onComplete() {
                    setLoading(false);
                    CurrentProfile.init(workingProfile);
                    Toast.makeText(ProfileEditActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                @Override public void onError(Exception e) {
                    setLoading(false);
                    Toast.makeText(ProfileEditActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
        etName.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPhone.setEnabled(!loading);
        userGroupSpinner.setEnabled(!loading);
    }
}
