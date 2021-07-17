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
    private static final String NUM_REVIEWS = "num_reviews";

    private final String mName;
    private final String mPhotoUrl;
    private final int mNumReviews;

    public User(@NonNull String id, @NonNull String name, @Nullable Uri photoUrl, int numReviews) {
        super(id);

        mName = name;
        if (photoUrl != null) {
            mPhotoUrl = photoUrl.toString();
        } else {
            mPhotoUrl = null;
        }

        this.mNumReviews = numReviews;
    }

    public String getName() { return mName; }
    public String getPhotoUrl() { return mPhotoUrl; }
    public int getNumReviews() { return mNumReviews; }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(ID, mId);
        map.put(NAME, mName);
        map.put(PHOTO_URL, mPhotoUrl);
        map.put(NUM_REVIEWS, mNumReviews);

        return map;
    }

    @Override
    public String toString() {
        return "User{" +
                "Name='" + mName + '\'' +
                ", PhotoUrl='" + mPhotoUrl + '\'' +
                ", NumReviews=" + mNumReviews +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }

        User user = (User) other;
        return super.mId.equals(user.mId);
    }
}
