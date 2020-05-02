package com.movielix.bean;

/**
 * Class that represents a movie.
 */
public class Movie {

    private String mTitle;
    private String mOverview;

    private String mDuration;
    private int mReleaseYear;

    private String[] mGenres;

    private String mImageUrl;

    public Movie(String title, String overview, int releaseYear, String duration, String imageUrl, String[] genres) {
        this.mTitle = title;
        this.mOverview = overview;
        this.mReleaseYear = releaseYear;
        this.mDuration = duration;
        this.mImageUrl = imageUrl;
        this.mGenres = genres;
    }

    public String getTitle() { return mTitle; }
    public String getOverview() { return mOverview; }
    public int getmReleaseYear() { return mReleaseYear; }
    public String getDuration() { return mDuration; }
    public String getImageUrl() { return mImageUrl; }
    public String[] getGenres() { return mGenres; }
}
