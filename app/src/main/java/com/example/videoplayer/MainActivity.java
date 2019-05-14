package com.example.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String[] MOVIESNAME;
    ArrayList<String> MOVIES = new ArrayList<String>();
    ArrayList<String> moviesToSend;

    ArrayList<String> IMAGES = new ArrayList<String>();
    ArrayList<String> NAMES = new ArrayList<String>();
    ArrayList<String> YEARS = new ArrayList<String>();
    ArrayList<String> DURATIONS = new ArrayList<String>();
    ArrayList<String> CATEGORIES = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAllMoviesNames(this);

        for (String movie: MOVIESNAME) {
            getMovieInfo(movie);
        }

        setContentView(R.layout.activity_main);
        ListView listView=(ListView)findViewById(R.id.moviesListView);






        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Seu codigo aqui
                Log.d("CLICK ONNN", "onItemClick: " + id);
                previewMovie(view, position);
            }
        });
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
                           MOVIES.add(myResponse);
                           addMovieInfoToLists(myResponse);
                        }
                    });
                }
            }
        });
    }


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

        String selected = MOVIES.get(position);

        moviesToSend = new ArrayList<>(MOVIES);
        moviesToSend.remove(selected);

        Intent intent = new Intent(this, ConsultaActivity.class);
        intent.putStringArrayListExtra("MoviesList", moviesToSend);
        intent.putExtra("Selected", selected);
        startActivity(intent);
    }



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

            Picasso.get().load(IMAGES.get(position)).into(imageView);
            textViewName.setText(NAMES.get(position));
            textViewYear.setText(YEARS.get(position));
            textViewCategorie.setText(CATEGORIES.get(position));
            textViewDuration.setText(DURATIONS.get(position));

            return convertView;
        }
    }

}
