package com.movielix;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        routeToAppropiateScreen(false);
    }

    private void routeToAppropiateScreen(boolean loggedIn) {
        if (loggedIn){

        } else {
            Intent intent = new Intent(this, IntroActivity.class);

            startActivity(intent);
        }

        finish();
    }
}
