package com.movielix.interfaces;

import com.movielix.firestore.FirestoreItem;

import java.util.List;

public interface IFirestoreListener<T extends FirestoreItem> {

    void onSuccess();
    void onSuccess(T item);
    void onSuccess(List<T> items);
    void onError();
}
