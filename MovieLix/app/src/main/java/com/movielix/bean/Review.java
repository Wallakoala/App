package com.movielix.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.movielix.firestore.FirestoreItem;

import java.util.Date;
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
    private static final String TIMESTAMP = "timestamp";

    // Firestore fields
    private final int mScore;
    private final String mMovieId;
    private final String mUserId;
    private final String mComment;
    private final Date mTimestamp;

    // Internal fields
    private final Movie mMovie;

    public Review(int score, @NonNull String movieId, @NonNull String user, @Nullable String comment, @Nullable Movie movie) {
        this.mScore = score;
        this.mMovieId = movieId;
        this.mUserId = user;
        this.mComment = comment;
        this.mMovie = movie;
        this.mTimestamp = new Date();
    }

    public Review(@NonNull String id, int score, @NonNull String movieId, @NonNull String user, @Nullable String comment, @Nullable Movie movie) {
        super(id);

        this.mScore = score;
        this.mMovieId = movieId;
        this.mUserId = user;
        this.mComment = comment;
        this.mMovie = movie;
        this.mTimestamp = new Date();
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
        map.put(TIMESTAMP, mTimestamp);
        if (mComment != null) {
            map.put(COMMENT, mComment);
        }

        return map;
    }

    @Override
    public String toString() {
        return "Review{" +
                "Score=" + mScore +
                ", MovieId='" + mMovieId + '\'' +
                ", UserId='" + mUserId + '\'' +
                ", Comment='" + mComment + '\'' +
                ", Timestamp=" + mTimestamp +
                '}';
    }
}
