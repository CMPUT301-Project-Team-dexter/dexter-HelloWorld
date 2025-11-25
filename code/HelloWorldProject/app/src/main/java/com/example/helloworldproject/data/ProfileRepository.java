package com.example.helloworldproject.data;

import com.example.helloworldproject.model.Profile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

/** Firestore repository for Profile. */
public class ProfileRepository {
    /** Callback for loading a list of profiles (for admin browse). */
    public interface ListCallback {
        void onLoaded(List<Profile> profiles);
        void onError(Exception e);
    }


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
                .addOnSuccessListener(ds -> {
                    if (ds.exists()) {
                        Profile p = ds.toObject(Profile.class);
                        cb.onLoaded(p);
                    } else {
                        cb.onNotFound();
                    }
                })
                .addOnFailureListener(cb::onError);
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

    public void deleteProfile(Profile profile, final CompleteCallback cb) {
        String docId = profile.getDeviceId();

        db.collection("profiles")
                .document(docId)
                .delete() // The core Firestore operation to delete the document

                // Success listener
                .addOnSuccessListener(unused -> cb.onComplete())

                // Failure listener
                .addOnFailureListener(cb::onError);
    }

    /**
     * Load all profiles in the system (for admin browse).
     */
    public void loadAllProfiles(final ListCallback cb) {
        db.collection("profiles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Profile> result = new ArrayList<>();
                    querySnapshot.getDocuments().forEach(doc -> {
                        Profile p = doc.toObject(Profile.class);
                        if (p != null) {
                            // Ensure the in-memory model has its ID set to the document ID
                            p.setId(doc.getId());
                            result.add(p);
                        }
                    });
                    cb.onLoaded(result);
                })
                .addOnFailureListener(cb::onError);
    }

}
