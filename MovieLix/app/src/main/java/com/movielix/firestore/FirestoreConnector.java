package com.movielix.firestore;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.movielix.R;
import com.movielix.bean.Movie;
import com.movielix.constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

@SuppressWarnings("unchecked")
public class FirestoreConnector {

    // Collections names
    private static final String MOVIES_SEARCH_COLLECTION = "movies_search";
    private static final String MOVIES_LITE_COLLECTION = "movies_lite";
    private static final String MOVIES_SUGGESTIONS_COLLECTION = "movies_suggestions";

    // Document fields names
    private static final String MOVIE_TITLE = "2";
    private static final String MOVIE_RELEASE_YEAR = "3";
    private static final String MOVIE_DURATION = "5";
    private static final String MOVIE_IMDB_RATING = "6";
    private static final String MOVIE_IMAGE_URL = "8";
    private static final String MOVIE_GENRES = "9";
    private static final String MOVIE_PG_RATING = "10";

    private static final int MAX_SUGGESTIONS = 10;

    private static FirestoreConnector sFirestoreConnector;

    final private Deque<String> mRequestStack = new ConcurrentLinkedDeque<>();

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore mDb;

    // Caches
    private FirestorePersistentCache mPersistentCache;
    private FirestoreVolatileCache mVolatileCache;

    private FirestoreConnector() {
        mDb = FirebaseFirestore.getInstance();
        mVolatileCache = FirestoreVolatileCache.newInstance();
        mPersistentCache = FirestorePersistentCache.newInstance();

        Log.d(Constants.TAG, "[FirestoreConnector] succesfully initialized");
    }

    public static FirestoreConnector newInstance() {
        if (sFirestoreConnector == null) {
            sFirestoreConnector = new FirestoreConnector();
        }

        return sFirestoreConnector;
    }

    public List<Movie> getDummyMovies(Context context) {
        List<Movie> movies = new ArrayList<>();
        Movie movie = new Movie(
                "0"
                , "La La Land"
                , context.getString(R.string.reviews_item_movie_overview)
                , 2016
                , 128
                , "https://m.media-amazon.com/images/M/MV5BMzUzNDM2NzM2MV5BMl5BanBnXkFtZTgwNTM3NTg4OTE@._V1_SX300.jpg"
                , Arrays.asList("Comedia", "Romance")
                , Movie.PG_RATING.G
                , 83);

        movies.add(movie);

        movie = new Movie(
                "1"
                , "Capitán América: El primer vengador"
                , "Nacido durante la Gran Depresión, Steve Rogers creció como un chico enclenque en una familia pobre. Horrorizado por las noticias que llegaban de Europa sobre los nazis, decidió enrolarse en el ejército; sin embargo, debido a su precaria salud, fue rechazado una y otra vez. Enternecido por sus súplicas, el General Chester Phillips le ofrece la oportunidad de tomar parte en un experimento especial. la \\\"Operación Renacimiento\\\". Después de admi"
                , 2014
                , 124
                , "https://m.media-amazon.com/images/M/MV5BMTYzOTc2NzU3N15BMl5BanBnXkFtZTcwNjY3MDE3NQ@@._V1_SX300.jpg"
                , Arrays.asList("Acción", "Aventura")
                , Movie.PG_RATING.PG
                , 72);

        movies.add(movie);

        movie = new Movie(
                "2"
                , "Django desencadenado"
                , "Dos años antes de estallar la Guerra Civil (1861-1865), Schultz, un cazarrecompensas alemán que le sigue la pista a unos asesinos, le promete al esclavo Django dejarlo en libertad si le ayuda a atraparlos. Terminado con éxito el trabajo, Django prefiere seguir al lado del alemán y ayudarle a capturar a los delincuentes más buscados del Sur. Se convierte así en un experto cazador de recompensas, pero su único objetivo es rescatar a su esposa Broomhilda, a la que perdió por culpa del tráfico de esclavos. La búsqueda llevará a Django y a Schultz hasta Calvin Candie, el malvado propietario"
                , 2012
                , 165
                , "https://m.media-amazon.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX300.jpg"
                , Arrays.asList("Drama", "Western")
                , Movie.PG_RATING.R
                , 90);

        movies.add(movie);

        return movies;
    }

