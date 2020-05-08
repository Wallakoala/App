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
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class FirestoreConnector {

    // Collections names
    private static final String MOVIES_SEARCH_COLLECTION = "movies_search";
    private static final String MOVIES_SUGGESTIONS_COLLECTION = "movies_suggestions";

    // Document fields names
    private static final String MOVIE_TITLE = "2";
    private static final String MOVIES_RELEASE_YEAR = "3";
    private static final String MOVIE_IMAGE_URL = "8";
    private static final String MOVIES_GENRES = "9";

    private static FirestoreConnector sFirestoreConnector;

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore mDb;

    private FirestoreConnector() {
        mDb = FirebaseFirestore.getInstance();
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
                "La La Land"
                , context.getString(R.string.reviews_item_movie_overview)
                , 2016
                , 128
                , "https://m.media-amazon.com/images/M/MV5BMzUzNDM2NzM2MV5BMl5BanBnXkFtZTgwNTM3NTg4OTE@._V1_SX300.jpg"
                , Arrays.asList("Comedia", "Romance"));

        movies.add(movie);

        movie = new Movie(
                "Capitán América: El primer vengador"
                , "Nacido durante la Gran Depresión, Steve Rogers creció como un chico enclenque en una familia pobre. Horrorizado por las noticias que llegaban de Europa sobre los nazis, decidió enrolarse en el ejército; sin embargo, debido a su precaria salud, fue rechazado una y otra vez. Enternecido por sus súplicas, el General Chester Phillips le ofrece la oportunidad de tomar parte en un experimento especial. la \\\"Operación Renacimiento\\\". Después de admi"
                , 2014
                , 124
                , "https://m.media-amazon.com/images/M/MV5BMTYzOTc2NzU3N15BMl5BanBnXkFtZTcwNjY3MDE3NQ@@._V1_SX300.jpg"
                , Arrays.asList("Acción", "Aventura"));

        movies.add(movie);

        movie = new Movie(
                "Django desencadenado"
                , "Dos años antes de estallar la Guerra Civil (1861-1865), Schultz, un cazarrecompensas alemán que le sigue la pista a unos asesinos, le promete al esclavo Django dejarlo en libertad si le ayuda a atraparlos. Terminado con éxito el trabajo, Django prefiere seguir al lado del alemán y ayudarle a capturar a los delincuentes más buscados del Sur. Se convierte así en un experto cazador de recompensas, pero su único objetivo es rescatar a su esposa Broomhilda, a la que perdió por culpa del tráfico de esclavos. La búsqueda llevará a Django y a Schultz hasta Calvin Candie, el malvado propietario"
                , 2012
                , 165
                , "https://m.media-amazon.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX300.jpg"
                , Arrays.asList("Drama", "Western"));

        movies.add(movie);

        return movies;
    }

    public void getMoviesSuggestionsByTitle(final String search, final FirestoreListener listener) {
        String[] searchTerms = search.split(" ");

        // If only one word has been typed...
        // \todo some mechanism has to be implemented to prevent calling too many times at the same time (countdownlatch?)
        if (searchTerms.length == 1) {
            mDb.collection(MOVIES_SEARCH_COLLECTION)
                    .whereGreaterThanOrEqualTo(MOVIE_TITLE, search)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final List<String> ids = new ArrayList<>();
                                final List<Movie> movies = new ArrayList<>();
                                // Everything went well, let's get the ids of all the documents
                                if (task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Firestore compares strings lexicographically, and that's not exactly what we want, so
                                        // let's filter the movies retrieved.
                                        // \todo lowercased
                                        if (Objects.requireNonNull(document.getString(MOVIE_TITLE)).startsWith(search) && (ids.size() < 10)) {
                                            ids.add(document.getId());
                                        }
                                    }
                                }

                                if (!ids.isEmpty()) {
                                    // Now let's search for those ids
                                    mDb.collection(MOVIES_SUGGESTIONS_COLLECTION)
                                            .whereIn(FieldPath.documentId(), ids)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String title = document.getString(MOVIE_TITLE);
                                                                int year = Objects.requireNonNull(document.getLong(MOVIES_RELEASE_YEAR)).intValue();
                                                                String imageUrl = document.getString(MOVIE_IMAGE_URL);
                                                                List<String> genres = (ArrayList<String>) document.get(MOVIES_GENRES);

                                                                movies.add(new Movie.Builder()
                                                                        .titled(title)
                                                                        .releasedIn(year)
                                                                        .withImage(imageUrl)
                                                                        .categorizedAs(genres)
                                                                        .build());
                                                            }
                                                        }

                                                        listener.onSuccess(movies);

                                                    } else {
                                                        Log.w(Constants.TAG, "Error getting movies suggestions.", task.getException());
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
    }
}
