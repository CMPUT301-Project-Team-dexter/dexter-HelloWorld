package com.example.helloworldproject.ui.activities;

import android.content.Context;
import android.content.Intent;
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
import com.example.helloworldproject.util.DeviceId;

/** Create/Update profile in a single screen. */
public class LoginActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    private EditText etName, etEmail, etPhone;
    private Spinner userGroupSpinner;
    private Button btnSave;
    private ProgressBar progress;

    private ProfileRepository repo;
    private String deviceId; // document ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        boolean skipAutoLogin = getIntent().getBooleanExtra("skip_auto_login", false);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        userGroupSpinner = findViewById(R.id.user_group_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_text_item, UserGroup.getNameList()
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_text_item);
        userGroupSpinner.setAdapter(spinnerAdapter);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setText("Log in");
        progress = findViewById(R.id.progress);

        repo = new ProfileRepository();

        setLoading(true);
        DeviceId.get(new DeviceId.DeviceIdCallback() {
            @Override public void onSuccess(String id) {
                deviceId = id;
                // Try to load existing profile (for update).
                repo.loadByDeviceId(deviceId, new ProfileRepository.LoadCallback() {
                    @Override public void onLoaded(Profile p) {
                        setLoading(false);
                        if (p != null) {
                            etName.setText(p.getName());
                            etEmail.setText(p.getEmail());
                            etPhone.setText(p.getPhone());
                            userGroupSpinner.setSelection(p.getUserGroup().ordinal());
                            CurrentProfile.init(p);
                            if (!skipAutoLogin) {
                                startActivity(HomeActivity.newIntent(LoginActivity.this));
                                finish();
                            }
                            return;
                        }
                        Toast.makeText(
                            LoginActivity.this,
                            "Failed to load: profile is null",
                            Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override public void onNotFound() {
                        btnSave.setText("Register");
                        setLoading(false); // new user: keep fields empty
                    }

                    @Override public void onError(Exception e) {
                        setLoading(false);
                        Toast.makeText(
                            LoginActivity.this,
                            "Failed to get installation id: " + e.getMessage(),
                            Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }

            @Override public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(
                    LoginActivity.this,
                    "Failed to get device ID: " + e.getMessage(),
                    Toast.LENGTH_LONG
                ).show();
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

            setLoading(true);
            Profile p = new Profile(
                deviceId, deviceId, name, email,
                TextUtils.isEmpty(phone) ? null : phone,
                UserGroup.valueOf(
                    spinnerAdapter.getItem(
                        userGroupSpinner.getSelectedItemPosition()
                    )
                )
            );
            repo.saveOrUpdate(p, new ProfileRepository.CompleteCallback() {
                @Override public void onComplete() {
                    setLoading(false);
                    CurrentProfile.init(p);
                    startActivity(HomeActivity.newIntent(LoginActivity.this));
                    finish();
                }

                @Override public void onError(Exception e) {
                    setLoading(false);
                    Toast.makeText(
                        LoginActivity.this,
                        "Save failed: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
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
