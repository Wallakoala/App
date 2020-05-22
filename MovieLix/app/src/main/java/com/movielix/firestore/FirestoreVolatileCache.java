package com.movielix.firestore;

import com.movielix.bean.Movie;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

/**
 * Implementation of a volatile cache.
 */
class FirestoreVolatileCache {

    private static FirestoreVolatileCache sFirestoreVolatileCache;

    private Map<String, List<Movie>> mSearchCache;

    private FirestoreVolatileCache() {
        mSearchCache = new ConcurrentHashMap<>();
    }

    static FirestoreVolatileCache newInstance() {
        if (sFirestoreVolatileCache == null) {
            sFirestoreVolatileCache = new FirestoreVolatileCache();
        }

        return sFirestoreVolatileCache;
    }

    /**
     * Returns the list of movies associated with a search
     * term if present in the cache.
     *
     * @param search: seartch term.
     * @return list of movies if present, null otherwise.
     */
    @Nullable
    List<Movie> getSearch(String search) {
        return mSearchCache.get(search);
    }

    /**
     * Adds a new entry to the cache.
     *
     * @param search: search term.
     * @param movies: list of movies associated with the search.
     */
    void addSearch(String search, List<Movie> movies) {
        mSearchCache.put(search, movies);
    }
}
