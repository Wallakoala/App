package com.movielix.firestore;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.movielix.MovieActivity;
import com.movielix.R;
import com.movielix.bean.BaseMovie;
import com.movielix.bean.LiteMovie;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.bean.User;
import com.movielix.constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.movielix.constants.Constants.TAG;

@SuppressWarnings("unchecked")
public class FirestoreConnector {

    // Collections names
    private static final String MOVIES_SEARCH_COLLECTION = "movies_search";
    private static final String MOVIES_LITE_COLLECTION = "movies_lite";
    private static final String MOVIES_COLLECTION = "movies";
    private static final String MOVIES_SUGGESTIONS_COLLECTION = "movies_suggestions";
    private static final String REVIEWS_COLLECTION = "reviews";
    private static final String USERS_COLLECTION = "users";

    // Document fields names
    private static final String MOVIE_TITLE = "2";
    private static final String MOVIE_RELEASE_YEAR = "3";
    private static final String MOVIE_OVERVIEW = "4";
    private static final String MOVIE_DURATION = "5";
    private static final String MOVIE_IMDB_RATING = "6";
    private static final String MOVIE_IMAGE_URL = "8";
    private static final String MOVIE_GENRES = "9";
    private static final String MOVIE_PG_RATING = "10";

    private static final int MAX_SUGGESTIONS = 10;

    private static FirestoreConnector sFirestoreConnector;

    private String mLastSearch;

    // Access a Cloud Firestore instance from your Activity
    private final FirebaseFirestore mDb;

    // Caches
    private final FirestorePersistentCache mPersistentCache;
    private final FirestoreVolatileCache mVolatileCache;

