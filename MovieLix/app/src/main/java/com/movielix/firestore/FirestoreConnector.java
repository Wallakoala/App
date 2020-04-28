package com.movielix.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.movielix.constants.Constants;

public class FirestoreConnector {

    private static FirestoreConnector sFirestoreConnector;

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore mDb;

    private FirestoreConnector() {
        mDb = FirebaseFirestore.getInstance();
    }

    public static FirestoreConnector newInstance() {
        if (sFirestoreConnector == null) {
            sFirestoreConnector = new FirestoreConnector();
        }

        return sFirestoreConnector;
    }

    public void getMovies(final FirestoreMoviesObserver observer) {
        mDb.collection("movies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(Constants.TAG, document.getId() + " => " + document.getData());
                    }

                    observer.onSuccess(null);

                } else {
                    Log.w(Constants.TAG, "Error getting documents.", task.getException());
                    observer.onError();
                }
            }
        });
    }
}
