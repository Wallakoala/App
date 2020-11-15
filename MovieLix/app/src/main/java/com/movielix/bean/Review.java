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

    private final int mScore;
    private final String mMovieId;
    private final String mUserId;
    private final String mComment;

    public Review(int score, @NonNull String movie, @NonNull String user, @Nullable String comment) {
        this.mScore = score;
        this.mMovieId = movie;
        this.mUserId = user;
        this.mComment = comment;
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
