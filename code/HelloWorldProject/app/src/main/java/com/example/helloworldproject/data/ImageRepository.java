package com.example.helloworldproject.data;

import android.net.Uri;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.helloworldproject.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ImageRepository {
    private ImageRepository() {  }

    public static final ImageRepository INSTANCE = new ImageRepository();

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface CompleteCallback {
        void onComplete();
        void onError(Exception e);
    }

    public void uploadImage(Uri imgUri, CompleteCallback cb) {
        StorageReference storageRef = storage.getReference().child("images/" + UUID.randomUUID().toString());
        if (imgUri != null) {
            storageRef.putFile(imgUri)
                    .addOnSuccessListener(unused -> cb.onComplete())
                    .addOnFailureListener(cb::onError);
        }
    }

    public void readImageIntoView(Fragment frag, ImageView v, String imgId, CompleteCallback cb) {
        StorageReference storageRef = storage.getReference().child("images/" + imgId);
        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(frag)
                            .load(uri)
                            .error(R.drawable.debug_card_image)
                            .into(v);
                    cb.onComplete();
                })
                .addOnFailureListener(cb::onError);
    }
}
