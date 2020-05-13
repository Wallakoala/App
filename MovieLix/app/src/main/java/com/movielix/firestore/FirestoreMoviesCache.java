package com.movielix.firestore;

import com.movielix.bean.Movie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

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

    /**
     * Returns a movie if present in the cache.
     *
     * @param id: id of the movie.
     * @return movie if present, null otherwise.
     */
    @Nullable
    Movie get(String id) {
        return mCache.get(id);
    }

    /**
     * Adds a new movie in the cache.
     *
     * @param id: id of the movie.
     * @param movie: movie.
     */
    void add(String id, Movie movie) {
        mCache.put(id, movie);
    }
}
