package com.movielix.bean;

import android.util.Log;

import androidx.annotation.NonNull;

import com.movielix.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a movie.
 */
public class Movie extends LiteMovie {

    private final String mOverview;

    public Movie(
            String id,
            String title,
            String overview,
            int releaseYear,
            int duration,
            String imageUrl,
            List<String> genres,
            PG_RATING pgRating,
            int imdbRating)
    {
        super(id, title, releaseYear, duration, imageUrl, genres, pgRating, imdbRating);

        this.mOverview = overview;
    }

    public static class Builder {

        private String id = "";
        private String title = "";
        private int year = 0;
        private int duration = 0;
        private int imdbRating = 0;
        private PG_RATING pgRating = PG_RATING.NOT_RATED;
        private String imageUrl = "";
        private List<String> genres = new ArrayList<>();
        private String overview = "";

        public Builder() {}

        public Builder withId(String id) {
            this.id = id;

            return this;
        }

        public Builder titled(String title) {
            this.title = title;

            return this;
        }

        public Builder releasedIn(int year) {
            this.year = year;

            return this;
        }

        public Builder lasts(int duration) {
            this.duration = duration;

            return this;
        }

        public Builder withImage(String imageUrl) {
            this.imageUrl = imageUrl;

            return this;
        }

        public Builder categorizedAs(List<String> genres) {
            this.genres = genres;

            return this;
        }

        public Builder classifiedAs(PG_RATING pgRating) {
            this.pgRating = pgRating;

            return this;
        }

        public Builder rated(int imdbRating) {
            this.imdbRating = imdbRating;

            return this;
        }

        public Builder withOverview(String overview) {
            this.overview = overview;

            return this;
        }

        public Movie build() {
            return new Movie(id, title, overview, year, duration, imageUrl, genres, pgRating, imdbRating);
        }
    }

    public String getOverview() { return mOverview; }
}