    /**
     * Method to retrieve a list of movie suggestions based on the search.
     *
     * @param search: search terms.
     * @param listener: object of type FirestoreListener.
     */
    public void getMoviesSuggestionsByTitle(final Context context, final String search, final FirestoreListener listener) {
        Log.d(Constants.TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: request to get suggestions by searching: " + search);

        final String search_term = search.toLowerCase();

        // See if the same search has been done in this session.
        final List<Movie> volatileSuggestions = mVolatileCache.getSearch(search_term);
        if (volatileSuggestions != null) {
            Log.d(Constants.TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: volatile cache hit");
            listener.onSuccess(volatileSuggestions);
            return;
        }

        mRequestStack.push(search_term);
        mDb.collection(MOVIES_SEARCH_COLLECTION)
                .whereGreaterThanOrEqualTo(MOVIE_TITLE, search_term)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(Constants.TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: found ids");
                        if (task.isSuccessful()) {
                            List<String> ids = new ArrayList<>();
                            // Everything went well, let's get the ids of all the documents
                            if (task.getResult() != null) {
                                ids = filterIds(task.getResult(), search_term);
                            }

                            final List<Movie> movies = new ArrayList<>();
                            if (!ids.isEmpty()) {
                                // Now let's search for those ids

                                // First, see if there are cached movies
                                final List<Movie> persistentSuggestions = mPersistentCache.getSuggestions(context, ids);
                                for (Movie suggestion : persistentSuggestions) {
                                    ids.remove(suggestion.getId());
                                }

                                if (!ids.isEmpty()) {
                                    mDb.collection(MOVIES_SUGGESTIONS_COLLECTION)
                                            .whereIn(FieldPath.documentId(), ids)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful() && (task.getResult() != null)) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String title = document.getString(MOVIE_TITLE);
                                                            String imageUrl = document.getString(MOVIE_IMAGE_URL);
                                                            List<String> genres = (ArrayList<String>) document.get(MOVIE_GENRES);
                                                            int year = Objects.requireNonNull(document.getLong(MOVIE_RELEASE_YEAR)).intValue();

                                                            movies.add(new Movie.Builder()
                                                                    .withId(document.getId())
                                                                    .titled(title)
                                                                    .releasedIn(year)
                                                                    .withImage(imageUrl)
                                                                    .categorizedAs(genres)
                                                                    .build());
                                                        }

                                                        mVolatileCache.addSearch(search_term, movies);
                                                        mPersistentCache.putSuggestions(context, movies);

                                                        movies.addAll(persistentSuggestions);
                                                        listener.onSuccess(movies);

                                                    } else {
                                                        Log.w(Constants.TAG, "[FirestoreConnector] error getting movies suggestions.", task.getException());
                                                        listener.onError();
                                                    }
                                                }
                                            });
                                } else {
                                    movies.addAll(persistentSuggestions);
                                    mVolatileCache.addSearch(search_term, movies);
                                }
                            }

                            listener.onSuccess(movies);

                        } else {
                            Log.w(Constants.TAG, "[FirestoreConnector] error searching movies.", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    public void getMoviesByTitle(String search, final FirestoreListener listener) {
        final String search_term = search.toLowerCase();
        mDb.collection(MOVIES_SEARCH_COLLECTION)
                .whereGreaterThanOrEqualTo(MOVIE_TITLE, search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> ids = new ArrayList<>();
                            final List<Movie> movies = new ArrayList<>();
                            // Everything went well, let's get the ids of all the documents
                            if (task.getResult() != null) {
                                ids = filterIds(task.getResult(), search_term);
                            }

                            if (!ids.isEmpty()) {
                                // Now let's search for those ids
                                mDb.collection(MOVIES_LITE_COLLECTION)
                                        .whereIn(FieldPath.documentId(), ids)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult() != null) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String title = document.getString(MOVIE_TITLE);
                                                            String imageUrl = document.getString(MOVIE_IMAGE_URL);
                                                            String pgRatingStr = document.getString(MOVIE_PG_RATING);
                                                            List<String> genres = (ArrayList<String>) document.get(MOVIE_GENRES);
                                                            int releaseYear = Objects.requireNonNull(document.getLong(MOVIE_RELEASE_YEAR)).intValue();
                                                            int duration = Objects.requireNonNull(document.getLong(MOVIE_DURATION)).intValue();
                                                            int imdbRating = (int)((Objects.requireNonNull(document.getDouble(MOVIE_IMDB_RATING))) * 10);

                                                            Movie.PG_RATING pgRating = Movie.PG_RATING.NOT_RATED;
                                                            if (pgRatingStr != null) {
                                                                if (pgRatingStr.equalsIgnoreCase("G")) {
                                                                    pgRating = Movie.PG_RATING.G;
                                                                } else if (pgRatingStr.equalsIgnoreCase("PG")) {
                                                                    pgRating = Movie.PG_RATING.PG;
                                                                } else if (pgRatingStr.equalsIgnoreCase("PG-13")) {
                                                                    pgRating = Movie.PG_RATING.PG_13;
                                                                } else if (pgRatingStr.equalsIgnoreCase("R")) {
                                                                    pgRating = Movie.PG_RATING.R;
                                                                } else if (pgRatingStr.equalsIgnoreCase("NC-17")) {
                                                                    pgRating = Movie.PG_RATING.NC_17;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-Y")) {
                                                                    pgRating = Movie.PG_RATING.TV_Y;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-Y7")) {
                                                                    pgRating = Movie.PG_RATING.TV_Y7;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-G")) {
                                                                    pgRating = Movie.PG_RATING.TV_G;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-PG")) {
                                                                    pgRating = Movie.PG_RATING.TV_PG;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-14")) {
                                                                    pgRating = Movie.PG_RATING.TV_14;
                                                                } else if (pgRatingStr.equalsIgnoreCase("PG-MA")) {
                                                                    pgRating = Movie.PG_RATING.TV_MA;
                                                                }
                                                            }

                                                            movies.add(new Movie.Builder()
                                                                    .withId(document.getId())
                                                                    .titled(title)
                                                                    .withImage(imageUrl)
                                                                    .releasedIn(releaseYear)
                                                                    .lasts(duration)
                                                                    .categorizedAs(genres)
                                                                    .classifiedAs(pgRating)
                                                                    .rated(imdbRating)
                                                                    .build());
                                                        }

                                                        listener.onSuccess(movies);
                                                    }

                                                } else {
                                                    Log.w(Constants.TAG, "Error getting movies.", task.getException());
                                                    listener.onError();
                                                }
                                            }
                                        });
                            } else {
                                listener.onSuccess(movies);
                            }

                        } else {
                            Log.w(Constants.TAG, "Error searching movies.", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    private List<String> filterIds(QuerySnapshot task, String search_term) {
        List<String> ids = new ArrayList<>();
        for (QueryDocumentSnapshot document : task) {
            // Firestore compares strings lexicographically, and that's not exactly what we want, so
            // let's filter the movies retrieved.
            if (ids.size() < MAX_SUGGESTIONS) {
                //noinspection ConstantConditions
                if (document.getString(MOVIE_TITLE).startsWith(search_term)) {
                    ids.add(document.getId());
                }

            } else {
                break;
            }
        }

        for (QueryDocumentSnapshot document : task) {
            if (ids.size() < MAX_SUGGESTIONS) {
                //noinspection ConstantConditions
                if (document.getString(MOVIE_TITLE).contains(search_term) && !ids.contains(document.getId())) {
                    ids.add(document.getId());
                }

            } else {
                break;
            }
        }

        return ids;
    }
}
