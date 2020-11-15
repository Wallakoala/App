package com.movielix;

import android.animation.Animator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.movielix.adapter.MoviesAdapter;
import com.movielix.adapter.MoviesSuggestionAdapter;
import com.movielix.bean.LiteMovie;
import com.movielix.bean.BaseMovie;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.FirestoreListener;
import com.movielix.font.TypeFace;

import java.util.List;

public class MoviesActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    // ProgressBar
    private ProgressBar mProgressBar;

    // TextView
    private AppCompatTextView mMessageTextview;

    // RecyclerView
    private RecyclerView mMoviesRecyclerView;
    private RecyclerView mSuggestionsRecyclerView;

    // Containers
    private View mSuggestionsContainer;
    private View mMoviesContainer;

    private FirestoreConnector firestoreConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movies);

        mProgressBar = findViewById(R.id.movies_progress_bar);
        mMessageTextview = findViewById(R.id.movies_message_textview);
        mMoviesRecyclerView = findViewById(R.id.movies_recycler_view);
        mSuggestionsRecyclerView = findViewById(R.id.movies_suggestions_recycler_view);
        mSuggestionsContainer = findViewById(R.id.movies_suggestions_container);
        mMoviesContainer = findViewById(R.id.movies_container);

        mSuggestionsContainer.setVisibility(View.GONE);
        mSuggestionsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        firestoreConnector = FirestoreConnector.newInstance();

        initializeSearchView();
        hideMessage();
        hideProgressBar(false);
    }

    private void initializeSearchView() {
        MaterialSearchBar searchBar = findViewById(R.id.movies_search_bar);

        searchBar.setOnSearchActionListener(this);
        searchBar.setSuggestionsEnabled(false);

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

        // Listen to text changes to retrieve the suggestions.
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1) {
                    showProgressBar();
                    firestoreConnector.getMoviesSuggestionsByTitle(MoviesActivity.this, charSequence.toString(), new FirestoreListener<BaseMovie>() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onSuccess(BaseMovie item) {}

                        @Override
                        public void onSuccess(List<BaseMovie> movies) {
                            hideProgressBar(true);

                            MoviesSuggestionAdapter adapter = new MoviesSuggestionAdapter(
                                    MoviesActivity.this, movies, charSequence.toString());
                            mSuggestionsRecyclerView.setAdapter(adapter);
                            mSuggestionsContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            hideProgressBar(true);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void showProgressBar() {
        YoYo.with(Techniques.ZoomIn).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }).playOn(mProgressBar);
    }

    private void hideProgressBar(boolean animated) {
        if (animated) {
            YoYo.with(Techniques.ZoomOut).playOn(mProgressBar);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showMessage(String message) {
        mMessageTextview.setText(message);
        mMessageTextview.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        mMessageTextview.setVisibility(View.GONE);
    }

    private void hideSuggestions() {
        mSuggestionsContainer.setVisibility(View.GONE);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            mSuggestionsContainer.setVisibility(View.GONE);

            mMoviesContainer.animate()
                    .alpha(1.f)
                    .setDuration(250)
                    .start();
        } else {
            mMoviesContainer.animate()
                    .alpha(.25f)
                    .setDuration(250)
                    .start();
        }
    }

    @Override
    public void onSearchConfirmed(final CharSequence text) {
        if (text.length() > 1) {
            MaterialSearchBar searchBar = findViewById(R.id.movies_search_bar);
            searchBar.closeSearch();

            showProgressBar();
            hideSuggestions();
            hideMessage();

            mMessageTextview.setVisibility(View.GONE);
            mMoviesRecyclerView.setVisibility(View.GONE);
            firestoreConnector.getMoviesByTitle(text.toString(), new FirestoreListener<LiteMovie>() {
                @Override
                public void onSuccess() {}

                @Override
                public void onSuccess(LiteMovie item) {}

                @Override
                public void onSuccess(List<LiteMovie> movies) {
                    hideProgressBar(true);
                    hideSuggestions();

                    if (!movies.isEmpty()) {
                        MoviesAdapter moviesAdapter = new MoviesAdapter(movies, MoviesActivity.this);
                        mMoviesRecyclerView.setLayoutManager(
                                new LinearLayoutManager(MoviesActivity.this, RecyclerView.VERTICAL, false));
                        mMoviesRecyclerView.setAdapter(moviesAdapter);
                        mMoviesRecyclerView.setVisibility(View.VISIBLE);

                    } else {
                        String search = text.toString();
                        String message = MoviesActivity.this.getString(R.string.no_movies_found);
                        message = message.replace("%1", search);

                        showMessage(message);
                    }
                }

                @Override
                public void onError() {
                    Snackbar.make(mMoviesContainer, "Ops, algo ha ido mal", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onButtonClicked(int buttonCode) {}
}
