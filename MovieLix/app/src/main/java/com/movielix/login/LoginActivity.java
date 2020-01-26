package com.movielix.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.movielix.MainActivity;
import com.movielix.R;
import com.movielix.constants.Constants;
import com.movielix.font.TypeFace;
import com.movielix.validator.EmailValidator;
import com.movielix.validator.PasswordValidator;
import com.movielix.validator.Validator;
import com.movielix.view.TextInputLayout;

import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

/**
 * Login screen
 */

public class LoginActivity extends AppCompatActivity {

    private enum AuthError {
        USER_NOT_FOUND,
        OTHER
    }

    private static final int SHAKE_ANIM_DURATION = 350;

    private static final int ENTER_ANIM_DURATION = 350;
    private static final int EXIT_ANIM_DURATION  = 250;

    private static final int ENTER_ANIM_OFFSET = 35;
    private static final int EXIT_ANIM_OFFSET  = 25;

    private static final int ENTER_ANIM_TRANSLATION = -200;
    private static final int EXIT_ANIM_TRANSLATION  = 200;

    /* Firebase */
    private FirebaseAuth mAuth;

    /* Views */
    private View mContainer;
    private View mDivider;
    private View mSocialButtons;
    private View mTitle;
    private View mForgotPassword;

    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;

    private AppCompatEditText mEmailEditText;
    private AppCompatEditText mPasswordEditText;

    private CircularProgressButton mLoginButton;

    private Drawable mBackground;

    /* Data */
    private int mTitleTop;
    private int mTitleLeft;
    private int mTitleWidth;
    private int mTitleHeight;
    private int mLeftDeltaTitle;
    private int mTopDeltaTitle;
    private float mWidthScaleTitle;
    private float mHeightScaleTitle;

    private int mButtonTop;
    private int mButtonLeft;
    private int mButtonWidth;
    private int mButtonHeight;
    private int mLeftDeltaButton;
    private int mTopDeltaButton;
    private float mWidthScaleButton;
    private float mHeightScaleButton;

    private boolean mExiting;
    private boolean mSigningIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initData();
        initViews();
        initEditTexts();

