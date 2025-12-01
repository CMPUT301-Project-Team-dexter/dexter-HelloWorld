package com.example.helloworldproject.data;

import android.net.Uri;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ImageRepository {
    private ImageRepository() {  }

    public static final ImageRepository INSTANCE = new ImageRepository();

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface UriCallback {
        void onSuccess(Uri downloadUrl);
        void onError(Exception e);
    }

    public interface CompleteCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public void uploadImage(Uri imgUri, UriCallback cb) {
        StorageReference storageRef = storage.getReference().child("images/" + UUID.randomUUID().toString() + ".jpg");
        if (imgUri != null) {
            // 1. Start the upload task
            storageRef.putFile(imgUri)
                    .continueWithTask(task -> {
                        // 2. Check for failure during the upload
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // 3. Get the download URL of the successfully uploaded file
                        return storageRef.getDownloadUrl();
                    })
                    .addOnSuccessListener(downloadUri -> {
                        // 4. Report the final URL back to the caller
                        cb.onSuccess(downloadUri);
                    })
                    .addOnFailureListener(cb::onError);
        }
    }

    public void readImageIntoView(Fragment frag, ImageView v, String imgId, UriCallback cb) {
        StorageReference storageRef = storage.getReference().child("images/" + imgId);
        storageRef.getDownloadUrl()
                .addOnSuccessListener(cb::onSuccess)
                .addOnFailureListener(cb::onError);
    }
}
