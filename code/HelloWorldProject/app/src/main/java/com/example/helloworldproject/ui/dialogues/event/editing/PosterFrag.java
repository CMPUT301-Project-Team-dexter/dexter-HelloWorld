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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.activity.result.PickVisualMediaRequest;

import com.example.helloworldproject.R;
import com.example.helloworldproject.databinding.FragPosterEditBinding;

public class PosterFrag extends DialogFragment {

    public PosterFrag() {}

    FragPosterEditBinding binding;

    private ImgSelListener listener;
    private Uri selectedImgUri;
    private ImageView imgView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ImgSelListener) context;
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
            if (selectedImgUri != null) {
                listener.onImgSelected(selectedImgUri);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
