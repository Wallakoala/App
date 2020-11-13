package com.movielix.firestore;

import java.util.List;

public interface FirestoreListener<T extends FirestoreItem> {

    void onSuccess(FirestoreItem.Type type);
    void onSuccess(FirestoreItem.Type type, T item);
    void onSuccess(FirestoreItem.Type type, List<T> items);
    void onError(FirestoreItem.Type type);
}
