package com.movielix.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a movie.
 */
public class LiteMovie extends BaseMovie {

    public enum PG_RATING {
        G, PG, PG_13, R, NC_17, TV_Y, TV_Y7, TV_G, TV_PG, TV_14, TV_MA, NOT_RATED
    }

    private final PG_RATING mPGRating;
    private final int mDuration;
    private final int mIMDBRating;

    LiteMovie(String id, String title, int releaseYear, int duration, String imageUrl, List<String> genres, PG_RATING pgRating, int imdbRating) {
        super(id, title, imageUrl, genres, releaseYear);

        this.mDuration = duration;
        this.mIMDBRating = imdbRating;
        this.mPGRating = pgRating;
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

        public LiteMovie build() {
            return new LiteMovie(id, title, year, duration, imageUrl, genres, pgRating, imdbRating);
        }
    }

    public PG_RATING getPGRating() { return mPGRating; }
    public int getIMDBRating() { return mIMDBRating; }

    public String getDurationAsStr() {
        int hours = mDuration / 60;
        int minutes = mDuration % 60;

        return hours + "h " + minutes + "min";
    }
}
