package com.movielix;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.movielix.adapter.ReviewsAdapter;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.interfaces.IFirestoreListener;
import com.movielix.interfaces.IFirestoreFieldListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UserActivity extends AppCompatActivity {
    private String mUserId;
    private String mUserName;
    private String mUserProfilePic;

    private ProgressBar mProgressBar;
    private TextView mMessageTextview;
    private TextView mNumReviewsTextview;
    private TextView mNumFollowersTextview;
    private TextView mNumFollowingTextview;
    private RecyclerView mReviewsRecyclerView;
    private View mContainer;

    private AtomicInteger mRequestCounter;
    private AtomicBoolean mRequestFailed;

    private List<Review> mReviews;
    private int mNumFollowers;
    private boolean mFollowing;
    private int mNumRequests;

    private Button mFollow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        Bundle bundle = getIntent().getExtras();
        assert(bundle != null);

        mUserId = bundle.getString(Constants.USER_ID);
        mUserName = bundle.getString(Constants.USER_NAME);
        mUserProfilePic = bundle.getString(Constants.USER_PROFILE_PIC);

        mProgressBar = findViewById(R.id.user_progress_bar);
        mMessageTextview = findViewById(R.id.user_message_textview);
        mNumReviewsTextview = findViewById(R.id.friend_num_reviews);
        mNumFollowersTextview = findViewById(R.id.friend_num_followers);
        mNumFollowingTextview = findViewById(R.id.friend_num_following);
        mReviewsRecyclerView = findViewById(R.id.user_recyclerview);
        mContainer = findViewById(R.id.user_container);
        findViewById(R.id.friend_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initViews();
        getData();
    }

    private void initViews() {
        hideMessage();

        // Set profile pic
        Picasso.get()
                .load(mUserProfilePic)
                .error(R.drawable.ic_default_profile_pic)
                .into((ImageView) findViewById(R.id.user_profile_pic));

        // Update toolbar with the user's name.
        ((TextView) findViewById(R.id.user_name)).setText(mUserName);

        mFollow = findViewById(R.id.friend_add_button);
        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFollowing) {
                    FirestoreConnector.newInstance().unfollow(
                            Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), mUserId);

                    mNumFollowers--;

                } else {
                    FirestoreConnector.newInstance().follow(
                            Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), mUserId);

                    mNumFollowers++;
                }

                String message = (mNumFollowers == 1) ? getString(R.string.friend_num_followers_singular) : getString(R.string.friend_num_followers);
                message = message.replace(Constants.STR_PLACEHOLDER_1, Integer.toString(mNumFollowers));
                mNumFollowersTextview.setText(message);

                mFollowing = !mFollowing;
                updateFollowButton();
            }
        });
    }

    /**
     * Initializes the UI.
     */
    private void initializeUi() {
        mContainer.setVisibility(View.VISIBLE);

        hideProgressBar();
        hideMessage();
        updateFollowButton();

        if (mReviews.isEmpty()) {
            showMessage(getResources().getString(R.string.friend_no_reviews).replace(Constants.STR_PLACEHOLDER_1, mUserName));
        } else {
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(mReviews, false, this);

            mReviewsRecyclerView.setLayoutManager(
                    new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mReviewsRecyclerView.setAdapter(reviewsAdapter);
            mReviewsRecyclerView.setVisibility(View.VISIBLE);
        }

        mNumReviewsTextview.setText(
                getString(R.string.friend_num_reviews_2).replace(Constants.STR_PLACEHOLDER_1, Integer.toString(mReviews.size())));
    }

    private void getData() {
        mContainer.setVisibility(View.INVISIBLE);
        showProgressBar();

        mReviews = new ArrayList<>();
        mNumFollowers = 0;
        mNumRequests = 0;

        mRequestCounter = new AtomicInteger(0);
        mRequestFailed = new AtomicBoolean(false);

        getReviews();
        getNumFollowers();
        getNumFollowing();
    }

    private void getReviews() {
        mNumRequests++;
        FirestoreConnector.newInstance()
                .getReviewsByUser(mUserId, new IFirestoreListener<Review>() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Review item) {}

            @Override
            public void onSuccess(final List<Review> reviews) {
                if (reviews.isEmpty()) {
                    finishTask(true);

                } else {
                    final List<String> ids = new ArrayList<>();
                    for (Review review : reviews) {
                        ids.add(review.getMovieId());
                    }

                    FirestoreConnector.newInstance()
                            .getMoviesById(ids, new IFirestoreListener<Movie>() {
                                @Override
                                public void onSuccess() {}

                                @Override
                                public void onSuccess(Movie item) {}

                                @Override
                                public void onSuccess(List<Movie> movies) {
                                    for (Review review : reviews) {
                                        mReviews.add(new Review(
                                                review.getScore(),
                                                review.getMovieId(),
                                                review.getUserId(),
                                                review.getComment(),
                                                getMovieById(movies, review.getMovieId())
                                        ));

                                        Log.d(Constants.TAG, review.toString());
                                    }

                                    finishTask(true);
                                }

                                @Override
                                public void onError(ErrCode reason) {
                                    finishTask(false);
                                }
                            });
                }
            }

            @Override
            public void onError(ErrCode reason) {
                finishTask(false);
            }
        });
    }

    private void getNumFollowers() {
        mNumRequests++;
        FirestoreConnector
                .newInstance()
                .getFollowersOfUser(mUserId, new IFirestoreFieldListener<String>() {
                    @Override
                    public void onSuccess(List<String> ids) {
                        mNumFollowers = ids.size();
                        String message = (mNumFollowers == 1) ? getString(R.string.friend_num_followers_singular) : getString(R.string.friend_num_followers);
                        message = message.replace(Constants.STR_PLACEHOLDER_1, Integer.toString(mNumFollowers));
                        mNumFollowersTextview.setText(message);

                        finishTask(true);
                    }

                    @Override
                    public void onError() {
                        finishTask(false);
                    }
                });
    }

    private void getNumFollowing() {
        mNumRequests++;
        FirestoreConnector
                .newInstance()
                .getFollowingOfUser(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), new IFirestoreFieldListener<String>()
                {
                    @Override
                    public void onSuccess(List<String> ids) {
                        String message = (ids.size() == 1) ? getString(R.string.friend_num_friends_singular) : getString(R.string.friend_num_friends);
                        message = message.replace(Constants.STR_PLACEHOLDER_1, Integer.toString(ids.size()));
                        mNumFollowingTextview.setText(message);

                        mFollowing = ids.contains(mUserId);

                        finishTask(true);
                    }

                    @Override
                    public void onError() {
                        finishTask(false);
                    }
                });
    }

    private void finishTask(boolean ok) {
        if (!ok) {
            mRequestFailed.set(true);
        }

        // If we are the last one, and something went wrong, then show the error.
        if ((mRequestCounter.incrementAndGet() == mNumRequests)) {
            hideProgressBar();
            if (mRequestFailed.get()) {
                Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressBar();
                        getData();
                    }
                }).show();

            } else {
                // We know that everyone finished and all the data is initialized.
                initializeUi();
            }
        }
    }

    private void showMessage(final String message) {
        mMessageTextview.setText(message);
        mMessageTextview.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        mMessageTextview.setVisibility(View.GONE);
    }

    private void updateFollowButton() {
        if (mFollowing) {
            mFollow.setBackground(
                    ContextCompat.getDrawable(this, R.drawable.rounded_button_fill));
            mFollow.setTextColor(
                    getResources().getColor(android.R.color.black, getTheme()));
            mFollow.setText(
                    getResources().getText(R.string.friend_unfollow));

        } else {
            mFollow.setBackground(
                    ContextCompat.getDrawable(this, R.drawable.rounded_button_border_transparent));
            mFollow.setTextColor(
                    getResources().getColor(R.color.colorAccent, getTheme()));
            mFollow.setText(
                    getResources().getText(R.string.friend_follow));
        }
    }

    /**
     * Shows the progress bar with an animation.
     */
    private void showProgressBar() {
        YoYo.with(Techniques.ZoomIn).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }).playOn(mProgressBar);
    }

    /**
     * Hides the progress bar with an animation.
     */
    private void hideProgressBar() {
        YoYo.with(Techniques.ZoomOut).playOn(mProgressBar);
    }

    private Movie getMovieById(List<Movie> movies, String id) {
        for (Movie movie : movies) {
            if (movie.getId().equals(id)) {
                return movie;
            }
        }

        return null;
    }
}
