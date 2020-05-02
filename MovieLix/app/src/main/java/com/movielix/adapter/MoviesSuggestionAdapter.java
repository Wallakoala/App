package com.movielix.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.movielix.R;
import com.movielix.bean.Movie;
import com.squareup.picasso.Picasso;

/**
 * RecyclerView adapter to display movies.
 */
public class MoviesSuggestionAdapter extends SuggestionsAdapter<Movie, MoviesSuggestionAdapter.MovieHolder> {

    public MoviesSuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
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
    public void onBindSuggestionHolder(Movie suggestion, MovieHolder holder, int position) {
        holder.bindMovieItem(suggestion);
    }

    @Override
    public int getSingleViewHeight() {
        return 80;
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

        void bindMovieItem(final Movie movie) {
            mTitle.setText(movie.getTitle());

            Picasso.get()
                   .load(movie.getImageUrl())
                   .into(mCover);
        }
    }
}
