package com.movielix.firestore;

import com.movielix.bean.Movie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a movie cache to avoid too many requests to Firestore.
 */
class FirestoreMoviesCache {

    private static FirestoreMoviesCache sFirestoreMoviesCache;

    private Map<String, Movie> mCache;

    private FirestoreMoviesCache() {
        mCache = new ConcurrentHashMap<>();
    }

    static FirestoreMoviesCache newInstance() {
        if (sFirestoreMoviesCache == null) {
            sFirestoreMoviesCache = new FirestoreMoviesCache();
        }

        return sFirestoreMoviesCache;
    }

    Movie get(String id) {
        return mCache.get(id);
    }

    void add(String id, Movie movie) {
        mCache.put(id, movie);
    }
}
