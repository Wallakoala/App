package com.movielix.bean;

import java.net.URL;
import java.util.List;

/**
 * Class that represents a movie.
 */
public class Movie
{
    private String mTitle;
    private String mOverview;

    private String mDuration;
    private int mReleaseYear;

    private String[] mGenres;

    private URL mImageUrl;

    public Movie() {}

    public Movie(String title, String overview, int releaseYear, String duration, URL imageUrl, String[] genres)
    {
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
    public URL getImageUrl() { return mImageUrl; }
    public String[] getGenres() { return mGenres; }

    public Movie setTitle(String title)
    {
        this.mTitle = title;
        return this;
    }

    public Movie setOverview(String overview)
    {
        this.mOverview = overview;
        return this;
    }

    public Movie setmReleaseYear(int mReleaseYear)
    {
        this.mReleaseYear = mReleaseYear;
        return this;
    }

    public Movie setDuration(String duration)
    {
        this.mDuration = duration;
        return this;
    }

    public Movie setImageUrl(URL imageUrl)
    {
        this.mImageUrl = imageUrl;
        return this;
    }

    public Movie setGenres(String[] genres)
    {
        this.mGenres = genres;
        return this;
    }
}
