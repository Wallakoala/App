package com.movielix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mancj.materialsearchbar.MaterialSearchBar;

public class MoviesActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private MaterialSearchBar mSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movies);
        initSearchView();
    }

    private void initSearchView() {
        mSearchBar = findViewById(R.id.movies_search_bar);

        mSearchBar.setOnSearchActionListener(this);
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
}
