package com.movielix;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import com.movielix.firestore.FirestoreListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class UserActivity extends AppCompatActivity {

    private static final int NUM_REQUESTS = 4;

    private String mUserId;
    private String mUserName;
    private String mUserProfilePic;

    private ProgressBar mProgressBar;
    private TextView mMessageTextview;
    private RecyclerView mReviewsRecyclerView;
    private View mContainer;

    private CountDownLatch mBarrier;

    private List<Review> mReviews;
    private int mNumFollowers;
    private int mNumFriends;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend);

        Bundle bundle = getIntent().getExtras();
        assert(bundle != null);

        mUserId = bundle.getString(Constants.USER_ID);
        mUserName = bundle.getString(Constants.USER_NAME);
        mUserProfilePic = bundle.getString(Constants.USER_PROFILE_PIC);

        mProgressBar = findViewById(R.id.friend_progress_bar);
        mMessageTextview = findViewById(R.id.friend_message_textview);
        mReviewsRecyclerView = findViewById(R.id.friend_recyclerview);
        mContainer = findViewById(R.id.friend_container);
        findViewById(R.id.friend_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        hideMessage();
        initViews();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mBarrier = new CountDownLatch(1);

                mReviews = new ArrayList<>();
                mNumFollowers = 0;
                mNumFriends = 0;

                getReviews();
                getNumFollowers();
                getNumFriends();

                try {
                    mBarrier.await();

                    // todo updateUi();

                } catch (InterruptedException e) {
                    Log.e(Constants.TAG, "Thread interrupted");

                    // todo show error
                }
            }

        }).start();
    }

    private void initViews() {
        mContainer.setVisibility(View.INVISIBLE);

        // Set profile pic
        Picasso.get()
                .load(mUserProfilePic)
                .error(R.drawable.ic_default_profile_pic)
                .into((ImageView) findViewById(R.id.friend_profile_pic));

        // Update toolbar with the user's name.
        ((TextView)findViewById(R.id.friend_name)).setText(mUserName);
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Review> reviews) {
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews, false, this);

        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mReviewsRecyclerView.setAdapter(reviewsAdapter);
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void getReviews() {
        FirestoreConnector.newInstance()
                .getReviewsByUser(mUserId, new FirestoreListener<Review>() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Review item) {}

            @Override
            public void onSuccess(final List<Review> reviews) {
                hideProgressBar();
                if (reviews.isEmpty()) {
                    showMessage(getResources().getString(R.string.friend_no_reviews));

                    mBarrier.countDown();

                } else {
                    List<String> ids = new ArrayList<>();
                    for (Review review : reviews) {
                        ids.add(review.getMovieId());
                    }

                    FirestoreConnector.newInstance()
                            .getMoviesById(ids, new FirestoreListener<Movie>() {
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
                                    }

                                    mBarrier.countDown();
                                }

                                @Override
                                public void onError() {
                                    hideProgressBar();

                                    Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showProgressBar();
                                            getReviews();
                                        }
                                    }).show();

                                    mBarrier.countDown();
                                }
                            });
                }
            }

            @Override
            public void onError() {
                hideProgressBar();

                Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressBar();
                        getReviews();
                    }
                }).show();

                mBarrier.countDown();
            }
        });
    }

    private void getNumFollowers() {
        // todo
    }

    private void getNumFriends() {
        // todo
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
