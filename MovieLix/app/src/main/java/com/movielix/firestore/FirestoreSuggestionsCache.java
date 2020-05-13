package com.movielix.firestore;

import com.movielix.bean.Movie;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

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

    /**
     * Returns the list of movies associated with a search
     * term if present in the cache.
     *
     * @param search: seartch term.
     * @return list of movies if present, null otherwise.
     */
    @Nullable
    List<Movie> get(String search) {
        return mCache.get(search);
    }

    /**
     * Adds a new entry to the cache.
     *
     * @param search: search term.
     * @param movies: list of movies associated with the search.
     */
    void add(String search, List<Movie> movies) {
        mCache.put(search, movies);
    }
}
