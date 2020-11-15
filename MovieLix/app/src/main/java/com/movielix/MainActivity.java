package com.movielix;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.movielix.adapter.ReviewsAdapter;
import com.movielix.bean.Movie;
import com.movielix.firestore.FirestoreConnector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SWIPE_TRIGGER_DISTANCE = 750;

    // Views
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeToolbar();
        initializeDrawer();
        initializedFAB();

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setDistanceToTriggerSync(SWIPE_TRIGGER_DISTANCE);

        mProgressBar = findViewById(R.id.reviews_progress_bar);

        new GetReviewsTask(this).execute();
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

    /**
     * Initializes the floating action button.
     */
    private void initializedFAB() {
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
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
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Movie> movies) {
        RecyclerView recyclerView = findViewById(R.id.review_recyclerview);
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(movies, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(reviewsAdapter);

        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * Initializes the navigation drawer and the action bar drawer toggle.
     */
    private void initializeDrawer() {
        // Navigation drawer
        DrawerLayout dl = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, dl, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        dl.addDrawerListener(mDrawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerToggle.syncState();
    }

    /**
     * Hides the progress bar with an animation.
     */
    private void hideProgressBar() {
        YoYo.with(Techniques.ZoomOut).playOn(mProgressBar);
    }

    @SuppressWarnings("StaticFieldLeak")
    private class GetReviewsTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> mContext;
        private List<Movie> movies;

        GetReviewsTask(final Context context) {
            super();

            mContext = new WeakReference<>(context);
            movies = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                FirestoreConnector fc = FirestoreConnector.newInstance();
                movies = fc.getDummyMovies(mContext.get());

                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hideProgressBar();
            initializeRecyclerView(movies);
        }
    }
}