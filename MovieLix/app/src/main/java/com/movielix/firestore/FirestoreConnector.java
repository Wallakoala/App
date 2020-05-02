package com.movielix.firestore;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.movielix.R;
import com.movielix.bean.Movie;
import com.movielix.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class FirestoreConnector {

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
                , "2h 8m"
                , "https://m.media-amazon.com/images/M/MV5BMzUzNDM2NzM2MV5BMl5BanBnXkFtZTgwNTM3NTg4OTE@._V1_SX300.jpg"
                , new String[] { "Comedia", "Romance" });

        movies.add(movie);

        movie = new Movie(
                "Capitán América: El primer vengador"
                , "Nacido durante la Gran Depresión, Steve Rogers creció como un chico enclenque en una familia pobre. Horrorizado por las noticias que llegaban de Europa sobre los nazis, decidió enrolarse en el ejército; sin embargo, debido a su precaria salud, fue rechazado una y otra vez. Enternecido por sus súplicas, el General Chester Phillips le ofrece la oportunidad de tomar parte en un experimento especial. la \\\"Operación Renacimiento\\\". Después de admi"
                , 2014
                , "2h 4m"
                , "https://m.media-amazon.com/images/M/MV5BMTYzOTc2NzU3N15BMl5BanBnXkFtZTcwNjY3MDE3NQ@@._V1_SX300.jpg"
                , new String[] { "Acción", "Aventura" });

        movies.add(movie);

        movie = new Movie(
                "Django desencadenado"
                , "Dos años antes de estallar la Guerra Civil (1861-1865), Schultz, un cazarrecompensas alemán que le sigue la pista a unos asesinos, le promete al esclavo Django dejarlo en libertad si le ayuda a atraparlos. Terminado con éxito el trabajo, Django prefiere seguir al lado del alemán y ayudarle a capturar a los delincuentes más buscados del Sur. Se convierte así en un experto cazador de recompensas, pero su único objetivo es rescatar a su esposa Broomhilda, a la que perdió por culpa del tráfico de esclavos. La búsqueda llevará a Django y a Schultz hasta Calvin Candie, el malvado propietario"
                , 2012
                , "2h 45m"
                , "https://m.media-amazon.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX300.jpg"
                , new String[] { "Drama", "Western" });

        movies.add(movie);

        return movies;
    }

    public void getMoviesBySearch(final FirestoreMoviesObserver observer, String search) {
        mDb.collection("movies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(Constants.TAG, document.getId() + " => " + document.getData());
                    }

                    observer.onSuccess(null);

                } else {
                    Log.w(Constants.TAG, "Error getting documents.", task.getException());
                    observer.onError();
                }
            }
        });
    }
}
