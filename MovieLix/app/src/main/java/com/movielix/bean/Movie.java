package com.movielix.bean;

import java.util.List;

/**
 * Class that represents a movie.
 */
public class Movie extends LiteMovie {

    private String mOverview;

    public Movie(String id, String title, String overview, int releaseYear, int duration, String imageUrl, List<String> genres, PG_RATING pgRating, int imdbRating) {
        super(id, title, releaseYear, duration, imageUrl, genres, pgRating, imdbRating);

        this.mOverview = overview;
    }

    public String getOverview() { return mOverview; }
}
