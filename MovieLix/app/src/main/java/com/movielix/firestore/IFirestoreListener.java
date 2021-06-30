package com.movielix.firestore;

import java.util.List;

public interface IFirestoreListener<T extends FirestoreItem> {

    void onSuccess();
    void onSuccess(T item);
    void onSuccess(List<T> items);
    void onError();
}
