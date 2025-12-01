package com.example.helloworldproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.helloworldproject.R;
import com.example.helloworldproject.data.EventRepository;
import com.example.helloworldproject.model.Event;

public class ImageDetailFragment extends Fragment {

    private static final String ARG_EVENT_ID = "arg_event_id";
    private static final String ARG_IMAGE_URL = "arg_image_url";

    private String eventId;
    private String imageUrl;

    // Called from AllImagesFragment when an image is tapped
    public static ImageDetailFragment newInstance(String eventId, String imageUrl) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString(ARG_EVENT_ID);
            imageUrl = args.getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_detail_fragment, container, false);

        ImageView imageView = view.findViewById(R.id.image);
        Button deleteButton = view.findViewById(R.id.delete_button);

        // Show the image
        Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imageView);

        // Wire the delete button
        deleteButton.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(requireContext(),
                        "Event ID is missing, cannot delete image.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove image")
                    .setMessage("Are you sure you want to remove this image from the event?")
                    .setPositiveButton("Delete", (dialog, which) -> performDelete())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    private void performDelete() {
        EventRepository.INSTANCE.asyncLoadById(eventId, new EventRepository.LoadCallback() {
            @Override
            public void onLoaded(Event e) {
                if (e == null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(
                                    requireContext(),
                                    "Event not found.",
                                    Toast.LENGTH_SHORT
                            ).show()
                    );
                    return;
                }

                // Clear all poster-related fields
                e.setImgId(null);
                e.setImgUrl(null);
                e.setImgUrlEnable(false);

                EventRepository.INSTANCE.saveOrUpdate(e, new EventRepository.CompleteCallback() {
                    @Override
                    public void onComplete() {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(
                                    requireContext(),
                                    "Image removed.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            // Go back to the previous screen (All Images grid)
                            requireActivity()
                                    .getSupportFragmentManager()
                                    .popBackStack();
                        });
                    }

                    @Override
                    public void onError(Exception ex) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(
                                        requireContext(),
                                        "Failed to remove image: " + ex.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                        );
                    }
                });
            }

            @Override
            public void onNotFound() {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(
                                requireContext(),
                                "Event not found.",
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(
                                requireContext(),
                                "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}
