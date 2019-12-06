package com.movielix.login;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.movielix.MainActivity;
import com.movielix.R;
import com.movielix.constants.Constants;
import com.movielix.font.TypeFace;
import com.movielix.view.TextInputLayout;

/**
 * Login screen
 */

public class LoginActivity extends AppCompatActivity {

    private static final int ENTER_ANIM_DURATION = 350;
    private static final int EXIT_ANIM_DURATION = 250;

    private static final int ENTER_ANIM_OFFSET = 35;
    private static final int EXIT_ANIM_OFFSET = 25;

    private static final int ENTER_ANIM_TRANSLATION = -200;
    private static final int EXIT_ANIM_TRANSLATION = 200;

    /* Views */
    private View mContainer;
    private View mDivider;
    private View mSocialButtons;
    private View mTitle;
    private View mForgotPassword;

    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;

    private AppCompatButton mLoginButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initData();
        initViews();
        initEditText();

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
    public void onBackPressed()
    {
        if (!mExiting)
        {
            runExitAnimation(new Runnable() {
                public void run() {
                    finish();
                }
            });
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();

        mTitleTop    = bundle.getInt(Constants.PACKAGE + ".topTitle");
        mTitleLeft   = bundle.getInt(Constants.PACKAGE + ".leftTitle");
        mTitleWidth  = bundle.getInt(Constants.PACKAGE + ".widthTitle");
        mTitleHeight = bundle.getInt(Constants.PACKAGE + ".heightTitle");

        mButtonTop    = bundle.getInt(Constants.PACKAGE + ".topButton");
        mButtonLeft   = bundle.getInt(Constants.PACKAGE + ".leftButton");
        mButtonWidth  = bundle.getInt(Constants.PACKAGE + ".widthButton");
        mButtonHeight = bundle.getInt(Constants.PACKAGE + ".heightButton");

        mExiting = false;
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

        // Background.
        mBackground = getDrawable(R.drawable.dark_background);
        mContainer.setBackground(mBackground);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                // Get the title and button coordinates
                int[] titleScreenLocation = new int[2];
                mTitle.getLocationInWindow(titleScreenLocation);

                intent.putExtra(Constants.PACKAGE + ".leftTitle", titleScreenLocation[0])
                      .putExtra(Constants.PACKAGE + ".topTitle", titleScreenLocation[1])
                      .putExtra(Constants.PACKAGE + ".widthTitle", mTitle.getWidth())
                      .putExtra(Constants.PACKAGE + ".heightTitle", mTitle.getHeight());

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initEditText() {
        mEmailInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));
        mPasswordInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));
    }

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
}
