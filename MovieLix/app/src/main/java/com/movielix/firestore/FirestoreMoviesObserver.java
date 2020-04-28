package com.movielix.firestore;

import com.movielix.bean.Movie;

import java.util.List;

public interface FirestoreMoviesObserver {

    void onSuccess(List<Movie> movies);
    void onError();
}
