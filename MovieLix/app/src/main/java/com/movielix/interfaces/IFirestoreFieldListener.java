package com.movielix.interfaces;

import java.util.List;

public interface IFirestoreFieldListener<T> {

    void onSuccess(List<T> fields);
    void onError();
}
