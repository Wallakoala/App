package com.movielix;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.movielix.adapter.MoviesAdapter;
import com.movielix.adapter.MoviesSuggestionAdapter;
import com.movielix.bean.Movie;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.FirestoreMoviesObserver;
import com.movielix.font.TypeFace;

import java.util.List;

public class MoviesActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener, FirestoreMoviesObserver {

    // ProgressBar
    private ProgressBar mProgressBar;

    // TextView
    private AppCompatTextView mMessageTextview;

    // RecyclerView
    private RecyclerView mMoviesRecyclerView;

    private FirestoreConnector firestoreConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movies);

        firestoreConnector = FirestoreConnector.newInstance();

        initializeSearchView();

        mProgressBar = findViewById(R.id.movies_progress_bar);
        mMessageTextview = findViewById(R.id.movies_message_textview);
        mMoviesRecyclerView = findViewById(R.id.movies_recycler_view);

        hideMessage();
    }

    private void initializeSearchView() {
        MaterialSearchBar searchBar = findViewById(R.id.movies_search_bar);

        searchBar.setOnSearchActionListener(this);
        MoviesSuggestionAdapter suggestionAdapter =
                new MoviesSuggestionAdapter((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));

        suggestionAdapter.setSuggestions(firestoreConnector.getDummyMovies(this));
        searchBar.setCustomSuggestionAdapter(suggestionAdapter);

        // Set the font
        try {
            ConstraintLayout cl = (ConstraintLayout)((CardView) searchBar.getChildAt(0)).getChildAt(0);
            AppCompatTextView textView = (AppCompatTextView) cl.getChildAt(1);
            AppCompatEditText editText = (AppCompatEditText)((LinearLayout) cl.getChildAt(2)).getChildAt(1);

            Typeface tf = TypeFace.getTypeFace(this, "Raleway-Regular.ttf");
            textView.setTypeface(tf);
            editText.setTypeface(tf);

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error setting the font to the search_bar", e);
        }
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Movie> movies) {
        MoviesAdapter moviesAdapter = new MoviesAdapter(movies, this);

        mMoviesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mMoviesRecyclerView.setAdapter(moviesAdapter);
        mMoviesRecyclerView.scheduleLayoutAnimation();
    }

    private void showProgressBar() {
        YoYo.with(Techniques.ZoomIn).playOn(mProgressBar);
    }

    private void hideProgressBar() {
        YoYo.with(Techniques.ZoomOut).playOn(mProgressBar);
    }

    private void showMessage(String message) {
        mMessageTextview.setVisibility(View.VISIBLE);
        mMessageTextview.setText(message);
    }

    private void hideMessage() {
        mMessageTextview.setVisibility(View.GONE);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void onSuccess(List<Movie> movies) {
        hideProgressBar();
        //initializeRecyclerView(movies);
        showMessage(getResources().getString(R.string.no_movies_found));
    }

    @Override
    public void onError() {
        hideProgressBar();
    }
}
