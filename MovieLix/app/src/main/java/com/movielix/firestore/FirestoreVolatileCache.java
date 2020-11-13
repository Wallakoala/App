package com.movielix.firestore;

import com.movielix.bean.BaseMovie;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

/**
 * Implementation of a volatile cache.
 */
class FirestoreVolatileCache {

    private static FirestoreVolatileCache sFirestoreVolatileCache;

    private final Map<String, Object> mCache;

    private FirestoreVolatileCache() {
        mCache = new ConcurrentHashMap<>();
    }

    static FirestoreVolatileCache newInstance() {
        if (sFirestoreVolatileCache == null) {
            sFirestoreVolatileCache = new FirestoreVolatileCache();
        }

        return sFirestoreVolatileCache;
    }

    @Nullable
    Object get(String key) {
        return mCache.get(key);
    }

    void put(String key, Object value) {
        mCache.put(key, value);
    }
}
