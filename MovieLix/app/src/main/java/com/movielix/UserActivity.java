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
import com.movielix.adapter.ReviewsAdapter;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.IFirestoreListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UserActivity extends AppCompatActivity {

    // todo update with the correct value
    private static final int NUM_REQUESTS = 1;

    private String mUserId;
    private String mUserName;
    private String mUserProfilePic;

    private ProgressBar mProgressBar;
    private TextView mMessageTextview;
    private RecyclerView mReviewsRecyclerView;
    private View mContainer;

    private AtomicInteger mRequestCounter;
    private AtomicBoolean mRequestFailed;

    private List<Review> mReviews;
    private int mNumFollowers;
    private int mNumFriends;

    private Button mFollow;
    private boolean mFollowing;
    private boolean mFollowingExists;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        Bundle bundle = getIntent().getExtras();
        assert(bundle != null);

        mUserId = bundle.getString(Constants.USER_ID);
        mUserName = bundle.getString(Constants.USER_NAME);
        mUserProfilePic = bundle.getString(Constants.USER_PROFILE_PIC);
        if (bundle.containsKey(Constants.USER_PROFILE_PIC)) {
            mFollowing = bundle.getBoolean(Constants.USER_FOLLOWING);
            mFollowingExists = true;
        } else{
            mFollowingExists = false;
        }

        mProgressBar = findViewById(R.id.user_progress_bar);
        mMessageTextview = findViewById(R.id.user_message_textview);
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
        ((TextView)findViewById(R.id.user_name)).setText(mUserName);
        mFollowing = findViewById(R.id.friend_add_button);
        if (mFollowing) {
            mFollow.setBackground(
                    ContextCompat.getDrawable(mContext, R.drawable.rounded_button_fill));
            mFollow.setTextColor(
                    mContext.getResources().getColor(android.R.color.black, mContext.getTheme()));
            mFollow.setText(
                    mContext.getResources().getText(R.string.friend_unfollow));

        } else {
            mFollow.setBackground(
                    ContextCompat.getDrawable(mContext, R.drawable.rounded_button_border_transparent));
            mFollow.setTextColor(
                    mContext.getResources().getColor(R.color.colorAccent, mContext.getTheme()));
            mFollow.setText(
                    mContext.getResources().getText(R.string.friend_follow));
        }
    }

    /**
     * Initializes the UI.
     */
    private void initializeUi() {
        mContainer.setVisibility(View.VISIBLE);
        hideProgressBar();
        hideMessage();

        if (mReviews.isEmpty()) {
            showMessage(getResources().getString(R.string.friend_no_reviews));
        } else {
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(mReviews, false, this);

            mReviewsRecyclerView.setLayoutManager(
                    new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mReviewsRecyclerView.setAdapter(reviewsAdapter);
            mReviewsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getData() {
        mContainer.setVisibility(View.INVISIBLE);
        showProgressBar();

        mReviews = new ArrayList<>();
        mNumFollowers = 0;
        mNumFriends = 0;

        mRequestCounter = new AtomicInteger(0);
        mRequestFailed = new AtomicBoolean(false);

        getReviews();
        getNumFollowers();
        getNumFriends();

        if (!mFollowingExists){
            // TODO:
            getFollowing();
        }
    }

    private void getReviews() {
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
                    List<String> ids = new ArrayList<>();
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
                                public void onError() {
                                    finishTask(false);
                                }
                            });
                }
            }

            @Override
            public void onError() {
                finishTask(false);
            }
        });
    }

    private void getNumFollowers() {
        // todo
    }

    private void getNumFriends() {
        // todo
    }

    private void getFollowing(){
        // TODO
    }

    private void finishTask(boolean ok) {
        if (!ok) {
            mRequestFailed.set(true);
        }

        // If we are the last one, and something went wrong, then show the error.
        if ((mRequestCounter.incrementAndGet() == NUM_REQUESTS)) {
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

    private void showMessage(String message) {
        mMessageTextview.setText(message);
        mMessageTextview.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        mMessageTextview.setVisibility(View.GONE);
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
