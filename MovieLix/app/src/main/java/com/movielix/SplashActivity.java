package com.movielix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.movielix.constants.Constants;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        routeToAppropriateScreen(currentUser != null);
    }

    private void routeToAppropriateScreen(boolean signedIn) {
        if (signedIn){
            Log.d(Constants.TAG, "User is signed in");

        } else {
            Log.d(Constants.TAG, "User is NOT signed in");

            startActivity(new Intent(this, IntroActivity.class));
        }

        finish();
    }
}
