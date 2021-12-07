package com.movielix.interfaces;

import com.movielix.firestore.FirestoreItem;

import java.util.List;

public interface IFirestoreListener<T extends FirestoreItem> {

    enum ErrCode {
        FATAL_ERROR,
        NOT_FOUND,
    }

    void onSuccess();
    void onSuccess(T item);
    void onSuccess(List<T> items);
    void onError(ErrCode reason);
}
