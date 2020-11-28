package com.movielix.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.movielix.firestore.FirestoreItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Review class.
 */
public class Review extends FirestoreItem {

    private static final String SCORE = "score";
    private static final String MOVIE = "movie";
    private static final String USER = "user";
    private static final String COMMENT = "comment";

    // Firestore fields
    private final int mScore;
    private final String mMovieId;
    private final String mUserId;
    private final String mComment;

    // Internal fields
    private Movie mMovie;

    public Review(int score, @NonNull String movieId, @NonNull String user, @Nullable String comment, @Nullable Movie movie) {
        this.mScore = score;
        this.mMovieId = movieId;
        this.mUserId = user;
        this.mComment = comment;
        this.mMovie = movie;
    }

    public Review(@NonNull String id, int score, @NonNull String movieId, @NonNull String user, @Nullable String comment, @Nullable Movie movie) {
        super(id);

        this.mScore = score;
        this.mMovieId = movieId;
        this.mUserId = user;
        this.mComment = comment;
        this.mMovie = movie;
    }

    public int getScore() {
        return mScore;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getComment() {
        return mComment;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public Movie getMovie() {
        return mMovie;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(SCORE, mScore);
        map.put(MOVIE, mMovieId);
        map.put(USER, mUserId);
        if (mComment != null) {
            map.put(COMMENT, mComment);
        }

        return map;
    }
}
