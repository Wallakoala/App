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
import com.movielix.logging.Logger;
import com.movielix.util.ExternalStorage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Splash screen that routes to the appropiate screen.
 */
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        if (ExternalStorage.isWritable()) {
            File logFile = new File(getExternalCacheDir(), Constants.LOG_FILE);

            if (logFile.exists()) {
                logFile.delete();
            }

            try {
                Logger.init(logFile);

            } catch (IOException e) {
                Log.w(Constants.TAG, "loggerInit:failure " + e);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

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
