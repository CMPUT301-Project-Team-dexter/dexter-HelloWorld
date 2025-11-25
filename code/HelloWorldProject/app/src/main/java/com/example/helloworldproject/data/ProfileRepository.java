package com.example.helloworldproject.data;

import androidx.annotation.NonNull;

import com.example.helloworldproject.model.Event;
import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.util.CurrentProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void deleteProfile(Profile profile, final CompleteCallback cb) {
        String thisDeviceId = profile.getDeviceId();
        WriteBatch batch = db.batch();

        DocumentReference profileDeviceIdRef = db.collection("profiles").document(thisDeviceId);
        batch.delete(profileDeviceIdRef);

        db.collection("events")
                .get()
                .addOnSuccessListener(snap -> {
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    List<QueryDocumentSnapshot> parentEvents = new ArrayList<>(); // keep track of parent events

                    for (QueryDocumentSnapshot d : snap) {
                        parentEvents.add(d); // save the parent event
                        tasks.add(d.getReference().collection("waitlist").get());
                        tasks.add(d.getReference().collection("invites").get());
                    }

                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {

                        for (int i = 0; i < results.size(); i++) {
                            QuerySnapshot subSnap = (QuerySnapshot) results.get(i);

                            for (DocumentSnapshot dd : subSnap.getDocuments()) {
                                String profileId = dd.getString("profileId");
                                if (thisDeviceId.equals(profileId)) {
                                    batch.delete(dd.getReference());
                                }
                            }
                        }
                        batch.commit();
                        cb.onComplete();
                    });
                })
                .addOnFailureListener(cb::onError);
    }
}
