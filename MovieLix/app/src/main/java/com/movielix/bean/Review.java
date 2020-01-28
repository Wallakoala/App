package com.movielix.bean;

import java.util.Date;

/**
 * Review class.
 */

public class Review {

    private float mScore;

    private Movie mMovie;
    private User mUser;
    private Date mDate;

    public Review(float score, Movie movie, User user, Date date) {
        this.mScore = score;
        this.mMovie = movie;
        this.mUser = user;
        this.mDate = date;
    }

    public float getScore() {
        return mScore;
    }

    public void setScore(float score) {
        this.mScore = score;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        this.mMovie = movie;
    }
}
