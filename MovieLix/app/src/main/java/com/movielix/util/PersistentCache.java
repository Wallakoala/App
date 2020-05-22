package com.movielix.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Implementation of a persistent cache.
 *
 * @param <T> class type to be stored in the cache.
 */
public class PersistentCache<T> {

    private SharedPreferences mSharedPreferences;

    /**
     * Creates an object to access the persistent cache.
     *
     * @param context context object.
     */
    public PersistentCache(Context context) {
        mSharedPreferences =
                context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE);
    }

    /**
     * Puts a single object into the cache.
     *
     * @param key object's identifier.
     * @param object object to be stored.
     */
    public void put(@NonNull String key, @NonNull T object) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        String json = new Gson().toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    /**
     * Puts a set of objects.
     *
     * @param objects map containing the objects identified by their keys.
     */
    public void putObjects(@NonNull Map<String, T> objects) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        for (Map.Entry<String, T> entry : objects.entrySet()) {
            String json = gson.toJson(entry.getValue());

            editor.putString(entry.getKey(), json);
        }

        editor.apply();
    }

    /**
     * Puts a list of objects in a single entry.
     *
     * @param key list's identifier.
     * @param list list of objects.
     */
    public void putList(@NonNull String key, @NonNull List<T> list) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        String json = new Gson().toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    /**
     * Gets a cached object if present.
     *
     * @param key object's identifier.
     * @param clazz object's class type.
     * @return object of type clazz, null if not present in the cache.
     */
    @Nullable
    public T get(@NonNull String key, Class<T> clazz) {
        String json = mSharedPreferences.getString(key, null);
        if (json == null) {
            return null;
        }

        return new Gson().fromJson(json, clazz);
    }

    /**
     * Gets a cached list if present.
     *
     * @param key list's identifier.
     * @param clazz list's class type.
     * @return list of type clazz, null if not present in the cache.
     */
    @Nullable
    public List<T> getList(@NonNull String key, Class<T[]> clazz) {
        String json = mSharedPreferences.getString(key, null);
        if (json == null) {
            return null;
        }

        return Arrays.asList(new Gson().fromJson(json, clazz));
    }

    /**
     * Clears the cache.
     */
    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().apply();
    }
}
