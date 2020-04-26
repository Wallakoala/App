package com.movielix;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.movielix.adapter.MoviesAdapter;
import com.movielix.bean.Movie;
import com.movielix.font.TypeFace;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movies);
        initSearchView();

        new GetMoviesTask(this).execute();
    }

    private void initSearchView() {
        MaterialSearchBar searchBar = findViewById(R.id.movies_search_bar);

        searchBar.setOnSearchActionListener(this);

        // Set the font
        try {
            ConstraintLayout cl = (ConstraintLayout)((CardView) searchBar.getChildAt(0)).getChildAt(0);
            AppCompatTextView textView = (AppCompatTextView) cl.getChildAt(1);
            AppCompatEditText editText = (AppCompatEditText)((LinearLayout) cl.getChildAt(2)).getChildAt(1);

            Typeface tf = TypeFace.getTypeFace(this, "Raleway-Regular.ttf");
            textView.setTypeface(tf);
            editText.setTypeface(tf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the RecyclerView
     */
    private void initializeRecyclerView(final List<Movie> movies) {
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        MoviesAdapter moviesAdapter = new MoviesAdapter(movies, this);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(moviesAdapter);

        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @SuppressWarnings("StaticFieldLeak")
    private class GetMoviesTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Context> mContext;
        private List<Movie> movies;

        GetMoviesTask(final Context context) {
            super();

            mContext = new WeakReference<>(context);
            movies = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Movie movie = new Movie(
                        "La La Land"
                        , mContext.get().getString(R.string.reviews_item_movie_overview)
                        , 2016
                        , "2h 8m"
                        , new URL("https://m.media-amazon.com/images/M/MV5BMzUzNDM2NzM2MV5BMl5BanBnXkFtZTgwNTM3NTg4OTE@._V1_SX300.jpg")
                        , new String[]{"Comedia", "Romance"});

                movies.add(movie);

                movie = new Movie(
                        "Capitán América: El primer vengador"
                        , "Nacido durante la Gran Depresión, Steve Rogers creció como un chico enclenque en una familia pobre. Horrorizado por las noticias que llegaban de Europa sobre los nazis, decidió enrolarse en el ejército; sin embargo, debido a su precaria salud, fue rechazado una y otra vez. Enternecido por sus súplicas, el General Chester Phillips le ofrece la oportunidad de tomar parte en un experimento especial. la \\\"Operación Renacimiento\\\". Después de admi"
                        , 2014
                        , "2h 4m"
                        , new URL("https://m.media-amazon.com/images/M/MV5BMTYzOTc2NzU3N15BMl5BanBnXkFtZTcwNjY3MDE3NQ@@._V1_SX300.jpg")
                        , new String[]{"Acción", "Aventura"});

                movies.add(movie);

                movie = new Movie(
                        "Django desencadenado"
                        , "Dos años antes de estallar la Guerra Civil (1861-1865), Schultz, un cazarrecompensas alemán que le sigue la pista a unos asesinos, le promete al esclavo Django dejarlo en libertad si le ayuda a atraparlos. Terminado con éxito el trabajo, Django prefiere seguir al lado del alemán y ayudarle a capturar a los delincuentes más buscados del Sur. Se convierte así en un experto cazador de recompensas, pero su único objetivo es rescatar a su esposa Broomhilda, a la que perdió por culpa del tráfico de esclavos. La búsqueda llevará a Django y a Schultz hasta Calvin Candie, el malvado propietario"
                        , 2012
                        , "2h 45m"
                        , new URL("https://m.media-amazon.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX300.jpg")
                        , new String[]{"Drama", "Western"});

                movies.add(movie);

                Thread.sleep(2000);

            } catch (InterruptedException | MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            initializeRecyclerView(movies);
        }
    }
}
