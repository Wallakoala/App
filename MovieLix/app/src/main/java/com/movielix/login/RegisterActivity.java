package com.movielix.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
import androidx.core.content.res.ResourcesCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.movielix.MainActivity;
import com.movielix.R;
import com.movielix.bean.User;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.IFirestoreListener;
import com.movielix.validator.EmailValidator;
import com.movielix.validator.NameValidator;
import com.movielix.validator.PasswordValidator;
import com.movielix.validator.Validator;
import com.movielix.view.TextInputLayout;
import com.movielix.font.TypeFace;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

/**
 * Register screen. This activity provides all the functionality to register a
 * new user.
 *
 * We support: Email - Twitter - Facebook - Google
 */
public class RegisterActivity extends AppCompatActivity implements IFirestoreListener<User> {

    private static final int RC_GOOGLE = 0xA5;
    private static final int RC_FACEBOOK = 64206;

    // Types of authentication.
    private enum AuthType {
        EMAIL_AND_PASSWORD,
        TWITTER,
        FACEBOOK,
        GOOGLE,
        FIRESTORE
    }

    // Types of authentication errors.
    private enum AuthError {
        EMAIL_ALREADY_REGISTERED,
        FIRESTORE,
        OTHER,
    }

    // Set of durations for the different animations.
    private static final int SHAKE_ANIM_DURATION = 350;
    private static final int ENTER_ANIM_DURATION = 350;
    private static final int EXIT_ANIM_DURATION = 250;
    private static final int ENTER_ANIM_OFFSET = 35;
    private static final int EXIT_ANIM_OFFSET = 25;
    private static final int ENTER_ANIM_TRANSLATION = -200;
    private static final int EXIT_ANIM_TRANSLATION = 200;

    // FirebaseAuth object to interact with Firebase.
    private FirebaseAuth mFirebaseAuth;

    // Facebook SDK
    private CallbackManager mCallbackManager;

    /* Views */
    private View mContainer;
    private View mDivider;
    private View mSocialButtons;
    private View mTitle;

    private TextInputLayout mNameInputLayout;
    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;

    private AppCompatEditText mNameEditText;
    private AppCompatEditText mEmailEditText;
    private AppCompatEditText mPasswordEditText;

    private CircularProgressButton mRegisterButton;

    private Drawable mBackground;

    // Variables needed for some animations
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

    // Flag to indicate if the Activity is exiting.
    private boolean mExiting;
    // Flag to indicate if the sign up process has started.
    private boolean mRegistering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        initData();
        initViews();

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
                    mTopDeltaTitle = mTitleTop - titleScreenLocation[1];

                    int[] buttonScreenLocation = new int[2];
                    mRegisterButton.getLocationOnScreen(buttonScreenLocation);
                    mLeftDeltaButton = mButtonLeft - buttonScreenLocation[0];
                    mTopDeltaButton = mButtonTop - buttonScreenLocation[1];

                    // Calculate the scale factors
                    mWidthScaleTitle = (float) mTitleWidth / (float) mTitle.getWidth();
                    mHeightScaleTitle = (float) mTitleHeight / (float) mTitle.getHeight();

                    mWidthScaleButton = (float) mButtonWidth / (float) mRegisterButton.getWidth();
                    mHeightScaleButton = (float) mButtonHeight / (float) mRegisterButton.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * Method called when back button is pressed.
     *
     * It runs the exit animation.
     */
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

