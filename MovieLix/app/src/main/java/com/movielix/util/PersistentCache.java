package com.movielix.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.movielix.constants.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class PersistentCache<T> {

    private SharedPreferences mSharedPreferences;

    public PersistentCache(Context context) {
        mSharedPreferences =
                context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE);
    }

    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().apply();
    }

    public void put(@NonNull String key, @NonNull T object) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        String json = new Gson().toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    public void putObjects(@NonNull Map<String, T> objects) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        for (Map.Entry<String, T> entry : objects.entrySet()) {
            String json = gson.toJson(entry.getValue());

            editor.putString(entry.getKey(), json);
        }

        editor.apply();
    }

    public void putList(@NonNull String key, @NonNull List<T> list) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        String json = new Gson().toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    @Nullable
    public T get(@NonNull String key, Class<T> clazz) {
        String json = mSharedPreferences.getString(key, null);
        if (json == null) {
            return null;
        }

        return new Gson().fromJson(json, clazz);
    }

    @Nullable
    public List<T> getList(@NonNull String key, Class<T[]> clazz) {
        String json = mSharedPreferences.getString(key, null);
        if (json == null) {
            return null;
        }

        return Arrays.asList(new Gson().fromJson(json, clazz));
    }
}
