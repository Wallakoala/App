package com.movielix.firestore;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.movielix.bean.BaseMovie;
import com.movielix.bean.Movie;
import com.movielix.constants.Constants;
import com.movielix.util.PersistentCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Implementation of a persistent cache for Firestore items.
 */
class FirestorePersistentCache {

    // Constants
    private static final String SUGGESTIONS_PREFIX = "suggestions_";

    // Singleton's reference.
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
     * @param context context object.
     * @param suggestions list of suggestions.
     */
    void putSuggestions(Context context, @NonNull List<BaseMovie> suggestions) {
        Log.d(Constants.TAG, "[FirestorePersistentCache]::putSuggestions: adding new suggestions");

        // Add missing suggestions to the SharedPreferences
        PersistentCache<BaseMovie> cache = new PersistentCache<>(context);
        Map<String, BaseMovie> missingMovies = new HashMap<>();
        for (BaseMovie suggestion : suggestions) {
            String key = SUGGESTIONS_PREFIX + suggestion.getId();
            if (cache.get(key, BaseMovie.class) == null) {
                Log.d(Constants.TAG,
                        "[FirestorePersistentCache]::putSuggestions: suggestion misssing, adding movie (" + suggestion.getTitle() + ")");
                missingMovies.put(key, suggestion);

            } else {
                Log.d(Constants.TAG,
                        "[FirestorePersistentCache]::putSuggestions: suggestion already present, skipping movie (" + suggestion.getTitle() + ")");
            }
        }

        cache.putObjects(missingMovies);
    }

    /**
     * Checks if the list of movies' ids are present in the cache. If they are, the movies are returned.
     *
     * @param context context object.
     * @param ids list of movies' identifier.
     * @return list of cached movies, emtpy list if none are found.
     */
    List<BaseMovie> getSuggestions(Context context, @NonNull List<String> ids) {
        Log.d(Constants.TAG, "[FirestorePersistentCache]::getSuggestions: getting suggestions");

        List<BaseMovie> suggestions = new ArrayList<>();
        PersistentCache<BaseMovie> cache = new PersistentCache<>(context);
        for (String id : ids) {
            BaseMovie movie = cache.get(SUGGESTIONS_PREFIX + id, BaseMovie.class);
            if (movie != null) {
                Log.d(Constants.TAG,
                        "[FirestorePersistentCache]::getSuggestions: cache hit, getting movie (" + movie.getTitle() + ")");
                suggestions.add(movie);

            } else {
                Log.d(Constants.TAG,
                        "[FirestorePersistentCache]::getSuggestions: cache miss, id (" + id + ") is not present");
            }
        }

        return suggestions;
    }
}
