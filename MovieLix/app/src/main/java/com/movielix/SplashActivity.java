package com.movielix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.movielix.constants.Constants;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.signOut();

        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                routeToAppropriateScreen(currentUser != null);
            }
        }, 2000);
    }

    @Override
    public void onStop() {
        super.onStop();

        finish();
    }

    private void routeToAppropriateScreen(boolean signedIn) {
        View title = findViewById(R.id.title);

        if (signedIn){
            Log.d(Constants.TAG, "user is signed in");

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, title, Objects.requireNonNull(ViewCompat.getTransitionName(title)));

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent, options.toBundle());

        } else {
            Log.d(Constants.TAG, "user is NOT signed in");

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, title, Objects.requireNonNull(ViewCompat.getTransitionName(title)));

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent, options.toBundle());
        }
    }
}
