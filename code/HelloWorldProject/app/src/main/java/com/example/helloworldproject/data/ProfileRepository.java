package com.example.helloworldproject.data;

import androidx.annotation.NonNull;

import com.example.helloworldproject.model.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

/** Firestore repository for Profile. */
public class ProfileRepository {

    public interface LoadCallback {
        void onLoaded(Profile profile);
        void onNotFound(); // document does not exist
        void onError(Exception e);
    }

    public interface CompleteCallback {
        void onComplete();
        void onError(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadByDeviceId(String deviceId, final LoadCallback cb) {
        db.collection("profiles").document(deviceId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override public void onSuccess(DocumentSnapshot ds) {
                        if (ds.exists()) {
                            Profile p = ds.toObject(Profile.class);
                            cb.onLoaded(p);
                        } else {
                            cb.onNotFound();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(@NonNull Exception e) {
                        cb.onError(e);
                    }
                });
    }

    /** Create or update (merge) the profile by deviceId. */
    public void saveOrUpdate(Profile profile, final CompleteCallback cb) {
        String docId = profile.getDeviceId();
        profile.setId(docId);
        db.collection("profiles")
                .document(docId)
                .set(profile, SetOptions.merge())
                .addOnSuccessListener(unused -> cb.onComplete())
                .addOnFailureListener(cb::onError);
    }
}