    /**
     * Method called when either the Google or Facebook activity has finished the
     * sign-in process.
     *
     * From this point the process continues.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        // GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE) {
            if (resultCode == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    finishAuthWithGoogle(Objects.requireNonNull(account));

                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(Constants.TAG, "FirebaseAuth with Google failed", e);
                    Log.e(Constants.TAG, " - Status code: " + e.getStatusCode());

                    showError(AuthType.GOOGLE, AuthError.OTHER);
                }

            } else {
                mRegistering = false;
                mRegisterButton.revertAnimation();
                mRegisterButton.setBackground(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_fill, getTheme()));

                Log.w(Constants.TAG, "Google sign in failed");
                Log.e(Constants.TAG, " - Status code: " + requestCode);

                showError(AuthType.GOOGLE, AuthError.OTHER);
            }

        } else if (requestCode == RC_FACEBOOK) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Method that initializes the variables.
     */
    private void initData() {
        Bundle bundle = getIntent().getExtras();

        mTitleTop = Objects.requireNonNull(bundle).getInt(Constants.PACKAGE + ".topTitle");
        mTitleLeft = bundle.getInt(Constants.PACKAGE + ".leftTitle");
        mTitleWidth = bundle.getInt(Constants.PACKAGE + ".widthTitle");
        mTitleHeight = bundle.getInt(Constants.PACKAGE + ".heightTitle");

        mButtonTop = bundle.getInt(Constants.PACKAGE + ".topButton");
        mButtonLeft = bundle.getInt(Constants.PACKAGE + ".leftButton");
        mButtonWidth = bundle.getInt(Constants.PACKAGE + ".widthButton");
        mButtonHeight = bundle.getInt(Constants.PACKAGE + ".heightButton");

        mExiting = false;
        mRegistering = false;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Method that initializes and sets up all the UI views.
     */
    private void initViews() {
        mContainer = findViewById(R.id.register_container);
        mDivider = findViewById(R.id.register_divider);
        mSocialButtons = findViewById(R.id.register_social_buttons);
        mTitle = findViewById(R.id.register_title);
        mRegisterButton = findViewById(R.id.register_button);
        mNameInputLayout = findViewById(R.id.name_input_layout);
        mEmailInputLayout = findViewById(R.id.email_input_layout);
        mPasswordInputLayout = findViewById(R.id.password_input_layout);
        mNameEditText = findViewById(R.id.name_edittext);
        mEmailEditText = findViewById(R.id.email_edittext);
        mPasswordEditText = findViewById(R.id.password_edittext);

        mBackground = ResourcesCompat.getDrawable(getResources(), R.drawable.dark_background, getTheme());
        mContainer.setBackground(mBackground);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(AuthType.EMAIL_AND_PASSWORD);
            }
        });

        findViewById(R.id.google_auth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(AuthType.GOOGLE);
            }
        });

        findViewById(R.id.facebook_auth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(AuthType.FACEBOOK);
            }
        });

        findViewById(R.id.twitter_auth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(AuthType.TWITTER);
            }
        });

        initEditTexts();
    }

    /**
     * Method that initialzies the EditTexts. It basically sets the correct font and
     * listeners.
     */
    private void initEditTexts() {
        mNameInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));
        mEmailInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));
        mPasswordInputLayout.setTypeface(TypeFace.getTypeFace(this, "Raleway-Light.ttf"));

        mNameEditText
                .addTextChangedListener(new MyTextWatcher(new NameValidator(this, mNameEditText, mNameInputLayout)));
        mEmailEditText
                .addTextChangedListener(new MyTextWatcher(new EmailValidator(this, mEmailEditText, mEmailInputLayout)));
        mPasswordEditText.addTextChangedListener(
                new MyTextWatcher(new PasswordValidator(this, mPasswordEditText, mPasswordInputLayout)));
    }

    /**
     * Generic method for authenticating. This method is called directly when
     * clicking any of the authentication buttons, or when there is an error and the
     * user retried the operation.
     *
     * @param authType: type of authentication to be done.
     */
    private void register(AuthType authType) {
        if (!mRegistering) {
            if ((authType == AuthType.EMAIL_AND_PASSWORD)
                    && !new NameValidator(this, mNameEditText, mNameInputLayout).validate()) {
                YoYo.with(Techniques.Shake).duration(SHAKE_ANIM_DURATION).playOn(mNameInputLayout);

            } else if ((authType == AuthType.EMAIL_AND_PASSWORD)
                    && !new EmailValidator(this, mEmailEditText, mEmailInputLayout).validate()) {
                YoYo.with(Techniques.Shake).duration(SHAKE_ANIM_DURATION).playOn(mEmailInputLayout);

            } else if ((authType == AuthType.EMAIL_AND_PASSWORD)
                    && !new PasswordValidator(this, mPasswordEditText, mPasswordInputLayout).validate()) {
                YoYo.with(Techniques.Shake).duration(SHAKE_ANIM_DURATION).playOn(mPasswordInputLayout);

            } else {
                Log.d(Constants.TAG, "validateFields: success");

                mRegistering = true;
                // Start the animation of the register button
                mRegisterButton.startAnimation();

                switch (authType) {
                case EMAIL_AND_PASSWORD:
                    registerWithEmailAndPassword();
                    break;

                case GOOGLE:
                    registerWithGoogle();
                    break;

                case FACEBOOK:
                    registerWithFacebook();
                    break;

                case TWITTER:
                    registerWithTwitter();
                    break;

                case FIRESTORE:
                    registerWithFirestore();
                    break;
                }
            }
        }
    }

    /**
     * Registers the user using the email and password using Firebase. Once it
     * succeds, it needs to add the user to Firestore.
     */
    private void registerWithEmailAndPassword() {
        // Get all the fields
        final String name = Objects.requireNonNull(mNameEditText.getText()).toString();
        final String email = Objects.requireNonNull(mEmailEditText.getText()).toString();
        final String password = Objects.requireNonNull(mPasswordEditText.getText()).toString();

        // Let Firebase do its thing
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(Constants.TAG, "[RegisterActivity:registerWithEmailAndPassword]: success");
                            final FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(Constants.TAG,
                                                            "[RegisterActivity:registerWithEmailAndPassword]: Update succeeded");

                                                    registerWithFirestore();
                                                }
                                            }
                                        });

                            } else {
                                Log.wtf(Constants.TAG,
                                        "[RegisterActivity:registerWithEmailAndPassword]: user is null after creation");

                                showError(AuthType.EMAIL_AND_PASSWORD, AuthError.OTHER);
                            }

                        } else {
                            Log.w(Constants.TAG, "[RegisterActivity:registerWithEmailAndPassword]: failure",
                                    task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                showError(AuthType.EMAIL_AND_PASSWORD, AuthError.EMAIL_ALREADY_REGISTERED);
                            } else {
                                showError(AuthType.EMAIL_AND_PASSWORD, AuthError.OTHER);
                            }
                        }
                    }
                });
    }

    /**
     * Registers the user using Google Auth. This will open a new Activity, the
     * process will continue on @see onActivityResult.
     */
    private void registerWithGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE);
    }

    /**
     * Signs in the user using Facebook Auth. This will open a new Activity, the
     * process will continue on @see onActivityResult.
     */
    private void registerWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this, Collections.singleton("email"));

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(Constants.TAG, "[RegisterActivity:registerWithFacebook]: Success");

                finishAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(Constants.TAG, "[RegisterActivity:registerWithFacebook]: Cancelled");

                mRegistering = false;
                mRegisterButton.revertAnimation();
                mRegisterButton.setBackground(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_fill, getTheme()));
            }

            @Override
            public void onError(FacebookException exception) {
                Log.w(Constants.TAG, "[RegisterActivity:registerWithFacebook]: Error: ", exception);

                showError(AuthType.FACEBOOK, AuthError.OTHER);
            }
        });
    }

    /**
     * Registers the user using Twitter Auth.
     */
    private void registerWithTwitter() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        Task<AuthResult> pendingResultTask = mFirebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d(Constants.TAG, "[RegisterActivity:registerWithTwitter]: success");
                    // User is signed in.
                    registerWithFirestore();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(Constants.TAG, "twitterAuth:failure " + e);
                    // Handle failure.
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        showError(AuthType.TWITTER, AuthError.EMAIL_ALREADY_REGISTERED);
                    } else {
                        showError(AuthType.TWITTER, AuthError.OTHER);
                    }
                }
            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            mFirebaseAuth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d(Constants.TAG, "[RegisterActivity:registerWithTwitter]: success");
                            // User is signed in.
                            registerWithFirestore();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(Constants.TAG, "[RegisterActivity:registerWithTwitter]: Error " + e);
                            // Handle failure.
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                showError(AuthType.TWITTER, AuthError.EMAIL_ALREADY_REGISTERED);
                            } else {
                                showError(AuthType.TWITTER, AuthError.OTHER);
                            }
                        }
                    });
        }
    }

    /**
     * Method that finishes the authentication started with Google.
     */
    private void finishAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(Constants.TAG, "[RegisterActivity:finishAuthWithGoogle] Success, id = " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Constants.TAG, "[RegisterActivity:finishAuthWithGoogle]: success");

                            registerWithFirestore();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Constants.TAG, "[RegisterActivity:finishAuthWithGoogle]: Error: ",
                                    task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                showError(AuthType.GOOGLE, AuthError.EMAIL_ALREADY_REGISTERED);
                            } else {
                                showError(AuthType.GOOGLE, AuthError.OTHER);
                            }
                        }
                    }
                });
    }

    /**
     * Method that finishes the authentication started with Facebook.
     */
    private void finishAuthWithFacebook(final AccessToken token) {
        Log.d(Constants.TAG, "[RegisterActivity:finishAuthWithFacebook] Success, token = " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Constants.TAG, "[RegisterActivity:finishAuthWithFacebook]: success");

                            registerWithFirestore();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Constants.TAG, "[RegisterActivity:finishAuthWithFacebook]: Error: ",
                                    task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                showError(AuthType.FACEBOOK, AuthError.EMAIL_ALREADY_REGISTERED);
                            } else {
                                showError(AuthType.FACEBOOK, AuthError.OTHER);
                            }
                        }
                    }
                });
    }

    /**
     * Method that updates the user in Firestore, or creates it if it didn't exist.
     *
     * This method is only reached if the Firebase authentication succeeded.
     */
    private void registerWithFirestore() {
        final FirebaseUser user = mFirebaseAuth.getCurrentUser();

        if (user != null) {
            User myUser = new User(
                      user.getUid()
                    , user.getDisplayName() == null ? "Anónimo" : user.getDisplayName()
                    , user.getPhotoUrl()
                    , 0);

            FirestoreConnector.newInstance().addUser(myUser, this);
        }
    }

    @Override
    public void onSuccess() {
        mRegistering = false;
        animateSuccess(mRegisterButton);
    }

    @Override
    public void onError() {
        mRegistering = false;
        showError(AuthType.FIRESTORE, AuthError.FIRESTORE);
    }

    @Override
    public void onSuccess(User item) {
        // Non-used
    }

    @Override
    public void onSuccess(List<User> items) {
        // Non-used
    }

    /**
     * Shows an error message when signing in.
     *
     * @param authType: type of authentication to be performed.
     * @param error:    type of authentication error.
     */
    private void showError(final AuthType authType, final AuthError error) {
        mRegistering = false;

        // Show the retry icon in the button
        mRegisterButton.revertAnimation();
        mRegisterButton
                .setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_fill, getTheme()));

        // And show the snackbar
        Snackbar snackbar;
        if (error == AuthError.EMAIL_ALREADY_REGISTERED) {
            snackbar = Snackbar.make(mContainer, R.string.email_already_register, Snackbar.LENGTH_SHORT);

        } else if (error == AuthError.FIRESTORE) {
            snackbar = Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .setAction("Reintentar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            register(authType);
                        }
                    });

        } else {
            snackbar = Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .setAction("Reintentar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            register(authType);
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
        // Animate the change of background
        final AnimationDrawable drawable = new AnimationDrawable();

        drawable.addFrame(Objects.requireNonNull(
                ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_border, getTheme())), 0);
        drawable.addFrame(Objects.requireNonNull(
                ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_fill, getTheme())), 0);
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

        mNameInputLayout.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mEmailInputLayout.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mPasswordInputLayout.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mDivider.setTranslationY(-ENTER_ANIM_TRANSLATION);
        mSocialButtons.setTranslationY(-ENTER_ANIM_TRANSLATION);

        mNameInputLayout.setAlpha(0.0f);
        mEmailInputLayout.setAlpha(0.0f);
        mPasswordInputLayout.setAlpha(0.0f);
        mDivider.setAlpha(0.0f);
        mSocialButtons.setAlpha(0.0f);

        mTitle.animate().withLayer().setDuration(ENTER_ANIM_DURATION).scaleX(1).scaleY(1).translationX(0)
                .translationY(0).setInterpolator(new AccelerateDecelerateInterpolator());

        mNameInputLayout.animate().withLayer().setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET)
                .setStartDelay(ENTER_ANIM_OFFSET).setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION).alpha(1.0f);

        mEmailInputLayout.animate().withLayer().setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 2)
                .setStartDelay(ENTER_ANIM_OFFSET * 2).setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION).alpha(1.0f);

        mPasswordInputLayout.animate().withLayer().setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 3)
                .setStartDelay(ENTER_ANIM_OFFSET * 3).setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION).alpha(1.0f);

        mRegisterButton.animate().withLayer().setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 4)
                .setStartDelay(ENTER_ANIM_OFFSET * 4).scaleX(1).scaleY(1).translationX(0).translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator());

        mDivider.animate().withLayer().setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 5)
                .setStartDelay(ENTER_ANIM_OFFSET * 5).setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION).alpha(1.0f);

        mSocialButtons.animate().withLayer().setDuration(ENTER_ANIM_DURATION - ENTER_ANIM_OFFSET * 6)
                .setStartDelay(ENTER_ANIM_OFFSET * 6).setInterpolator(new DecelerateInterpolator())
                .translationYBy(ENTER_ANIM_TRANSLATION).alpha(1.0f);

        drawable.start();
    }

    /**
     * Shows the exit animation when the activity is destroyed.
     */
    private void runExitAnimation(final Runnable endAction) {
        mExiting = true;

        final AnimationDrawable drawable = new AnimationDrawable();

        drawable.addFrame(Objects.requireNonNull(
                ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_fill, getTheme())), 0);
        drawable.addFrame(Objects.requireNonNull(
                ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_border, getTheme())), 0);
        drawable.setOneShot(true);
        drawable.setEnterFadeDuration(EXIT_ANIM_DURATION);
        drawable.setExitFadeDuration(EXIT_ANIM_DURATION);

        mRegisterButton.setBackground(drawable);
        mRegisterButton.setTextColor(getColor(R.color.colorAccent));

        mSocialButtons.animate().withLayer().setDuration(EXIT_ANIM_DURATION).setStartDelay(0)
                .setInterpolator(new AccelerateInterpolator()).translationYBy(EXIT_ANIM_TRANSLATION).alpha(0.0f);

        mDivider.animate().withLayer().setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET)
                .setStartDelay(EXIT_ANIM_OFFSET).setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION).alpha(0.0f);

        mRegisterButton.animate().withLayer().setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 2)
                .setStartDelay(EXIT_ANIM_OFFSET * 2).setInterpolator(new AccelerateDecelerateInterpolator())
                .scaleX(mWidthScaleButton).scaleY(mHeightScaleButton).translationX(mLeftDeltaButton)
                .translationY(mTopDeltaButton);

        mPasswordInputLayout.animate().withLayer().setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 3)
                .setStartDelay(EXIT_ANIM_OFFSET * 3).setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION).alpha(0.0f);

        mEmailInputLayout.animate().withLayer().setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 4)
                .setStartDelay(EXIT_ANIM_OFFSET * 4).setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION).alpha(0.0f);

        mNameInputLayout.animate().withLayer().setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 5)
                .setStartDelay(EXIT_ANIM_OFFSET * 5).setInterpolator(new AccelerateInterpolator())
                .translationYBy(EXIT_ANIM_TRANSLATION).alpha(0.0f);

        mTitle.animate().withLayer().setDuration(EXIT_ANIM_DURATION - EXIT_ANIM_OFFSET * 6)
                .setStartDelay(EXIT_ANIM_OFFSET * 6).setInterpolator(new AccelerateDecelerateInterpolator())
                .scaleX(mWidthScaleTitle).scaleY(mHeightScaleTitle).translationX(mLeftDeltaTitle)
                .translationY(mTopDeltaTitle).withEndAction(endAction);

        drawable.start();

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(EXIT_ANIM_DURATION - 225);
        bgAnim.setStartDelay(225);
        bgAnim.start();
    }

    /**
     * Starts the success animation with a circular reveal where the user can
     * advance to the next screen.
     */
    private void animateSuccess(@NonNull View origin) {
        int enterButtonX = (origin.getLeft() + origin.getRight()) / 2;

        int enterButtonY = (origin.getTop() + origin.getBottom()) / 2;

        View background = findViewById(R.id.register_background);

        int radiusReveal = Math.max(background.getWidth(), background.getHeight());

        background.setVisibility(View.VISIBLE);

        Animator animator = android.view.ViewAnimationUtils.createCircularReveal(background, enterButtonX, enterButtonY,
                0, radiusReveal);

        animator.setDuration(500);
        animator.setInterpolator(
                AnimationUtils.loadInterpolator(RegisterActivity.this, R.anim.accelerator_interpolator));

        animator.start();

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the next screen
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                finish();
            }
        });
    }

    /**
     * Custom TextWatcher to validate the fields.
     */
    private static class MyTextWatcher implements TextWatcher {

        private final Validator validator;

        private MyTextWatcher(Validator validator) {
            this.validator = validator;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            validator.validate();
        }
    }
}
