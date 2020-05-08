package com.movielix.adapter;

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
 * RecyclerView adapter to display movies suggestions.
 */
public class MoviesSuggestionAdapter extends RecyclerView.Adapter<MoviesSuggestionAdapter.MovieHolder> {

    private List<Movie> movies;

    public MoviesSuggestionAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_suggestion_item
                        , parent
                        , false);

        return new MovieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        holder.bindMovieItem(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * Holder responsible to set all the attributes of this specific movie.
     */
    class MovieHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mExtraInfo;

        private RoundedImageView mCover;

        MovieHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.movie_title);
            mCover = itemView.findViewById(R.id.movie_cover);
            mExtraInfo = itemView.findViewById(R.id.movie_extra_info);
        }

        void bindMovieItem(Movie movie) {
            mTitle.setText(movie.getTitle());

            Picasso.get()
                   .load(movie.getImageUrl())
                   .into(mCover);
        }
    }
}
