package com.movielix.firestore;

import java.util.List;

public interface FirestoreListener<T> {

    void onSuccess(List<T> items);
    void onError();
}
