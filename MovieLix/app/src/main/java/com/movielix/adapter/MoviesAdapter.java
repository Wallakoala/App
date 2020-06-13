package com.movielix.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.movielix.MovieActivity;
import com.movielix.R;
import com.movielix.bean.LiteMovie;
import com.movielix.constants.Constants;
import com.movielix.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * RecyclerView adapter to display movies.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private final Context mContext;
    private final List<LiteMovie> mMovies;

    public MoviesAdapter(final List<LiteMovie> movies, final Context context) {
        mMovies = movies;
        mContext = context;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item
                        , parent
                        , false);

        return new MovieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        holder.bindMovieItem(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    /**
     * Holder responsible to set all the attributes of this specific movie.
     */
    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LiteMovie mMovie;

        private TextView mTitle;
        private TextView mInfo;
        private TextView mDuration;
        private ImageView mPGRating;
        private TextView mIMDBRating;

        private RoundedImageView mCover;

        MovieHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.movie_title);
            mInfo = itemView.findViewById(R.id.movie_release_year);
            mDuration = itemView.findViewById(R.id.movie_duration);
            mPGRating = itemView.findViewById(R.id.movie_pg_rating);
            mIMDBRating = itemView.findViewById(R.id.movie_imdb_rating);
            mCover = itemView.findViewById(R.id.movie_cover);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindMovieItem(final LiteMovie movie) {
            mMovie = movie;

            mTitle.setText(movie.getTitle());
            mInfo.setText("(" + movie.getReleaseYear() + ") - " + movie.getGenresAsString());
            mDuration.setText(movie.getDurationAsStr());
            mIMDBRating.setText(Integer.toString(movie.getIMDBRating()));
            mIMDBRating.setTextColor(mContext.getColor(Util.getRatingColor(movie.getIMDBRating())));

            int pgRatingImage = Util.getRatingImage(movie.getPGRating());
            if (pgRatingImage == -1) {
                mPGRating.setVisibility(View.GONE);
            } else {
                mPGRating.setImageResource(pgRatingImage);
            }

            Picasso.get()
                   .load(movie.getImageUrl())
                   .into(mCover);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, MovieActivity.class);

            intent.putExtra(Constants.MOVIE_ID_INTENT, mMovie.getId());
            intent.putExtra(Constants.MOVIE_TITLE_INTENT, mMovie.getTitle());
            intent.putExtra(Constants.MOVIE_GENRES_INTENT, mMovie.getGenresAsString());
            intent.putExtra(Constants.MOVIE_RELEASE_YEAR_INTENT, mMovie.getReleaseYear());
            intent.putExtra(Constants.MOVIE_IMAGE_INTENT, mMovie.getImageUrl());

            mContext.startActivity(intent);
        }
    }
}
