package com.movielix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.movielix.constants.Constants;
import com.movielix.login.LoginActivity;
import com.movielix.login.RegisterActivity;

/**
 * Intro screen
 */

public class IntroActivity extends AppCompatActivity {

    private AppCompatButton mRegisterButton;
    private AppCompatButton mLoginButton;

    private View mTitle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        initViews();
    }

    private void initViews()
    {
        mLoginButton = findViewById(R.id.intro_login);
        mRegisterButton = findViewById(R.id.intro_register);
        mTitle = findViewById(R.id.intro_title);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, RegisterActivity.class);

                // Get the title and button coordinates
                int[] titleScreenLocation = new int[2];
                mTitle.getLocationInWindow(titleScreenLocation);

                int[] buttonScreenLocation = new int[2];
                mRegisterButton.getLocationInWindow(buttonScreenLocation);

                intent.putExtra(Constants.PACKAGE + ".leftTitle", titleScreenLocation[0])
                      .putExtra(Constants.PACKAGE + ".topTitle", titleScreenLocation[1])
                      .putExtra(Constants.PACKAGE + ".widthTitle", mTitle.getWidth())
                      .putExtra(Constants.PACKAGE + ".heightTitle", mTitle.getHeight())
                      .putExtra(Constants.PACKAGE + ".leftButton", buttonScreenLocation[0])
                      .putExtra(Constants.PACKAGE + ".topButton", buttonScreenLocation[1])
                      .putExtra(Constants.PACKAGE + ".widthButton", mRegisterButton.getWidth())
                      .putExtra(Constants.PACKAGE + ".heightButton", mRegisterButton.getHeight());

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);

                // Get the title and button coordinates
                int[] titleScreenLocation = new int[2];
                mTitle.getLocationInWindow(titleScreenLocation);

                int[] buttonScreenLocation = new int[2];
                mLoginButton.getLocationInWindow(buttonScreenLocation);

                intent.putExtra(Constants.PACKAGE + ".leftTitle", titleScreenLocation[0])
                      .putExtra(Constants.PACKAGE + ".topTitle", titleScreenLocation[1])
                      .putExtra(Constants.PACKAGE + ".widthTitle", mTitle.getWidth())
                      .putExtra(Constants.PACKAGE + ".heightTitle", mTitle.getHeight())
                      .putExtra(Constants.PACKAGE + ".leftButton", buttonScreenLocation[0])
                      .putExtra(Constants.PACKAGE + ".topButton", buttonScreenLocation[1])
                      .putExtra(Constants.PACKAGE + ".widthButton", mLoginButton.getWidth())
                      .putExtra(Constants.PACKAGE + ".heightButton", mLoginButton.getHeight());

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
