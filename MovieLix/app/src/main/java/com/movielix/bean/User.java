package com.movielix.bean;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.movielix.firestore.FirestoreItem;

import java.util.HashMap;
import java.util.Map;

/**
 * User class.
 */
public class User extends FirestoreItem {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHOTO_URL = "photo_url";

    private final String mName;
    private final String mPhotoUrl;

    public User(@NonNull String id, @NonNull String name, @Nullable Uri photoUrl) {
        super(id);

        mName = name;
        if (photoUrl != null) {
            mPhotoUrl = photoUrl.toString();
        } else {
            mPhotoUrl = "";
        }
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(ID, mId);
        map.put(NAME, mName);
        map.put(PHOTO_URL, mPhotoUrl);

        return map;
    }
}
