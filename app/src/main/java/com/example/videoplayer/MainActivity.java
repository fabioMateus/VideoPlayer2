package com.example.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    String[] MOVIESNAME;
    ArrayList<String> MOVIES = new ArrayList<String>();
    ArrayList<String> MOVIESCOMPLETELSIT = new ArrayList<String>();
    ArrayList<String> moviesToSend;

    ArrayList<String> IMAGES = new ArrayList<String>();
    ArrayList<String> NAMES = new ArrayList<String>();
    ArrayList<String> YEARS = new ArrayList<String>();
    ArrayList<String> DURATIONS = new ArrayList<String>();
    ArrayList<String> CATEGORIES = new ArrayList<String>();

    Boolean SORTEDBYTITLE = false;
    Boolean SORTEDBYYEAR = false;
    Boolean SORTEDBYDURATION = false;

    CustomAdapter customAdapter;

    /**
     * To Gestures
     */
    private GestureLibrary gestureLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAllMoviesNames(this);

        for (String movie: MOVIESNAME) {
            getMovieInfo(movie);
        }

        setContentView(R.layout.activity_main);
        ListView listView=(ListView)findViewById(R.id.moviesListView);

        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                previewMovie(view, position);
            }
        });

        final Button orderByTitleButton = findViewById(R.id.orderByTitleButton);
        orderByTitleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderByParameter("title", customAdapter);
            }
        });

        final Button orderByYearButton = findViewById(R.id.orderByYearButton);
        orderByYearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderByParameter("year", customAdapter);
            }
        });

        final Button orderByDurationButton = findViewById(R.id.orderByDurationButton);
        orderByDurationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                orderByParameter("duration", customAdapter);
            }
        });

        /**For the gestures*/
        GestureOverlayView gestureOverlayView = findViewById(R.id.gestures);
        /**hide the gesture (change if need)*/
        gestureOverlayView.setGestureColor(Color.YELLOW);
        gestureOverlayView.setUncertainGestureColor(Color.YELLOW);
        /***/
        gestureOverlayView.addOnGesturePerformedListener((GestureOverlayView.OnGesturePerformedListener) this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gesturefabio);
        if (!gestureLib.load()) {
            finish();
        }
    }

    /**For search option*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item= menu.findItem(R.id.search_title);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByTitle(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void filterByTitle(String filter) {
        //Cria uma lista de jsons para depois poder filtrar pelo titulo
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        for (int i = 0; i < MOVIESCOMPLETELSIT.size(); i++) {
            try {
                JSONObject obj = new JSONObject(MOVIESCOMPLETELSIT.get(i));
                array.add(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Lista de filmes filtrados
        ArrayList<JSONObject> arrayFiltrado = new ArrayList<JSONObject>();

        for (JSONObject filme:array) {

            try {
                if (filme.getString("Title").toLowerCase().contains(filter.toLowerCase())) {
                    Log.d("sadsad", "MATCH");
                    arrayFiltrado.add(filme);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //Cria uma copia dos filmes existentes
        MOVIESCOMPLETELSIT.clear();
        for (int i = 0; i < array.size(); i++) {
            MOVIESCOMPLETELSIT.add(array.get(i).toString());
            addMovieInfoToLists(array.get(i).toString());
        }

        //Limpa todas as listas para as colocar pela ordem correta
        MOVIES.clear();
        NAMES.clear();
        IMAGES.clear();
        YEARS.clear();
        DURATIONS.clear();
        CATEGORIES.clear();

        for (int i = 0; i < arrayFiltrado.size(); i++) {
            MOVIES.add(arrayFiltrado.get(i).toString());
            addMovieInfoToLists(arrayFiltrado.get(i).toString());
        }

        customAdapter.notifyDataSetChanged();
    }

    /**
     * For the gestures
     */
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
                if (prediction.name.equals("title")) {
                    Button orderByTitleButton = findViewById(R.id.orderByTitleButton);
                    orderByTitleButton.performClick();
                    Log.d("sadsadsadsad", "onGesturePerformed: titulo");
                    break;
                } else if (prediction.name.equals("year")) {
                    Button orderByYearButton = findViewById(R.id.orderByYearButton);
                    orderByYearButton.performClick();
                    Log.d("sdadsadsadasdasd", "onGesturePerformed: ano");
                    break;
                } else if (prediction.name.equals("duration")) {
                    Button orderByDurationButton = findViewById(R.id.orderByDurationButton);
                    orderByDurationButton.performClick();
                    Log.d("sdadsadsadasdasd", "onGesturePerformed: duration");
                    break;
                }
            }
        }
    }

    /** Called to load all movies names on the device*/
    public void loadAllMoviesNames (Context context) {
        try {
            MOVIESNAME = context.getAssets().list("movies");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Function that get the movies info from OMBD API using name and year*/
    public void getMovieInfo(String movie) {
        String name = movie.substring(0,movie.length() - 8);
        String year = movie.substring(movie.length() - 8, movie.length() - 4);

        OkHttpClient client = new OkHttpClient();
        String url = "http://www.omdbapi.com/?apikey=254bcab9&t="+name+"&y="+year;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MOVIESCOMPLETELSIT.add(myResponse);
                            MOVIES.add(myResponse);
                           addMovieInfoToLists(myResponse);
                        }
                    });
                }
            }
        });
    }

    /**Adiciona a informação dos filmes às listas de informações*/
    public void addMovieInfoToLists(String Response) {
        try {
            JSONObject movieObject = new JSONObject(Response);
            if (movieObject.getString("Poster").isEmpty()) {
                IMAGES.add("http://i.imgur.com/DvpvklR.png");
            } else {
                IMAGES.add(movieObject.getString("Poster"));
            }

            NAMES.add(movieObject.getString("Title"));
            YEARS.add(movieObject.getString("Year"));
            DURATIONS.add(movieObject.getString("Runtime"));
            CATEGORIES.add(movieObject.getString("Genre"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** Called when the user taps the movie line*/
    public void previewMovie(View view, Integer position) {

        //Obtém o filme que foi escolhido
        String selected = MOVIES.get(position);

        //Duplica a lista com todos os filmes e elimina o que foi selecionado
        moviesToSend = new ArrayList<>(MOVIES);
        moviesToSend.remove(selected);

        //Inicia a Actividade para visualizar a informação do filme
        Intent intent = new Intent(this, ConsultaActivity.class);
        intent.putStringArrayListExtra("MoviesList", moviesToSend);
        intent.putExtra("Selected", selected);
        startActivity(intent);
    }

    /**Função utilizada para ordenar os filmes e atualizar a listagem*/
    public void orderByParameter(String orderBy, CustomAdapter adapter) {
        // Possiveis parametros: title, year, duration

        //Cria uma lista de jsons para depois poder ordenar utilizando o collections acedendo aos campos pretendidos
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        for (int i = 0; i < MOVIES.size(); i++) {
            try {
                JSONObject obj = new JSONObject(MOVIES.get(i));
                array.add(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        switch (orderBy){
            case "title":
                //caso já estejam ordenados pelo titulo, ordena pela ordem contrária
                if (SORTEDBYTITLE) {
                    Collections.reverse(array);
                } else {
                    Collections.sort(array, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject lhs, JSONObject rhs) {
                            try {
                                return (lhs.getString("Title").toLowerCase().compareTo(rhs.getString("Title").toLowerCase()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });
                    SORTEDBYTITLE = true;
                    SORTEDBYYEAR = false;
                    SORTEDBYDURATION = false;
                }
                break;
            case "year":
                //caso já estejam ordenados pelo ano, ordena pela ordem contrária
                if (SORTEDBYYEAR) {
                    Collections.reverse(array);
                } else {
                    Collections.sort(array, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject lhs, JSONObject rhs) {
                            try {
                                return (lhs.getString("Year").toLowerCase().compareTo(rhs.getString("Year").toLowerCase()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });
                    SORTEDBYTITLE = false;
                    SORTEDBYYEAR = true;
                    SORTEDBYDURATION = false;
                }
                break;
            case "duration":
                //caso já estejam ordenados pela duração, ordena pela ordem contrária
                if (SORTEDBYDURATION) {
                    Collections.reverse(array);
                } else {
                    Collections.sort(array, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject lhs, JSONObject rhs) {
                            try {
                                //Para retirar o " min" e converter para inteiro e poder comparar
                                Integer int1 = Integer.parseInt(lhs.getString("Runtime").substring(0, lhs.getString("Runtime").length() - 4));
                                Integer int2 = Integer.parseInt(rhs.getString("Runtime").substring(0, rhs.getString("Runtime").length() - 4));
                                return (int1 - int2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });
                    SORTEDBYTITLE = false;
                    SORTEDBYYEAR = false;
                    SORTEDBYDURATION = true;
                }
                break;
            default:
                break;
        }

        //Limpa todas as listas para as colocar pela ordem correta
        MOVIES.clear();
        NAMES.clear();
        IMAGES.clear();
        YEARS.clear();
        DURATIONS.clear();
        CATEGORIES.clear();

        for (int i = 0; i < array.size(); i++) {
            MOVIES.add(array.get(i).toString());
            addMovieInfoToLists(array.get(i).toString());
        }

        adapter.notifyDataSetChanged();
    }

    public void refreshMoviesList () {

    }

    /**Adapter to the listView*/
    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.elemento_listagem, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.MovieImageView);
            TextView textViewName = (TextView) convertView.findViewById(R.id.MovieNameTextView);
            TextView textViewYear = (TextView) convertView.findViewById(R.id.MovieYearTextView);
            TextView textViewCategorie = (TextView) convertView.findViewById(R.id.MovieCategorieTextView);
            TextView textViewDuration = (TextView) convertView.findViewById(R.id.MovieDurationTextView);

            //Coloca na view os valores de cada filme
            Picasso.get().load(IMAGES.get(position)).into(imageView);
            textViewName.setText(NAMES.get(position));
            textViewYear.setText(YEARS.get(position));
            textViewCategorie.setText(CATEGORIES.get(position));
            textViewDuration.setText(DURATIONS.get(position));

            return convertView;
        }
    }
}
