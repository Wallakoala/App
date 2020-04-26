package com.movielix.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.movielix.R;
import com.movielix.bean.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * RecyclerView adapter to display movies.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private final Context mContext;
    private final List<Movie> mMovies;

    public MoviesAdapter(final List<Movie> movies, final Context context) {
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
        holder.bindMovieItem(mMovies.get(position), (mMovies.size() - 1) == position);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    /**
     * Holder responsible to set all the attributes of this specific movie.
     */
    class MovieHolder extends RecyclerView.ViewHolder {

        private static final int EXTRA_PADDING = 32;

        private View mContainer;

        private TextView mTitle;
        private TextView mInfo;
        private TextView mDuration;
        private TextView mPGRating;
        private TextView mIMDBRating;

        private RoundedImageView mCover;

        MovieHolder(@NonNull View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.movie_container);
            mTitle = itemView.findViewById(R.id.movie_title);
            mInfo = itemView.findViewById(R.id.movie_release_year);
            mDuration = itemView.findViewById(R.id.movie_duration);
            mPGRating = itemView.findViewById(R.id.movie_pg_rating);
            mIMDBRating = itemView.findViewById(R.id.movie_imdb_rating);
            mCover = itemView.findViewById(R.id.movie_cover);
        }

        void bindMovieItem(final Movie movie, boolean last) {
            if (last) {
                float scale = mContext.getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (EXTRA_PADDING * scale + 0.5f);

                mContainer.setPadding(0, 0, 0, dpAsPixels);
            }

            mTitle.setText(movie.getTitle());

            String info = "(" + movie.getmReleaseYear() + ") - " + movie.getGenres()[0] + ", " + movie.getGenres()[1];
            mInfo.setText(info);

            mDuration.setText(movie.getDuration());

            mTitle.setText(movie.getTitle());

            Picasso.get()
                   .load(movie.getImageUrl().toString())
                   .into(mCover);
        }
    }
}
