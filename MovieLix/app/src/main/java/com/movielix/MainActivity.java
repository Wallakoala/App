package com.movielix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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
import com.google.firebase.auth.FirebaseAuth;
import com.movielix.adapter.ReviewsAdapter;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SWIPE_TRIGGER_DISTANCE = 750;

    // Views
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeToolbar();
        initializeDrawer();
        initializeFAB();

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
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Review> reviews) {
        RecyclerView recyclerView = findViewById(R.id.review_recyclerview);
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

    /**
     * Hides the progress bar with an animation.
     */
    private void hideProgressBar() {
        YoYo.with(Techniques.ZoomOut).playOn(mProgressBar);
    }

    @SuppressWarnings("StaticFieldLeak")
    private class GetReviewsTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> mContext;
        private List<Review> reviews;

        GetReviewsTask(final Context context) {
            super();

            mContext = new WeakReference<>(context);
            reviews = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                FirestoreConnector fc = FirestoreConnector.newInstance();
                reviews = fc.getDummyMovies(mContext.get());

                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hideProgressBar();
            initializeRecyclerView(reviews);
        }
    }
}