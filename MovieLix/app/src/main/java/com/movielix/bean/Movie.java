package com.movielix.bean;

import java.util.List;

/**
 * Class that represents a movie.
 */
public class Movie {

    private String mTitle;
    private String mOverview;
    private int mDuration;
    private int mReleaseYear;
    private List<String> mGenres;
    private String mImageUrl;

    private Movie() {}

    public Movie(String title, String overview, int releaseYear, int duration, String imageUrl, List<String> genres) {
        this.mTitle = title;
        this.mOverview = overview;
        this.mReleaseYear = releaseYear;
        this.mDuration = duration;
        this.mImageUrl = imageUrl;
        this.mGenres = genres;
    }

    public static class Builder {

        private String title;
        private String overview;
        private int year;
        private int duration;
        private String imageUrl;
        private List<String> genres;

        public Builder() {}

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

        public Movie build() {
            return new Movie(title, overview, year, duration, imageUrl, genres);
        }
    }

    public String getTitle() { return mTitle; }
    public String getOverview() { return mOverview; }
    public int getReleaseYear() { return mReleaseYear; }
    public int getDuration() { return mDuration; }
    public String getImageUrl() { return mImageUrl; }
    public List<String> getGenres() { return mGenres; }

    public String getDurationAsStr() {
        int hours = mDuration / 60;
        int minutes = mDuration % 60;

        return hours + "h " + minutes + "min";
    }

    public String getGenresAsString() {
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
