package com.movielix;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.movielix.constants.Constants;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie);

        Bundle bundle = getIntent().getExtras();

        String title = Objects.requireNonNull(bundle).getString(Constants.MOVIE_TITLE_INTENT);
        String url = bundle.getString(Constants.MOVIE_IMAGE_INTENT);
        String genres = bundle.getString(Constants.MOVIE_GENRES_INTENT);
        int releaseYear = bundle.getInt(Constants.MOVIE_RELEASE_YEAR_INTENT);

        Picasso.get()
                .load(url)
                .into((ImageView) findViewById(R.id.movie_cover));

        ((TextView) findViewById(R.id.movie_title)).setText(title);
        ((TextView) findViewById(R.id.movie_genres)).setText(genres);
        ((TextView) findViewById(R.id.movie_release_year)).setText("(" + releaseYear + ")");
    }
}
