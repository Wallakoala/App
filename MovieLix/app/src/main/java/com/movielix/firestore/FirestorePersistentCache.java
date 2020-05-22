package com.movielix.firestore;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.movielix.bean.Movie;
import com.movielix.constants.Constants;
import com.movielix.util.PersistentCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Implementation of a persistent cache.
 */
class FirestorePersistentCache {

    private static final String SUGGESTIONS_PREFIX = "suggestions_";

    private static FirestorePersistentCache sFirestorePersistentCache;

    private FirestorePersistentCache() {}

    static FirestorePersistentCache newInstance() {
        if (sFirestorePersistentCache == null) {
            sFirestorePersistentCache = new FirestorePersistentCache();
        }

        return sFirestorePersistentCache;
    }

    /**
     * Returns a movie if present in the cache.
     *
     * @param key: id of the movie.
     * @return movie if present, null otherwise.
     */
    @Nullable
    Movie get(Context context, @NonNull String key) {
        return new PersistentCache<Movie>(context).get(key, Movie.class);
    }

    /**
     * Adds a list of suggestions.
     *
     * @param suggestions: list of suggestions.
     */
    void putSuggestions(Context context, @NonNull List<Movie> suggestions) {
        Log.d(Constants.TAG, "[FirestorePersistentCache]::putSuggestions: adding new suggestions");

        // Add missing suggestions to the SharedPreferences
        PersistentCache<Movie> cache = new PersistentCache<>(context);
        Map<String, Movie> newMovies = new HashMap<>();
        for (Movie suggestion : suggestions) {
            String key = SUGGESTIONS_PREFIX + suggestion.getId();
            if (cache.get(key, Movie.class) == null) {
                Log.d(Constants.TAG, "[FirestorePersistentCache]::putSuggestions: suggestion misssing, adding movie (" + suggestion.getTitle() + ")");
                newMovies.put(key, suggestion);

            } else {
                Log.d(Constants.TAG, "[FirestorePersistentCache]::putSuggestions: suggestion already present, skipping movie (" + suggestion.getTitle() + ")");
            }
        }

        cache.putObjects(newMovies);
    }

    List<Movie> getSuggestions(Context context, @NonNull List<String> ids) {
        Log.d(Constants.TAG, "[FirestorePersistentCache]::getSuggestions: getting suggestions");

        List<Movie> suggestions = new ArrayList<>();
        PersistentCache<Movie> cache = new PersistentCache<>(context);

        for (String id : ids) {
            Movie movie = cache.get(SUGGESTIONS_PREFIX + id, Movie.class);
            if (movie != null) {
                Log.d(Constants.TAG, "[FirestorePersistentCache]::getSuggestions: cache hit, getting movie (" + movie.getTitle() + ")");
                suggestions.add(movie);
            } else {
                Log.d(Constants.TAG, "[FirestorePersistentCache]::getSuggestions: cache miss, id (" + id + ") is not present");
            }
        }

        return suggestions;
    }
}
