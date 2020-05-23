package com.movielix.bean;

import com.movielix.firestore.FirestoreItem;

import java.util.List;

public class BaseMovie extends FirestoreItem {

    private String mTitle;
    private String mImageUrl;
    private List<String> mGenres;
    private int mReleaseYear;

    BaseMovie() {
        super();
    }

    public BaseMovie(String id, String title, String image, List<String> genres, int release) {
        super(id);
        this.mTitle = title;
        this.mImageUrl = image;
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
