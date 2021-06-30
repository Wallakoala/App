package com.movielix;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.constants.Constants;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.firestore.IFirestoreListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MovieActivity extends AppCompatActivity implements RatingDialogListener {

    private String mMovieId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie);

        Bundle bundle = getIntent().getExtras();

        String title = Objects.requireNonNull(bundle).getString(Constants.MOVIE_TITLE_INTENT);
        mMovieId = bundle.getString(Constants.MOVIE_ID_INTENT);
        String url = bundle.getString(Constants.MOVIE_IMAGE_INTENT);
        String genres = bundle.getString(Constants.MOVIE_GENRES_INTENT);
        int releaseYear = bundle.getInt(Constants.MOVIE_RELEASE_YEAR_INTENT);

        Picasso.get()
               .load(url)
               .error(R.color.textIdle)
               .into((ImageView) findViewById(R.id.movie_cover));

        ((TextView) findViewById(R.id.movie_title)).setText(title);
        ((TextView) findViewById(R.id.movie_genres)).setText(genres);
        ((TextView) findViewById(R.id.movie_release_year)).setText("(" + releaseYear + ")");

        findViewById(R.id.movie_add_review_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppRatingDialog.Builder()
                        .setPositiveButtonText("Enviar")
                        .setNegativeButtonText("Cancelar")
                        .setNoteDescriptions(Arrays.asList("Mojón", "Malilla", "Meh", "Bastante bien", "Canela fina"))
                        .setDefaultRating(2)
                        .setTitle("Valora esta película")
                        .setDescription("Valora esta peli para que tus amigos blah blah")
                        .setCommentInputEnabled(true)
                        .setHint("Escribe tu valoración (opcional)...")
                        .setCommentBackgroundColor(android.R.color.transparent)
                        .setTitleTextColor(R.color.textIdle)
                        .setStarColor(R.color.colorAccent)
                        .setCommentTextColor(R.color.textIdle)
                        .setDescriptionTextColor(R.color.textDark)
                        .setHintTextColor(R.color.textDark)
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .create(MovieActivity.this)
                        .show();
            }
        });

        findViewById(R.id.movie_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirestoreConnector.newInstance().getMovieById(mMovieId, new IFirestoreListener<Movie>() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Movie item) {
                ((TextView) findViewById(R.id.movie_summary)).setText(item.getOverview());
            }

            @Override
            public void onSuccess(List<Movie> items) {}

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int score, @Nullable String comment) {
        if (comment != null && comment.isEmpty()) {
            comment = null;
        }

        FirestoreConnector.newInstance().createReview(
                mMovieId, FirebaseAuth.getInstance().getUid(), score, comment, new IFirestoreListener<Review>() {
                    @Override
                    public void onSuccess() {
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.movie_container), R.string.review_sent, Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getColor(R.color.colorPrimaryMedium));

                        snackbar.show();
                    }

                    @Override
                    public void onSuccess(Review item) {}

                    @Override
                    public void onSuccess(List<Review> items) {}

                    @Override
                    public void onError() {
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.movie_container), R.string.review_error, Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getColor(R.color.colorPrimaryMedium));

                        snackbar.show();
                    }
                });
    }

    @Override
    public void onNegativeButtonClicked() {}

    @Override
    public void onNeutralButtonClicked() {}
}
