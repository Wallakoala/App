package com.movielix.firestore;

import androidx.annotation.Nullable;

public class FirestoreItem {

    protected String mId;

    protected FirestoreItem() {}

    protected FirestoreItem(String id) {
        this.mId = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof FirestoreItem) {
            return this.mId.equals(((FirestoreItem)obj).mId);
        } else {
            return false;
        }
    }
}
