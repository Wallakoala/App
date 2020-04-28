package com.movielix;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.movielix.adapter.ReviewsAdapter;
import com.movielix.bean.Movie;
import com.movielix.constants.Constants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int SWIPE_TRIGGER_DISTANCE = 750;

    // Views
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButton mFAB;
    private ProgressBar mProgressBar;

    // Container
    private DrawerLayout mDrawerLayout;

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
        mFAB = findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
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
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

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

        private WeakReference<Context> mContext;
        private List<Movie> movies;

        GetReviewsTask(final Context context) {
            super();

            mContext = new WeakReference<>(context);
            movies = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Movie movie = new Movie(
                        "La La Land"
                        , mContext.get().getString(R.string.reviews_item_movie_overview)
                        , 2016
                        , "2h 8m"
                        , new URL("https://m.media-amazon.com/images/M/MV5BMzUzNDM2NzM2MV5BMl5BanBnXkFtZTgwNTM3NTg4OTE@._V1_SX300.jpg")
                        , new String[] { "Comedia", "Romance" });

                movies.add(movie);

                movie = new Movie(
                        "Capitán América: El primer vengador"
                        , "Nacido durante la Gran Depresión, Steve Rogers creció como un chico enclenque en una familia pobre. Horrorizado por las noticias que llegaban de Europa sobre los nazis, decidió enrolarse en el ejército; sin embargo, debido a su precaria salud, fue rechazado una y otra vez. Enternecido por sus súplicas, el General Chester Phillips le ofrece la oportunidad de tomar parte en un experimento especial. la \\\"Operación Renacimiento\\\". Después de admi"
                        , 2014
                        , "2h 4m"
                        , new URL("https://m.media-amazon.com/images/M/MV5BMTYzOTc2NzU3N15BMl5BanBnXkFtZTcwNjY3MDE3NQ@@._V1_SX300.jpg")
                        , new String[] { "Acción", "Aventura" });

                movies.add(movie);

                movie = new Movie(
                        "Django desencadenado"
                        , "Dos años antes de estallar la Guerra Civil (1861-1865), Schultz, un cazarrecompensas alemán que le sigue la pista a unos asesinos, le promete al esclavo Django dejarlo en libertad si le ayuda a atraparlos. Terminado con éxito el trabajo, Django prefiere seguir al lado del alemán y ayudarle a capturar a los delincuentes más buscados del Sur. Se convierte así en un experto cazador de recompensas, pero su único objetivo es rescatar a su esposa Broomhilda, a la que perdió por culpa del tráfico de esclavos. La búsqueda llevará a Django y a Schultz hasta Calvin Candie, el malvado propietario"
                        , 2012
                        , "2h 45m"
                        , new URL("https://m.media-amazon.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX300.jpg")
                        , new String[] { "Drama", "Western" });

                movies.add(movie);

                Thread.sleep(2000);

            } catch (InterruptedException | MalformedURLException e) {
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