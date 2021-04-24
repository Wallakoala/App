package com.movielix.bean;

import com.movielix.firestore.FirestoreItem;

import java.util.List;

public class BaseMovie extends FirestoreItem {

    private final String mTitle;
    private final String mImageUrl;
    private final List<String> mGenres;
    private final int mReleaseYear;

    public BaseMovie(String id, String title, String image, List<String> genres, int release) {
        super(id);
        this.mTitle = title;
        this.mImageUrl = (image.isEmpty()) ? null : image;
        this.mGenres = genres;
        this.mReleaseYear = release;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getReleaseYear() {
        return mReleaseYear;
    }

    public String getGenresAsString() {
        if (mGenres.isEmpty()) {
            return "Sin especificar";
        }

        StringBuilder genres = new StringBuilder();
        for (int i = 0; i < mGenres.size(); ++i) {
            genres.append(mGenres.get(i));
            if (i < mGenres.size() - 1) {
                genres.append(", ");
            }
        }

        return genres.toString();
    }
}
