package com.movielix;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.movielix.adapter.ReviewsAdapter;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.IFirestoreListener;
import com.movielix.interfaces.IFirestoreFieldListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private enum RefreshType {
        SWIPE,
        DEFAULT
    }

    private static final int SWIPE_TRIGGER_DISTANCE = 500;

    // Views
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mMessageTextView;
    private View mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeToolbar();
        initializeDrawer();
        initializeFAB();

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getReviews(RefreshType.SWIPE);
            }
        });

        mSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_TRIGGER_DISTANCE);
        mProgressBar = findViewById(R.id.reviews_progress_bar);
        mMessageTextView = findViewById(R.id.reviews_message_textview);
        mContainer = findViewById(R.id.reviews_container);

        getReviews(RefreshType.DEFAULT);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onEnterAnimationComplete() {
        mToolbar.findViewById(R.id.toolbar_title).setTransitionName(null);
    }

    @Override
    public void finishAfterTransition() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.menu_my_reviews) {
            Intent intent = new Intent(MainActivity.this, MyReviewsActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_my_friends) {
            Intent intent = new Intent(MainActivity.this, MyFriendsActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_feedback) {
            // todo

        } else if (id == R.id.menu_licenses) {
            // todo

        } else if (id == R.id.menu_sign_out) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this, R.style.MyAlertDialogStyleLight);
            adb.setTitle("¿Seguro quieres salir?");
            adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logoutIntent = new Intent(getApplicationContext(), IntroActivity.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                }
            });
            adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { }
            });
            adb.show();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Initializes the floating action button.
     */
    private void initializeFAB() {
        findViewById(R.id.add_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MoviesActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the toolbar.
     */
    private void initializeToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mToolbar.findViewById(R.id.toolbar_add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Review> reviews) {
        RecyclerView recyclerView = findViewById(R.id.reviews_recyclerview);
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews, false, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(reviewsAdapter);

        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * Initializes the navigation drawer and the action bar drawer toggle.
     */
    private void initializeDrawer() {
        // Navigation drawer
        NavigationView nv = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        nv.setNavigationItemSelectedListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerToggle.syncState();
    }

    private void getReviews(final RefreshType refreshType) {
        mMessageTextView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
        FirestoreConnector.newInstance().getFollowingOfUser(
                Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), new IFirestoreFieldListener<String>()
        {
            @Override
            public void onSuccess(List<String> ids) {
                if (!ids.isEmpty()) {
                    FirestoreConnector.newInstance().getReviewsByUsers(ids, new IFirestoreListener<Review>() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onSuccess(Review item) {}

                        @Override
                        public void onSuccess(final List<Review> reviews) {
                            List<String> ids = new ArrayList<>();
                            for (Review review: reviews) {
                                ids.add(review.getMovieId());
                            }

                            if (!reviews.isEmpty()) {
                                FirestoreConnector.newInstance().getMoviesById(ids, new IFirestoreListener<Movie>() {
                                    @Override
                                    public void onSuccess() {}

                                    @Override
                                    public void onSuccess(Movie item) {}

                                    @Override
                                    public void onSuccess(List<Movie> movies) {
                                        for (Movie movie: movies) {
                                            for (Review review: reviews) {
                                                if (review.getMovieId().equals(movie.getId())) {
                                                    review.setMovie(movie);
                                                }
                                            }
                                        }

                                        hideProgressBar(refreshType);
                                        initializeRecyclerView(reviews);
                                    }

                                    @Override
                                    public void onError() {
                                        hideProgressBar(refreshType);
                                        hideMessage();

                                        Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showProgressBar();
                                                getReviews(refreshType);
                                            }
                                        }).show();
                                    }
                                });

                            } else {
                                hideProgressBar(refreshType);
                                showMessage(getString(R.string.reviews_no_reviews));
                            }
                        }

                        @Override
                        public void onError() {
                            hideProgressBar(refreshType);
                            hideMessage();

                            Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showProgressBar();
                                    getReviews(refreshType);
                                }
                            }).show();
                        }
                    });

                } else {
                    hideProgressBar(refreshType);
                    showMessage(getString(R.string.reviews_no_users));
                }
            }

            @Override
            public void onError() {
                hideProgressBar(refreshType);
                hideMessage();

                Snackbar.make(mContainer, R.string.something_went_wrong, Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressBar();
                        getReviews(refreshType);
                    }
                }).show();
            }
        });
    }

    private void showMessage(String message) {
        mMessageTextView.setText(message);
        mMessageTextView.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        mMessageTextView.setVisibility(View.GONE);
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
    private void hideProgressBar(RefreshType refreshType) {
        if (refreshType == RefreshType.DEFAULT) {
            YoYo.with(Techniques.ZoomOut).playOn(mProgressBar);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mSwipeRefreshLayout.setEnabled(true);
    }
}
