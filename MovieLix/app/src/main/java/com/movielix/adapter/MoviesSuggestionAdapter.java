package com.movielix.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.movielix.MovieActivity;
import com.movielix.R;
import com.movielix.bean.BaseMovie;
import com.movielix.constants.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Pattern;

/**
 * RecyclerView adapter to display movies suggestions.
 */
public class MoviesSuggestionAdapter extends RecyclerView.Adapter<MoviesSuggestionAdapter.MovieHolder> {

    private Context mContext;
    private List<BaseMovie> mMovies;
    private String mSearch;

    public MoviesSuggestionAdapter(Context context, List<BaseMovie> movies, String search) {
        this.mContext = context;
        this.mMovies = movies;
        this.mSearch = search;
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

        private BaseMovie mMovie;

        private TextView mTitle;
        private TextView mExtraInfo;

        private RoundedImageView mCover;

        MovieHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.movie_title);
            mCover = itemView.findViewById(R.id.movie_cover);
            mExtraInfo = itemView.findViewById(R.id.movie_extra_info);

            itemView.setOnClickListener(this);
        }

        void bindMovieItem(BaseMovie movie) {
            mMovie = movie;

            int colorAccent = mContext.getResources().getColor(R.color.colorAccent, mContext.getTheme());
            String highlightColor = String.format("%X", colorAccent).substring(2);

            String highlightedTitle = movie.getTitle().replaceAll(
                    "(?i)" + Pattern.quote(mSearch)
                    , String.format("<font color=\"#%s\">" + mSearch + "</font>", highlightColor));

            mTitle.setText(Html.fromHtml(highlightedTitle));
            mExtraInfo.setText(
                    mContext.getText(R.string.movie_suggestion_extra_info)
                            .toString()
                            .replace("%1", Integer.toString(movie.getReleaseYear()))
                            .replace("%2", movie.getGenresAsString()));

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
