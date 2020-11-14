package com.movielix.firestore;

import java.util.List;

public interface FirestoreListener<T extends FirestoreItem> {

    void onSuccess();
    void onSuccess(T item);
    void onSuccess(List<T> items);
    void onError();
}