        if (savedInstanceState == null) {
            // Global listener
            final ViewTreeObserver observer = mContainer.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Get the title location and the distance to the target
                    int[] titleScreenLocation = new int[2];
                    mTitle.getLocationOnScreen(titleScreenLocation);
                    mLeftDeltaTitle = mTitleLeft - titleScreenLocation[0];
                    mTopDeltaTitle  = mTitleTop - titleScreenLocation[1];

                    int[] buttonScreenLocation = new int[2];
                    mLoginButton.getLocationOnScreen(buttonScreenLocation);
                    mLeftDeltaButton = mButtonLeft - buttonScreenLocation[0];
                    mTopDeltaButton  = mButtonTop - buttonScreenLocation[1];

                    // Calculate the scale factors
                    mWidthScaleTitle = (float)mTitleWidth / (float)mTitle.getWidth();
                    mHeightScaleTitle = (float)mTitleHeight / (float)mTitle.getHeight();

                    mWidthScaleButton = (float)mButtonWidth / (float) mLoginButton.getWidth();
                    mHeightScaleButton = (float)mButtonHeight / (float) mLoginButton.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (!mExiting) {
            runExitAnimation(new Runnable() {
                public void run() {
                    finish();
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();

        mTitleTop    = Objects.requireNonNull(bundle).getInt(Constants.PACKAGE + ".topTitle");
        mTitleLeft   = bundle.getInt(Constants.PACKAGE + ".leftTitle");
        mTitleWidth  = bundle.getInt(Constants.PACKAGE + ".widthTitle");
        mTitleHeight = bundle.getInt(Constants.PACKAGE + ".heightTitle");

        mButtonTop    = bundle.getInt(Constants.PACKAGE + ".topButton");
        mButtonLeft   = bundle.getInt(Constants.PACKAGE + ".leftButton");
        mButtonWidth  = bundle.getInt(Constants.PACKAGE + ".widthButton");
        mButtonHeight = bundle.getInt(Constants.PACKAGE + ".heightButton");

        mExiting = false;
        mSigningIn = false;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        mContainer           = findViewById(R.id.login_container);
        mDivider             = findViewById(R.id.login_divider);
        mSocialButtons       = findViewById(R.id.login_social_buttons);
        mTitle               = findViewById(R.id.login_title);
        mLoginButton         = findViewById(R.id.login_button);
        mEmailInputLayout    = findViewById(R.id.email_input_layout);
        mPasswordInputLayout = findViewById(R.id.password_input_layout);
        mForgotPassword      = findViewById(R.id.login_forgot_password);
        mEmailEditText       = findViewById(R.id.email_edittext);
        mPasswordEditText    = findViewById(R.id.password_edittext);

        // Background.
        mBackground = getDrawable(R.drawable.dark_background);
        mContainer.setBackground(mBackground);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void initEditTexts() {
        mEmailInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));
        mPasswordInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));

        mEmailEditText.addTextChangedListener(new MyTextWatcher(new EmailValidator(this, mEmailEditText, mEmailInputLayout)));
        mPasswordEditText.addTextChangedListener(new MyTextWatcher(new PasswordValidator(this, mPasswordEditText, mPasswordInputLayout)));
    }

    private void signIn() {
        if (!mSigningIn) {
            if (!new EmailValidator(this, mEmailEditText, mEmailInputLayout).validate()) {
                YoYo.with(Techniques.Shake)
                        .duration(SHAKE_ANIM_DURATION)
                        .playOn(mEmailInputLayout);

            } else if (!new PasswordValidator(this, mPasswordEditText, mPasswordInputLayout).validate()) {
                YoYo.with(Techniques.Shake)
                        .duration(SHAKE_ANIM_DURATION)
                        .playOn(mPasswordInputLayout);

            } else {
                Log.d(Constants.TAG, "validateFields: success");

                mSigningIn = true;
                mLoginButton.startAnimation();

                final String email = Objects.requireNonNull(mEmailEditText.getText()).toString();
                final String password = Objects.requireNonNull(mPasswordEditText.getText()).toString();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(Constants.TAG, "signInWithEmailAndPassword: success");

                            mSigningIn = false;
                            animateSucces(mLoginButton);

                        } else {
                            Log.w(Constants.TAG, "signInWithEmailAndPassword: failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                showError(AuthError.USER_NOT_FOUND);
                            } else {
                                showError(AuthError.OTHER);
                            }

                            mSigningIn = false;
                        }
                    }
                });
            }
        }
    }

    /**
     * Starts the success animation with a circular reveal where the user can advance
     * to the next screen.
     */
    private void animateSucces(@NonNull View origin) {
        int enterButtonX = (origin.getLeft()
                + origin.getRight()) / 2;

        int enterButtonY = (origin.getTop()
                + origin.getBottom()) / 2;

        View background = findViewById(R.id.register_background);

        int radiusReveal = Math.max(background.getWidth(), background.getHeight());

        background.setVisibility(View.VISIBLE);

        Animator animator =
                android.view.ViewAnimationUtils.createCircularReveal(background
                        , enterButtonX
                        , enterButtonY
                        , 0
                        , radiusReveal);

        animator.setDuration(500);
        animator.setInterpolator(
                AnimationUtils.loadInterpolator(LoginActivity.this, R.anim.accelerator_interpolator));

        animator.start();

        background.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Move to the next screen
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                startActivity(intent);

                finish();
            }
        });
    }

    /**
     * Shows an error message when signing in.
     */
    private void showError(AuthError error) {
        // Show the retry icon in the button
        mLoginButton.revertAnimation();
        mLoginButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_fill, getTheme()));

        // And show the snackbar
        Snackbar snackbar;
        if (error == AuthError.USER_NOT_FOUND) {
            snackbar = Snackbar.make(mContainer, R.string.user_not_found, Snackbar.LENGTH_SHORT);

        } else {
            snackbar = Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }

        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent, getTheme()));
        snackbar.getView().setBackgroundColor(getColor(R.color.colorPrimaryMedium));

        snackbar.show();
    }

    /**
     * Shows the initial animation when the activity is created.
     */
    private void runEnterAnimation() {
        mTitle.setPivotX(0);
        mTitle.setPivotY(0);
        mTitle.setScaleX(mWidthScaleTitle);
        mTitle.setScaleY(mHeightScaleTitle);
        mTitle.setTranslationX(mLeftDeltaTitle);
        mTitle.setTranslationY(mTopDeltaTitle);

        mLoginButton.setPivotX(0);
        mLoginButton.setPivotY(0);
        mLoginButton.setScaleX(mWidthScaleButton);
        mLoginButton.setScaleY(mHeightScaleButton);
        mLoginButton.setTranslationX(mLeftDeltaButton);
        mLoginButton.setTranslationY(mTopDeltaButton);

        mEmailInputLayout.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mPasswordInputLayout.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mDivider.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mSocialButtons.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mForgotPassword.setTranslationY(-ENTER_ANIM_TRANSLATION);

        mEmailInputLayout.setAlpha(0.0f);
        mPasswordInputLayout.setAlpha(0.0f);
        mDivider.setAlpha(0.0f);
        mSocialButtons.setAlpha(0.0f);
        mForgotPassword.setAlpha(0.0f);

        mTitle.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION)
                .scaleX(1).scaleY(1)
                .translationX(0).translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator());

        mEmailInputLayout.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET)
                .setStartDelay(ENTER_ANIM_OFFSET)
                .setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION)
                .alpha(1.0f);

        mPasswordInputLayout.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 2)
                .setStartDelay(ENTER_ANIM_OFFSET * 2)
                .setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION)
                .alpha(1.0f);

        mForgotPassword.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 3)
                .setStartDelay(ENTER_ANIM_OFFSET * 3)
                .setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION)
                .alpha(1.0f);

        mLoginButton.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 4)
                .setStartDelay(ENTER_ANIM_OFFSET * 4)
                .scaleX(1).scaleY(1)
                .translationX(0).translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator());

        mDivider.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 5)
                .setStartDelay(ENTER_ANIM_OFFSET * 5)
                .setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION)
                .alpha(1.0f);

        mSocialButtons.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 6)
                .setStartDelay(ENTER_ANIM_OFFSET * 6)
                .setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION)
                .alpha(1.0f);
    }

    /**
     * Shows the exit animation when the activity is destroyed.
     */
    private void runExitAnimation(final Runnable endAction) {
        mExiting = true;

        mSocialButtons.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION )
                .setStartDelay(0)
                .setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION)
                .alpha(0.0f);

        mDivider.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET)
                .setStartDelay(EXIT_ANIM_OFFSET)
                .setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION)
                .alpha(0.0f);

        mLoginButton.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 2)
                .setStartDelay(EXIT_ANIM_OFFSET * 2)
                .scaleX(mWidthScaleButton).scaleY(mHeightScaleButton)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationX(mLeftDeltaButton).translationY(mTopDeltaButton);

        mForgotPassword.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 3)
                .setStartDelay(EXIT_ANIM_OFFSET * 3)
                .setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION)
                .alpha(0.0f);

        mPasswordInputLayout.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 4)
                .setStartDelay(EXIT_ANIM_OFFSET * 4)
                .setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION)
                .alpha(0.0f);

        mEmailInputLayout.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 5)
                .setStartDelay(EXIT_ANIM_OFFSET * 5)
                .setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION)
                .alpha(0.0f);

        mTitle.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 6)
                .setStartDelay(EXIT_ANIM_OFFSET * 6)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .scaleX(mWidthScaleTitle).scaleY(mHeightScaleTitle)
                .translationX(mLeftDeltaTitle).translationY(mTopDeltaTitle)
                .withEndAction(endAction);

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(EXIT_ANIM_DURATION - 225);
        bgAnim.setStartDelay(225);
        bgAnim.start();
    }

    /**
     * Custom TextWatcher to validate the fields.
     */
    private class MyTextWatcher implements TextWatcher {

        private Validator validator;

        private MyTextWatcher(Validator validator) {
            this.validator = validator;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            validator.validate();
        }
    }
}