    private FirestoreConnector() {
        mDb = FirebaseFirestore.getInstance();
        mVolatileCache = FirestoreVolatileCache.newInstance();
        mPersistentCache = FirestorePersistentCache.newInstance();

        mLastSearch = "";

        Log.d(TAG, "[FirestoreConnector] succesfully initialized");
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
     * @param context context object.
     * @param search search terms.
     * @param listener object of type FirestoreListener to be notifed with the result.
     */
    public void getMoviesSuggestionsByTitle(final Context context, final String search, final FirestoreListener<BaseMovie> listener) {
        Log.d(TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: request to get suggestions by searching: " + search);

        final String search_term = search.toLowerCase();

        /* Step 1
         * See if the same search has been done in this session.
         */
        Object cachedResult = mVolatileCache.get(search_term);
        if (cachedResult != null) {
            Log.d(TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: volatile cache hit");
            listener.onSuccess(FirestoreItem.Type.MOVIE, (List<BaseMovie>) cachedResult);
            return;
        }

        /* Step 2
         * Search first in the `movies_search` collection to get the ids of the matching movies.
         *
         * Set the request so that only the last one is processed.
         */
        setLastSearch(search_term);
        mDb.collection(MOVIES_SEARCH_COLLECTION)
                .whereGreaterThanOrEqualTo(MOVIE_TITLE, search_term)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            /* Step 3
                             * Everything went well, let's filter the ids of all the documents received.
                             */
                            final List<String> ids = filterIds(Objects.requireNonNull(task.getResult()), search_term);
                            final List<BaseMovie> movies = new ArrayList<>();

                            if (!ids.isEmpty()) {
                                /* Step 4
                                 * Now let's search for those ids.
                                 *
                                 * First, see if the ids received are present in the persistent cache.
                                 */
                                final List<BaseMovie> persistentSuggestions = mPersistentCache.getSuggestions(context, ids);
                                for (BaseMovie suggestion : persistentSuggestions) {
                                    ids.remove(suggestion.getId());
                                }

                                /* Step 5
                                 * Retrieve the movies that are not cached.
                                 */
                                if (!ids.isEmpty()) {
                                    mDb.collection(MOVIES_SUGGESTIONS_COLLECTION)
                                            .whereIn(FieldPath.documentId(), ids)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful() && (task.getResult() != null)) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            try {
                                                                String title = document.getString(MOVIE_TITLE);
                                                                String imageUrl = document.getString(MOVIE_IMAGE_URL);
                                                                List<String> genres = (ArrayList<String>) document.get(MOVIE_GENRES);
                                                                int year = Objects.requireNonNull(document.getLong(MOVIE_RELEASE_YEAR)).intValue();

                                                                movies.add(new BaseMovie(document.getId(), title, imageUrl, genres, year));

                                                            } catch (Exception e) {
                                                                Log.e(TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: error parsing movies.", e);
                                                            }
                                                        }

                                                        /* Step 6
                                                         * Update the volatile and persistent caches.
                                                         */
                                                        mVolatileCache.put(search_term, movies);
                                                        mPersistentCache.putSuggestions(context, movies);

                                                        /* Step 7
                                                         * Notify the listener if it's the last request.
                                                         */
                                                        if (isLastSearch(search_term)) {
                                                            Log.d(TAG,
                                                                    "[FirestoreConnector]::getMoviesSuggestionsByTitle: last request, notifying the listener");

                                                            clearLastSearch();
                                                            movies.addAll(persistentSuggestions);
                                                            listener.onSuccess(FirestoreItem.Type.MOVIE, movies);

                                                        } else {
                                                            Log.d(TAG,
                                                                    "[FirestoreConnector]::getMoviesSuggestionsByTitle: not the last request, discarding results");
                                                        }

                                                    } else {
                                                        Log.w(TAG,
                                                                "[FirestoreConnector]::getMoviesSuggestionsByTitle: error getting movies suggestions.", task.getException());

                                                        if (isLastSearch(search_term)) {
                                                            clearLastSearch();
                                                            listener.onError(FirestoreItem.Type.MOVIE);
                                                        }
                                                    }
                                                }
                                            });

                                } else {
                                    // All the movies are cached, no need to connect with Firestore.
                                    mVolatileCache.put(search_term, persistentSuggestions);
                                    if (isLastSearch(search_term)) {
                                        Log.d(TAG,
                                                "[FirestoreConnector]::getMoviesSuggestionsByTitle: last request, notifying the listener");

                                        // This request is the last one, so we notify the listener.
                                        clearLastSearch();
                                        listener.onSuccess(FirestoreItem.Type.MOVIE, persistentSuggestions);

                                    } else {
                                        Log.d(TAG,
                                                "[FirestoreConnector]::getMoviesSuggestionsByTitle: not the last request, discarding results");
                                    }
                                }

                            } else {
                                // No ids retrieved.
                                if (isLastSearch(search_term)) {
                                    Log.d(TAG,
                                            "[FirestoreConnector]::getMoviesSuggestionsByTitle: last request, notifying the listener");

                                    // This request is the last one, so we notify the listener.
                                    clearLastSearch();
                                    listener.onSuccess(FirestoreItem.Type.MOVIE, movies);

                                } else {
                                    Log.d(TAG,
                                            "[FirestoreConnector]::getMoviesSuggestionsByTitle: not the last request, discarding results");
                                }
                            }

                        } else {
                            // Error retrieving the ids.
                            Log.w(TAG,
                                    "[FirestoreConnector]::getMoviesSuggestionsByTitle: error searching movies.", task.getException());

                            if (isLastSearch(search_term)) {
                                clearLastSearch();
                                listener.onError(FirestoreItem.Type.MOVIE);
                            }
                        }
                    }
                });
    }

    /**
     * Method to retrieve movies based on the search term.
     *
     * @param search search terms.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void getMoviesByTitle(String search, final FirestoreListener<LiteMovie> listener) {
        Log.d(TAG, "[FirestoreConnector]::getMoviesByTitle: request to get movies by searching: " + search);

        final String search_term = search.toLowerCase();

        /* Step 1
         * Search first in the `movies_search` collection to get the ids of the matching movies.
         */
        mDb.collection(MOVIES_SEARCH_COLLECTION)
                .whereGreaterThanOrEqualTo(MOVIE_TITLE, search)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            /* Step 2
                             * Everything went well, let's filter the ids of all the documents received.
                             */
                            final List<String> ids = filterIds(Objects.requireNonNull(task.getResult()), search_term);
                            final List<LiteMovie> movies = new ArrayList<>();

                            if (!ids.isEmpty()) {
                                /* Step 3
                                 * Now let's search for those ids.
                                 */
                                mDb.collection(MOVIES_LITE_COLLECTION)
                                        .whereIn(FieldPath.documentId(), ids)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && (task.getResult() != null)) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        try {
                                                            String title = document.getString(MOVIE_TITLE);
                                                            String imageUrl = document.getString(MOVIE_IMAGE_URL);
                                                            String pgRatingStr = document.getString(MOVIE_PG_RATING);
                                                            List<String> genres = (ArrayList<String>) document.get(MOVIE_GENRES);
                                                            int releaseYear = Objects.requireNonNull(document.getLong(MOVIE_RELEASE_YEAR)).intValue();
                                                            int duration = Objects.requireNonNull(document.getLong(MOVIE_DURATION)).intValue();
                                                            int imdbRating = (int)((Objects.requireNonNull(document.getDouble(MOVIE_IMDB_RATING))) * 10);

                                                            LiteMovie.PG_RATING pgRating = LiteMovie.PG_RATING.NOT_RATED;
                                                            if (pgRatingStr != null) {
                                                                if (pgRatingStr.equalsIgnoreCase("G")) {
                                                                    pgRating = LiteMovie.PG_RATING.G;
                                                                } else if (pgRatingStr.equalsIgnoreCase("PG")) {
                                                                    pgRating = LiteMovie.PG_RATING.PG;
                                                                } else if (pgRatingStr.equalsIgnoreCase("PG-13")) {
                                                                    pgRating = LiteMovie.PG_RATING.PG_13;
                                                                } else if (pgRatingStr.equalsIgnoreCase("R")) {
                                                                    pgRating = LiteMovie.PG_RATING.R;
                                                                } else if (pgRatingStr.equalsIgnoreCase("NC-17")) {
                                                                    pgRating = LiteMovie.PG_RATING.NC_17;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-Y")) {
                                                                    pgRating = LiteMovie.PG_RATING.TV_Y;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-Y7")) {
                                                                    pgRating = LiteMovie.PG_RATING.TV_Y7;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-G")) {
                                                                    pgRating = LiteMovie.PG_RATING.TV_G;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-PG")) {
                                                                    pgRating = LiteMovie.PG_RATING.TV_PG;
                                                                } else if (pgRatingStr.equalsIgnoreCase("TV-14")) {
                                                                    pgRating = LiteMovie.PG_RATING.TV_14;
                                                                } else if (pgRatingStr.equalsIgnoreCase("PG-MA")) {
                                                                    pgRating = LiteMovie.PG_RATING.TV_MA;
                                                                }
                                                            }

                                                            movies.add(new LiteMovie.Builder()
                                                                    .withId(document.getId())
                                                                    .titled(title)
                                                                    .withImage(imageUrl)
                                                                    .releasedIn(releaseYear)
                                                                    .lasts(duration)
                                                                    .categorizedAs(genres)
                                                                    .classifiedAs(pgRating)
                                                                    .rated(imdbRating)
                                                                    .build());

                                                        } catch (Exception e) {
                                                            Log.e(TAG, "[FirestoreConnector]::getMoviesByTitle: error parsing movies.", e);
                                                        }
                                                    }

                                                    /* Step 4
                                                     * Notify the listener.
                                                     */
                                                    listener.onSuccess(FirestoreItem.Type.MOVIE, movies);

                                                } else {
                                                    Log.w(TAG, "[FirestoreConnector]::getMoviesByTitle: error getting movies.", task.getException());
                                                    listener.onError(FirestoreItem.Type.MOVIE);
                                                }
                                            }
                                        });
                            } else {
                                listener.onSuccess(FirestoreItem.Type.MOVIE, movies);
                            }

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getMoviesByTitle: error searching movies.", task.getException());
                            listener.onError(FirestoreItem.Type.MOVIE);
                        }
                    }
                });
    }

    /**
     *
     * @param id
     * @param listener
     */
    public void getMovieById(@NonNull final String id, @NonNull final FirestoreListener<FirestoreItem> listener) {
        Log.d(TAG, "[FirestoreConnector]::getMovieById: request to get movie by id: " + id);

        /* Step 1
         * See if the same movie has been retrieved in the same session.
         */
        Object cachedResult = mVolatileCache.get(id);
        if (cachedResult != null) {
            Log.d(TAG, "[FirestoreConnector]::getMovieById: volatile cache hit");
            listener.onSuccess(FirestoreItem.Type.MOVIE, (Movie) cachedResult);
            return;
        }

        mDb.collection(MOVIES_COLLECTION)
                .whereEqualTo(FieldPath.documentId(), id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);

                                try {
                                    String title = document.getString(MOVIE_TITLE);
                                    String imageUrl = document.getString(MOVIE_IMAGE_URL);
                                    String overview = document.getString(MOVIE_OVERVIEW);
                                    String pgRatingStr = document.getString(MOVIE_PG_RATING);
                                    List<String> genres = (ArrayList<String>) document.get(MOVIE_GENRES);
                                    int releaseYear = Objects.requireNonNull(document.getLong(MOVIE_RELEASE_YEAR)).intValue();
                                    int duration = Objects.requireNonNull(document.getLong(MOVIE_DURATION)).intValue();
                                    int imdbRating = (int)((Objects.requireNonNull(document.getDouble(MOVIE_IMDB_RATING))) * 10);

                                    LiteMovie.PG_RATING pgRating = LiteMovie.PG_RATING.NOT_RATED;
                                    if (pgRatingStr != null) {
                                        if (pgRatingStr.equalsIgnoreCase("G")) {
                                            pgRating = LiteMovie.PG_RATING.G;
                                        } else if (pgRatingStr.equalsIgnoreCase("PG")) {
                                            pgRating = LiteMovie.PG_RATING.PG;
                                        } else if (pgRatingStr.equalsIgnoreCase("PG-13")) {
                                            pgRating = LiteMovie.PG_RATING.PG_13;
                                        } else if (pgRatingStr.equalsIgnoreCase("R")) {
                                            pgRating = LiteMovie.PG_RATING.R;
                                        } else if (pgRatingStr.equalsIgnoreCase("NC-17")) {
                                            pgRating = LiteMovie.PG_RATING.NC_17;
                                        } else if (pgRatingStr.equalsIgnoreCase("TV-Y")) {
                                            pgRating = LiteMovie.PG_RATING.TV_Y;
                                        } else if (pgRatingStr.equalsIgnoreCase("TV-Y7")) {
                                            pgRating = LiteMovie.PG_RATING.TV_Y7;
                                        } else if (pgRatingStr.equalsIgnoreCase("TV-G")) {
                                            pgRating = LiteMovie.PG_RATING.TV_G;
                                        } else if (pgRatingStr.equalsIgnoreCase("TV-PG")) {
                                            pgRating = LiteMovie.PG_RATING.TV_PG;
                                        } else if (pgRatingStr.equalsIgnoreCase("TV-14")) {
                                            pgRating = LiteMovie.PG_RATING.TV_14;
                                        } else if (pgRatingStr.equalsIgnoreCase("PG-MA")) {
                                            pgRating = LiteMovie.PG_RATING.TV_MA;
                                        }
                                    }

                                    Movie movie = new Movie.Builder()
                                            .withId(document.getId())
                                            .titled(title)
                                            .withImage(imageUrl)
                                            .releasedIn(releaseYear)
                                            .lasts(duration)
                                            .categorizedAs(genres)
                                            .classifiedAs(pgRating)
                                            .rated(imdbRating)
                                            .withOverview(overview)
                                            .build();

                                    listener.onSuccess(FirestoreItem.Type.MOVIE, movie);

                                } catch (Exception e) {
                                    Log.e(TAG, "[FirestoreConnector]::getMoviesByTitle: error parsing movies.", e);
                                    listener.onError(FirestoreItem.Type.MOVIE);
                                }

                            } else {
                                Log.w(TAG, "[FirestoreConnector]::getMovieById: no movie found with the id " + id);
                                listener.onError(FirestoreItem.Type.MOVIE);
                            }

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getMovieById: error getting movie.", task.getException());
                            listener.onError(FirestoreItem.Type.MOVIE);
                        }
                    }
                });
    }

    /**
     *
     * @param idMovie
     * @param idUser
     * @param score
     * @param comment
     * @param listener
     */
    public void createReview(@NonNull final String idMovie, @NonNull final String idUser, int score, @Nullable final String comment, @NonNull final FirestoreListener<FirestoreItem> listener) {
        Log.d(TAG, "[FirestoreConnector]::createReview: request to create review");

        mDb.collection(REVIEWS_COLLECTION)
                .document()
                .set(new Review(score, idMovie, idUser, comment).asMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess(FirestoreItem.Type.REVIEW);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "[FirestoreConnector]::createReview: error creating review", e);
                        listener.onError(FirestoreItem.Type.REVIEW);
                    }
                });
    }

    /**
     *
     * @param user
     * @param listener
     */
    public void addUser(@NonNull User user, @NonNull final FirestoreListener<User> listener) {
        Log.d(TAG, "[FirestoreConnector]::addUser: request to add user");

        mDb.collection(USERS_COLLECTION)
                .document(user.getId())
                .set(user.asMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess(FirestoreItem.Type.USER);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "[FirestoreConnector]::addUser: error creating review", e);
                        listener.onError(FirestoreItem.Type.USER);
                    }
                });
    }

    private synchronized void setLastSearch(String search) {
        mLastSearch = search;
    }

    private synchronized void clearLastSearch() {
        mLastSearch = "";
    }

    private synchronized boolean isLastSearch(String search) {
        return mLastSearch.equals(search);
    }

    private List<String> filterIds(QuerySnapshot task, String search_term) {
        List<String> ids = new ArrayList<>();
        for (QueryDocumentSnapshot document : task) {
            // Firestore compares strings lexicographically, and that's not exactly what we want, so
            // let's filter the movies retrieved.
            if (ids.size() < MAX_SUGGESTIONS) {
                if (document.getString(MOVIE_TITLE).startsWith(search_term)) {
                    ids.add(document.getId());
                }

            } else {
                break;
            }
        }

        for (QueryDocumentSnapshot document : task) {
            if (ids.size() < MAX_SUGGESTIONS) {
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
