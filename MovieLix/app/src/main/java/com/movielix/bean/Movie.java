package com.movielix.bean;

import com.movielix.firestore.FirestoreItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a movie.
 */
public class Movie extends FirestoreItem {

    public enum PG_RATING {
        G,
        PG,
        PG_13,
        R,
        NC_17,
        TV_Y,
        TV_Y7,
        TV_G,
        TV_PG,
        TV_14,
        TV_MA,
        NOT_RATED
    }

    private String mTitle;
    private String mOverview;
    private String mImageUrl;
    private PG_RATING mPGRating;
    private List<String> mGenres;
    private int mDuration;
    private int mReleaseYear;
    private int mIMDBRating;

    private Movie() {
        super();
    }

    public Movie(
            String id
            , String title
            , String overview
            , int releaseYear
            , int duration
            , String imageUrl
            , List<String> genres
            , PG_RATING pgRating
            , int imdbRating) {
        super(id);
        this.mTitle = title;
        this.mOverview = overview;
        this.mReleaseYear = releaseYear;
        this.mDuration = duration;
        this.mImageUrl = imageUrl;
        this.mGenres = genres;
        this.mIMDBRating = imdbRating;
        this.mPGRating = pgRating;
    }

    public static class Builder {

        private String id = "";
        private String title = "";
        private String overview = "";
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

        public Builder withOverview(String overview) {
            this.overview = overview;

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

        public Movie build() {
            return new Movie(
                      id
                    , title
                    , overview
                    , year
                    , duration
                    , imageUrl
                    , genres
                    , pgRating
                    , imdbRating);
        }
    }

    public String getId() { return super.mId; }
    public String getTitle() { return mTitle; }
    public String getOverview() { return mOverview; }
    public String getImageUrl() { return mImageUrl; }
    public PG_RATING getPGRating() { return mPGRating; }
    public List<String> getGenres() { return mGenres; }
    public int getReleaseYear() { return mReleaseYear; }
    public int getDuration() { return mDuration; }
    public int getIMDBRating() { return mIMDBRating; }

    public String getDurationAsStr() {
        int hours = mDuration / 60;
        int minutes = mDuration % 60;

        return hours + "h " + minutes + "min";
    }

    public String getGenresAsString() {
        if (mGenres.isEmpty()) {
            return "Sin especificar";
        }

        StringBuilder genres = new StringBuilder();
        for (int i = 0; i < mGenres.size(); ++i) {
            genres.append(mGenres.get(i));
            if (i < mGenres.size() - 1) {
                genres.append(", ");
            }
        }

        return genres.toString();
    }
}
