package com.movielix;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.movielix.adapter.ReviewsAdapter;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.interfaces.IFirestoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyReviewsActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private TextView mMessageTextview;
    private RecyclerView mReviewsRecyclerView;
    private View mContainer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_reviews);

        mProgressBar = findViewById(R.id.my_reviews_progress_bar);
        mMessageTextview = findViewById(R.id.my_reviews_message_textview);
        mReviewsRecyclerView = findViewById(R.id.my_reviews_recyclerview);
        mContainer = findViewById(R.id.my_reviews_container);
        findViewById(R.id.my_reviews_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initializeFAB();
        hideMessage();

        getReviews();
    }

    /**
     * Initializes the floating action button.
     */
    private void initializeFAB() {
        findViewById(R.id.add_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyReviewsActivity.this, MoviesActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Review> reviews) {
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews, true, this);

        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mReviewsRecyclerView.setAdapter(reviewsAdapter);
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void getReviews() {
        mMessageTextview.setVisibility(View.GONE);
        mReviewsRecyclerView.setVisibility(View.GONE);
        FirestoreConnector.newInstance()
                .getReviewsByUser(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), new IFirestoreListener<Review>() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Review item) {}

            @Override
            public void onSuccess(final List<Review> reviews) {
                hideProgressBar();
                if (reviews.isEmpty()) {
                    showMessage(getResources().getString(R.string.no_reviews));
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
                                    List<Review> reviewsWithMovies = new ArrayList<>();
                                    for (Review review : reviews) {
                                        reviewsWithMovies.add(new Review(
                                                review.getScore(),
                                                review.getMovieId(),
                                                review.getUserId(),
                                                review.getComment(),
                                                getMovieById(movies, review.getMovieId())
                                        ));
                                    }

                                    initializeRecyclerView(reviewsWithMovies);
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
            }
        });
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
