package com.movielix.firestore;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.movielix.R;
import com.movielix.bean.BaseMovie;
import com.movielix.bean.LiteMovie;
import com.movielix.bean.Movie;
import com.movielix.bean.Review;
import com.movielix.bean.User;

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
    private static final String FRIENDS_COLLECTION = "friends";

    // Document fields names
    private static final String MOVIE_TITLE = "2";
    private static final String MOVIE_RELEASE_YEAR = "3";
    private static final String MOVIE_OVERVIEW = "4";
    private static final String MOVIE_DURATION = "5";
    private static final String MOVIE_IMDB_RATING = "6";
    private static final String MOVIE_IMAGE_URL = "8";
    private static final String MOVIE_GENRES = "9";
    private static final String MOVIE_PG_RATING = "10";

    private static final String REVIEW_MOVIE_ID = "movie";
    private static final String REVIEW_USER_ID = "user";
    private static final String REVIEW_SCORE = "score";
    private static final String REVIEW_COMMENT = "comment";

    private static final String FRIENDS_ID = "id";
    private static final String FRIENDS_FRIEND_OF = "friend_of";

    private static final String USER_NAME = "name";
    private static final String USER_PHOTO_URL = "photo_url";

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

    public List<Review> getDummyMovies(Context context) {
        List<Review> reviews = new ArrayList<>();
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

        reviews.add(new Review(4, "0", "0", "Super divertida, y gonica", movie));

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

        reviews.add(new Review(2, "1", "0", "Americanada...", movie));

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

        reviews.add(new Review(5, "2", "0", "Canela en rama", movie));

        return reviews;
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
            listener.onSuccess((List<BaseMovie>) cachedResult);
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
                                                            Log.d(TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: last request, notifying the listener");

                                                            clearLastSearch();
                                                            movies.addAll(persistentSuggestions);
                                                            listener.onSuccess(movies);

                                                        } else {
                                                            Log.d(TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: not the last request, discarding results");
                                                        }

                                                    } else {
                                                        Log.w(TAG, "[FirestoreConnector]::getMoviesSuggestionsByTitle: error getting movies suggestions.", task.getException());

                                                        if (isLastSearch(search_term)) {
                                                            clearLastSearch();
                                                            listener.onError();
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
                                        listener.onSuccess(persistentSuggestions);

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
                                    listener.onSuccess(movies);

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
                                listener.onError();
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
    public void getMoviesByTitle(final String search, final FirestoreListener<LiteMovie> listener) {
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
                                                        LiteMovie lm = getLiteMovieFromDocument(document);
                                                        if (lm != null) {
                                                            movies.add(lm);
                                                        }
                                                    }

                                                    /* Step 4
                                                     * Notify the listener.
                                                     */
                                                    listener.onSuccess(movies);

                                                } else {
                                                    Log.w(TAG, "[FirestoreConnector]::getMoviesByTitle: error getting movies.", task.getException());
                                                    listener.onError();
                                                }
                                            }
                                        });
                            } else {
                                listener.onSuccess(movies);
                            }

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getMoviesByTitle: error searching movies.", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    /**
     * Method that returns a movie given the ID.
     *
     * @param id movie id.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void getMovieById(@NonNull final String id, @NonNull final FirestoreListener<Movie> listener) {
        Log.d(TAG, "[FirestoreConnector]::getMovieById: request to get movie by id: " + id);

        /* Step 1
         * See if the same movie has been retrieved in the same session.
         */
        Object cachedResult = mVolatileCache.get(id);
        if (cachedResult != null) {
            Log.d(TAG, "[FirestoreConnector]::getMovieById: volatile cache hit");
            listener.onSuccess((Movie) cachedResult);
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
                                listener.onSuccess(getMovieFromDocument(document));

                            } else {
                                Log.w(TAG, "[FirestoreConnector]::getMovieById: no movie found with the id " + id);
                                listener.onError();
                            }

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getMovieById: error getting movie.", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    /**
     * Method that returns a list of movies given their IDs.
     *
     * @param ids list of the movies IDs.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void getMoviesById(final List<String> ids, @NonNull final FirestoreListener<Movie> listener) {
        Log.d(TAG, "[FirestoreConnector]::getMoviesById: request to get movies by ids");

        mDb.collection(MOVIES_COLLECTION)
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                List<Movie> movies = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Movie lm = getMovieFromDocument(document);
                                    if (lm != null) {
                                        movies.add(lm);
                                    }
                                }

                                listener.onSuccess(movies);

                            } else {
                                Log.w(TAG, "[FirestoreConnector]::getMoviesById: no movies found with the given ids");
                                listener.onError();
                            }

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getMoviesById: error getting movies.", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    /**
     * Method that creates a review from a user.
     *
     * @param idMovie movie id.
     * @param idUser user id.
     * @param score movie's score.
     * @param comment optional user's comment.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void createReview(
            @NonNull final String idMovie,
            @NonNull final String idUser,
            int score,
            @Nullable final String comment,
            @NonNull final FirestoreListener<Review> listener)
    {
        Log.d(TAG, "[FirestoreConnector]::createReview: request to create review");

        mDb.collection(REVIEWS_COLLECTION)
                .document()
                .set(new Review(score, idMovie, idUser, comment, null).asMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "[FirestoreConnector]::createReview: review created successfully");
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "[FirestoreConnector]::createReview: error creating review", e);
                        listener.onError();
                    }
                });
    }

    /**
     * Method that adds a new user.
     *
     * @param user user object.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void addUser(@NonNull User user, @NonNull final FirestoreListener<User> listener) {
        Log.d(TAG, "[FirestoreConnector]::addUser: request to add user");

        mDb.collection(USERS_COLLECTION)
                .document(user.getId())
                .set(user.asMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "[FirestoreConnector]::addUser: user added successfully");
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "[FirestoreConnector]::addUser: error creating review", e);
                        listener.onError();
                    }
                });
    }

    /**
     * Method that returns a list of reviews made by the given user.
     *
     * @param userId user's id.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void getReviewsByUser(@NonNull final String userId, final FirestoreListener<Review> listener) {
        Log.d(TAG, "[FirestoreConnector]::getReviewsByUser: request to get review by user: " + userId);

        mDb.collection(REVIEWS_COLLECTION)
                .whereEqualTo(REVIEW_USER_ID, userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            List<Review> reviews = new ArrayList<>();
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    Review review = new Review(
                                            document.getId(),
                                            Objects.requireNonNull(document.getLong(REVIEW_SCORE)).intValue(),
                                            Objects.requireNonNull(document.getString(REVIEW_MOVIE_ID)),
                                            Objects.requireNonNull(document.getString(REVIEW_USER_ID)),
                                            document.getString(REVIEW_COMMENT),
                                            null);

                                    reviews.add(review);
                                }

                            } else {
                                Log.w(TAG, "[FirestoreConnector]::getReviewsByUser: no reviews found by user " + userId);
                            }

                            listener.onSuccess(reviews);

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getReviewsByUser: error getting reviews.", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    /**
     * Method that returns the list of friends of a given user.
     *
     * @param userId user's id.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void getFriends(@NonNull final String userId, final FirestoreListener<User> listener) {
        Log.d(TAG, "[FirestoreConnector]::getFriendsOf: request to get friends of user: " + userId);

        mDb.collection(FRIENDS_COLLECTION)
                .whereEqualTo(FRIENDS_ID, userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            final List<User> users = new ArrayList<>();
                            if (!task.getResult().getDocuments().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    if (document.get(FRIENDS_FRIEND_OF) != null) {
                                        List<String> friends = (List<String>) document.get(FRIENDS_FRIEND_OF);

                                        assert friends != null;
                                        mDb.collection(USERS_COLLECTION)
                                                .whereIn(FieldPath.documentId(), friends)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful() && (task.getResult() != null)) {
                                                            if (!task.getResult().getDocuments().isEmpty()) {
                                                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                                                    Uri uri = null;
                                                                    if (document.getString(USER_PHOTO_URL) != null) {
                                                                        uri = Uri.parse(document.getString(USER_PHOTO_URL));
                                                                    }

                                                                    users.add(new User(
                                                                              document.getId()
                                                                            , Objects.requireNonNull(document.getString(USER_NAME))
                                                                            , uri));
                                                                }

                                                            } else {
                                                                Log.w(TAG, "[FirestoreConnector]::getFriendsOf: no friends found for user " + userId);
                                                            }

                                                            listener.onSuccess(users);

                                                        } else {
                                                            Log.w(TAG, "[FirestoreConnector]::getFriendsOf: error getting friends.", task.getException());
                                                            listener.onError();
                                                        }
                                                    }
                                                });
                                    }
                                }

                            } else {
                                Log.w(TAG, "[FirestoreConnector]::getFriendsOf: no friends found for user " + userId);
                            }

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getFriendsOf: error getting friends.", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    /**
     * Method that returns a list of users suggestions given a search term.
     *
     * @param search search term.
     * @param listener FirestoreListener object to be notified once the operation is complete.
     */
    public void getUsersSuggestionsByName(@NonNull final String search, final FirestoreListener<User> listener) {
        Log.d(TAG, "[FirestoreConnector]::getUsersSuggestionsByName: request to get users by searching: " + search);

        char c = search.charAt(search.length() - 1);
        c++;

        char[] cArray = search.toCharArray();
        cArray[cArray.length - 1] = c;

        final char[] cUpperArray = cArray.clone();
        if (Character.isLowerCase(cUpperArray[0])) {
            cUpperArray[0] = Character.toUpperCase(cUpperArray[0]);
        } else {
            cUpperArray[0] = Character.toLowerCase(cUpperArray[0]);
        }

        final char[] cPrimeArray = search.toCharArray();
        if (Character.isLowerCase(cPrimeArray[0])) {
            cPrimeArray[0] = Character.toUpperCase(cPrimeArray[0]);
        } else {
            cPrimeArray[0] = Character.toLowerCase(cPrimeArray[0]);
        }

        mDb.collection(USERS_COLLECTION)
                .whereGreaterThanOrEqualTo(USER_NAME, search)
                .whereLessThan(USER_NAME, new String(cArray))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            final List<User> users = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                User user = new User(
                                          document.getId()
                                        , Objects.requireNonNull(document.getString(USER_NAME))
                                        , Uri.parse(document.getString(USER_PHOTO_URL)));

                                users.add(user);
                            }

                            mDb.collection(USERS_COLLECTION)
                                    .whereGreaterThanOrEqualTo(USER_NAME, new String(cPrimeArray))
                                    .whereLessThan(USER_NAME, new String(cUpperArray))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && (task.getResult() != null)) {
                                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                                    User user = new User(
                                                            document.getId()
                                                            , Objects.requireNonNull(document.getString(USER_NAME))
                                                            , Uri.parse(document.getString(USER_PHOTO_URL)));

                                                    users.add(user);
                                                }

                                                listener.onSuccess(users);

                                            } else {
                                                Log.w(TAG, "[FirestoreConnector]::getUsersSuggestionsByName: error getting users.", task.getException());
                                                listener.onError();
                                            }
                                        }
                                    });

                            listener.onSuccess(users);

                        } else {
                            Log.w(TAG, "[FirestoreConnector]::getUsersSuggestionsByName: error getting users.", task.getException());
                            listener.onError();
                        }
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
                try {
                    if (document.getString(MOVIE_TITLE).startsWith(search_term)) {
                        ids.add(document.getId());
                    }

                } catch (NullPointerException ignored) {}

            } else {
                break;
            }
        }

        for (QueryDocumentSnapshot document : task) {
            if (ids.size() < MAX_SUGGESTIONS) {
                try {
                    if (document.getString(MOVIE_TITLE).contains(search_term) && !ids.contains(document.getId())) {
                        ids.add(document.getId());
                    }

                } catch (NullPointerException ignored) {}

            } else {
                break;
            }
        }

        return ids;
    }

    @Nullable
    private LiteMovie getLiteMovieFromDocument(QueryDocumentSnapshot document) {
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

            return new LiteMovie.Builder()
                    .withId(document.getId())
                    .titled(title)
                    .withImage(imageUrl)
                    .releasedIn(releaseYear)
                    .lasts(duration)
                    .categorizedAs(genres)
                    .classifiedAs(pgRating)
                    .rated(imdbRating)
                    .build();


        } catch (Exception e) {
            Log.e(TAG, "[FirestoreConnector]::getMoviesByTitle: error parsing movies.", e);
        }

        return null;
    }

    @Nullable
    private Movie getMovieFromDocument(QueryDocumentSnapshot document) {
        try {
            String title = document.getString(MOVIE_TITLE);
            String overview = document.getString(MOVIE_OVERVIEW);
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

            return new Movie.Builder()
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

        } catch (Exception e) {
            Log.e(TAG, "[FirestoreConnector]::getMoviesByTitle: error parsing movies.", e);
        }

        return null;
    }
}
