package com.example.helloworldproject.ui.dialogues.event.editing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;

import com.example.helloworldproject.R;
import com.example.helloworldproject.databinding.FragPosterEditBinding;
import com.example.helloworldproject.ui.activities.event.EventEditListener;

public class PosterFrag extends DialogFragment {

    FragPosterEditBinding binding;
    private EventEditListener listener;
    private Uri selectedImgUri;
    private ImageView imgView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    public PosterFrag() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EventEditListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + " must implement ImageSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                selectedImgUri = uri;
                imgView.setImageURI(selectedImgUri);

                requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                selectedImgUri = null;
                imgView.setImageResource(R.drawable.placeholder);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragPosterEditBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        imgView = binding.imgContainer;

        if (selectedImgUri != null) {
            binding.imgContainer.setImageURI(selectedImgUri);
        }

        // Set initial state
        if (selectedImgUri != null) {
            imgView.setImageURI(selectedImgUri);
        }

        Button btnChoose = binding.selectImgBtn;
        btnChoose.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
        });

        Button btnConfirm = binding.confirmBtn;
        btnConfirm.setOnClickListener(v -> {
            int checkedId = binding.radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(getContext(), "Source of image is not selected.", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.disable_radio_btn) {
                if (selectedImgUri != null) {
                    listener.updateImgUri(selectedImgUri);
                    listener.updateImgUrlEnable(false);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Please select an image first.", Toast.LENGTH_SHORT).show();
                }
            } else if (checkedId == R.id.enable_radio_btn) {
                String value = binding.editTextInput.getText().toString().strip();
                if (value.isEmpty()) {
                    Toast.makeText(getContext(), "Number can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    listener.updateImgUrlEnable(true);
                    listener.updateImgUrl(value);
                }
            }
        });

        binding.disableRadioBtn.setOnClickListener(v -> {
            binding.enableRadioBtn.setChecked(false);
            binding.disableRadioBtn.setChecked(true);
        });

        binding.enableRadioBtn.setOnClickListener(v -> {
            binding.disableRadioBtn.setChecked(false);
            binding.enableRadioBtn.setChecked(true);
        });

        return view;
    }
}
