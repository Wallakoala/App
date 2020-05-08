package com.movielix.firestore;

import com.movielix.bean.Movie;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a suggestions cache.
 */
class FirestoreSuggestionsCache {

    private static FirestoreSuggestionsCache sFirestoreMoviesCache;

    private Map<String, List<Movie>> mCache;

    private FirestoreSuggestionsCache() {
        mCache = new ConcurrentHashMap<>();
    }

    static FirestoreSuggestionsCache newInstance() {
        if (sFirestoreMoviesCache == null) {
            sFirestoreMoviesCache = new FirestoreSuggestionsCache();
        }

        return sFirestoreMoviesCache;
    }

    List<Movie> get(String search) {
        return mCache.get(search);
    }

    void add(String search, List<Movie> movies) {
        mCache.put(search, movies);
    }
}
