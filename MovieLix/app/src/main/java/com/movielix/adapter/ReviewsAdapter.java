package com.movielix.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.movielix.R;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter to display reviews.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {
    private final Context mContext;
    private final List<Review> mMovies;
    private final boolean mOwn;

    public ReviewsAdapter(final List<Review> reviews, boolean own, final Context context) {
        mMovies = reviews;
        mOwn = own;
        mContext = context;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item
                        , parent
                        , false);

        return new ReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        holder.bindReviewItem(mMovies.get(position), (mMovies.size() - 1) == position);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    /**
     * Holder responsible to set all the attributes of this specific review.
     */
    class ReviewHolder extends RecyclerView.ViewHolder {

        private static final int EXTRA_PADDING = 72;

        private final View mContainer;

        private final TextView mTitle;
        private final TextView mInfo;
        private final TextView mOverview;
        private final TextView mDuration;
        private final TextView mRatingVar;
        private final TextView mProfileName;
        private final TextView mIMDBRating;
        private final ImageView mPGRating;
        private final RoundedImageView mCover;
        private final CircleImageView mProfilePic;

        ReviewHolder(@NonNull View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.review_container);
            mTitle = itemView.findViewById(R.id.review_title);
            mInfo = itemView.findViewById(R.id.review_release_year);
            mOverview = itemView.findViewById(R.id.review_overview);
            mDuration = itemView.findViewById(R.id.review_duration);
            mRatingVar = itemView.findViewById(R.id.review_rating_var);
            mProfileName = itemView.findViewById(R.id.review_profile_name);
            mCover = itemView.findViewById(R.id.review_cover);
            mProfilePic = itemView.findViewById(R.id.review_profile_pic);
            mIMDBRating = itemView.findViewById(R.id.review_imdb_rating);
            mPGRating = itemView.findViewById(R.id.review_pg_rating);
        }

        @SuppressLint("SetTextI18n")
        void bindReviewItem(final Review review, boolean last) {
            if (last) {
                float scale = mContext.getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (EXTRA_PADDING * scale + 0.5f);

                mContainer.setPadding(0, 0, 0, dpAsPixels);
            }

            Movie movie = review.getMovie();

            mInfo.setText("(" + movie.getReleaseYear() + ") - " + movie.getGenresAsString());
            mOverview.setText(movie.getOverview());
            mTitle.setText(movie.getTitle());
            mDuration.setText(movie.getDurationAsStr());
            mIMDBRating.setText(Integer.toString(movie.getIMDBRating()));
            mIMDBRating.setTextColor(mContext.getColor(Util.getRatingColor(movie.getIMDBRating())));
            mRatingVar.setText(review.getScore() + " /");
            mProfilePic.setImageResource(R.drawable.ic_default_profile_pic);
            if (mOwn) {
                mProfileName.setText("La has valorado con:");
                Uri photo = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl();
                if (photo != null) {
                    Picasso.get()
                            .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                            .error(R.color.textIdle)
                            .into(mProfilePic);
                }

            } else {
                // todo #28 - get the friend's name and profile pic somehow.
            }

            int pgRatingImage = Util.getRatingImage(movie.getPGRating());
            if (pgRatingImage == -1) {
                mPGRating.setVisibility(View.GONE);
            } else {
                mPGRating.setImageResource(pgRatingImage);
            }

            Picasso.get()
                   .load(movie.getImageUrl())
                   .error(R.color.textIdle)
                   .into(mCover);
        }
    }
}
