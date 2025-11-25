package com.example.helloworldproject.ui.dialogues.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.helloworldproject.databinding.FragEventQrCodeBinding;
import com.example.helloworldproject.model.Event;

public class QRCodeFrag extends DialogFragment {
    Event event;
    FragEventQrCodeBinding binding;

    public QRCodeFrag(Event e) {
        this.event = e;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragEventQrCodeBinding.inflate(getLayoutInflater());
        binding.eventQrCodeImg.setImageBitmap(
            event.getQRCodeBitmap()
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(binding.getRoot())
            .setTitle("Event QR Code")
            .setNegativeButton("Close", null)
            .setNeutralButton("Save", (dialog, which) -> {
//                binding.eventQrCodeImg.buildDrawingCache();
//                DatabaseUtils.(
//                    requireContext(),
//                    binding.eventQrCodeImg.getDrawingCache(),
//                    "event_qr_" + eventId + ".png"
//                );
            })
            .create();
    }
}
