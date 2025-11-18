package com.example.helloworldproject.ui.activities.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.helloworldproject.R;
import com.example.helloworldproject.databinding.ActivityEventQrcodeScanBinding;

public class EventQRCodeScanActivity extends AppCompatActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, EventQRCodeScanActivity.class);
    }

    ActivityEventQrcodeScanBinding binding;
    boolean scanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_qrcode_scan);
        binding.eventQrcodeScanView.decodeContinuous(
            result -> {
                if (result == null) return;
                if (scanned) return;
                scanned = true;
                binding.eventQrcodeScanView.pause();
                String qrText = result.getText();
                if (qrText == null) {
                    qrText = "<empty>";
                }
                startActivity(EventDetailActivity.newIntent(this, qrText));
                finish();
            }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanned = false;
        binding.eventQrcodeScanView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.eventQrcodeScanView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.eventQrcodeScanView.pause();
    }
}
