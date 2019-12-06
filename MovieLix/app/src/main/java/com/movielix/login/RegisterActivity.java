package com.movielix.login;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.movielix.R;
import com.movielix.constants.Constants;
import com.movielix.view.TextInputLayout;
import com.movielix.font.TypeFace;

import java.util.Objects;

/**
 * Register screen
 */

public class RegisterActivity extends AppCompatActivity {

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

    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;

    private AppCompatButton mRegisterButton;

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

        setContentView(R.layout.activity_register);

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
                    mRegisterButton.getLocationOnScreen(buttonScreenLocation);
                    mLeftDeltaButton = mButtonLeft - buttonScreenLocation[0];
                    mTopDeltaButton  = mButtonTop - buttonScreenLocation[1];

                    // Calculate the scale factors
                    mWidthScaleTitle = (float)mTitleWidth / (float)mTitle.getWidth();
                    mHeightScaleTitle = (float)mTitleHeight / (float)mTitle.getHeight();

                    mWidthScaleButton = (float)mButtonWidth / (float)mRegisterButton.getWidth();
                    mHeightScaleButton = (float)mButtonHeight / (float)mRegisterButton.getHeight();

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

        mTitleTop    = Objects.requireNonNull(bundle).getInt(Constants.PACKAGE + ".topTitle");
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
        mContainer           = findViewById(R.id.register_container);
        mDivider             = findViewById(R.id.register_divider);
        mSocialButtons       = findViewById(R.id.register_social_buttons);
        mTitle               = findViewById(R.id.register_title);
        mRegisterButton      = findViewById(R.id.register_button);
        mEmailInputLayout    = findViewById(R.id.email_input_layout);
        mPasswordInputLayout = findViewById(R.id.password_input_layout);

        mBackground = getDrawable(R.drawable.dark_background);
        mContainer.setBackground(mBackground);
    }

    private void initEditText() {
        mEmailInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));
        mPasswordInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));
    }

    private void runEnterAnimation() {
        // Animate the change of background
        final AnimationDrawable drawable = new AnimationDrawable();

        drawable.addFrame(Objects.requireNonNull(getDrawable(R.drawable.rounded_button_border)), 0);
        drawable.addFrame(Objects.requireNonNull(getDrawable(R.drawable.rounded_button_fill)), 0);
        drawable.setOneShot(true);
        drawable.setEnterFadeDuration(ENTER_ANIM_DURATION);
        drawable.setExitFadeDuration(ENTER_ANIM_DURATION);

        mRegisterButton.setBackground(drawable);
        mTitle.setPivotX(0);
        mTitle.setPivotY(0);
        mTitle.setScaleX(mWidthScaleTitle);
        mTitle.setScaleY(mHeightScaleTitle);
        mTitle.setTranslationX(mLeftDeltaTitle);
        mTitle.setTranslationY(mTopDeltaTitle);

        mRegisterButton.setPivotX(0);
        mRegisterButton.setPivotY(0);
        mRegisterButton.setScaleX(mWidthScaleButton);
        mRegisterButton.setScaleY(mHeightScaleButton);
        mRegisterButton.setTranslationX(mLeftDeltaButton);
        mRegisterButton.setTranslationY(mTopDeltaButton);

        mEmailInputLayout.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mPasswordInputLayout.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mDivider.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mSocialButtons.setTranslationY(-ENTER_ANIM_TRANSLATION);

        mEmailInputLayout.setAlpha(0.0f);
        mPasswordInputLayout.setAlpha(0.0f);
        mDivider.setAlpha(0.0f);
        mSocialButtons.setAlpha(0.0f);

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

        mRegisterButton.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 3)
                .setStartDelay(ENTER_ANIM_OFFSET * 3)
                .scaleX(1).scaleY(1)
                .translationX(0).translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator());

        mDivider.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 4)
                .setStartDelay(ENTER_ANIM_OFFSET * 4)
                .setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION)
                .alpha(1.0f);

        mSocialButtons.animate()
                .withLayer()
                .setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 5)
                .setStartDelay(ENTER_ANIM_OFFSET * 5)
                .setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION)
                .alpha(1.0f);

        drawable.start();
    }

    private void runExitAnimation(final Runnable endAction) {
        mExiting = true;

        final AnimationDrawable drawable = new AnimationDrawable();

        drawable.addFrame(Objects.requireNonNull(getDrawable(R.drawable.rounded_button_fill)), 0);
        drawable.addFrame(Objects.requireNonNull(getDrawable(R.drawable.rounded_button_border)), 0);
        drawable.setOneShot(true);
        drawable.setEnterFadeDuration(EXIT_ANIM_DURATION);
        drawable.setExitFadeDuration(EXIT_ANIM_DURATION);

        mRegisterButton.setBackground(drawable);
        mRegisterButton.setTextColor(getColor(R.color.colorAccent));

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

        mRegisterButton.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 2)
                .setStartDelay(EXIT_ANIM_OFFSET * 2)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .scaleX(mWidthScaleButton).scaleY(mHeightScaleButton)
                .translationX(mLeftDeltaButton).translationY(mTopDeltaButton);

        mPasswordInputLayout.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 3)
                .setStartDelay(EXIT_ANIM_OFFSET * 3)
                .setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION)
                .alpha(0.0f);

        mEmailInputLayout.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 4)
                .setStartDelay(EXIT_ANIM_OFFSET * 4)
                .setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION)
                .alpha(0.0f);

        mTitle.animate()
                .withLayer()
                .setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 5)
                .setStartDelay(EXIT_ANIM_OFFSET * 5)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .scaleX(mWidthScaleTitle).scaleY(mHeightScaleTitle)
                .translationX(mLeftDeltaTitle).translationY(mTopDeltaTitle)
                .withEndAction(endAction);

        drawable.start();

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(EXIT_ANIM_DURATION - 225);
        bgAnim.setStartDelay(225);
        bgAnim.start();
    }
}
