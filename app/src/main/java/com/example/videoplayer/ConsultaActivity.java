package com.example.videoplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.gesture.GestureOverlayView.OnGesturePerformedListener;

public class ConsultaActivity extends AppCompatActivity implements OnGesturePerformedListener {
    /**
     * Arrays of movies
     */
    ArrayList<String> MoviesList = new ArrayList<>();
    String Selected;
    /**
     * To the scrollbar
     */
    ArrayList<String> IMAGES;
    ArrayList<String> NAMES;
    ArrayList<String> YEARS;
    ArrayList<String> DURATIONS;
    ArrayList<String> CATEGORIES;
    /**
     * To populate details
     */
    String title;
    String year;
    String duration;
    String genre;
    String rating;
    String Plot;
    String Poster;
    /**
     * To the API
     */
    private RequestQueue queue;
    /**
     * To Gestures
     */
    private GestureLibrary gestureLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**Get the data from  MainActivity*/
        MoviesList = getIntent().getExtras().getStringArrayList("MoviesList");
        Selected = getIntent().getStringExtra("Selected");
        /**Force Orientation Portrait*/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /**Populate data*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        populateData(Selected);
        populateRecommended(MoviesList, Selected);
        /**for the api of youtube*/
        queue = Volley.newRequestQueue(this);
        /**For the Recommend list and click in one item from the list*/
        final ListView listView = findViewById(R.id.moviesListView);
        final ConsultaActivity.CustomAdapter customAdapter = new ConsultaActivity.CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.MovieNameTextView);
                String MovieSeleted = (textView.getText() + "");
                for (String movies : MoviesList) {
                    try {
                        JSONObject result = new JSONObject(movies);
                        String titleRecomemended = result.getString("Title");
                        if (titleRecomemended.equals(MovieSeleted)) {
                            String tempSelected = Selected;
                            Selected = movies;
                            MoviesList.remove(Selected);
                            MoviesList.add(tempSelected);
                            populateData(Selected);
                            populateRecommended(MoviesList, Selected);
                            customAdapter.notifyDataSetChanged();
                            break;
                        }

                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }
            }
        });
        /**For the gestures*/
        GestureOverlayView gestureOverlayView = findViewById(R.id.gestures);
        /**hide the gesture (change if need)*/
        gestureOverlayView.setGestureColor(Color.TRANSPARENT);
        gestureOverlayView.setUncertainGestureColor(Color.TRANSPARENT);
        /***/
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestureconsulta);
        if (!gestureLib.load()) {
            finish();
        }
    }

    /**
     * Called when the user taps the Play trailer button or gesture T
     */
    public void PlayTrailer(View view) {
        StringRequest stringRequest = searchNameStringRequest(title, year);
        queue.add(stringRequest);
    }

    /**
     * Called when the user taps the Play movie button or gesture M
     */
    public void playMovie(View view) {
        Log.d("PLAY_FILE_NAME",parseNameYear(title, year));
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("movie", parseNameYear(title, year));
        startActivity(intent);
    }

    /**
     * Used to parse the json and after parse populate the form with info from json
     */
    public void populateData(String jsonToParse) {
        try {
            JSONObject result = new JSONObject(jsonToParse);
            //Log.d("aa",result.toString())
            title = result.getString("Title");
            year = result.getString("Year");
            duration = result.getString("Runtime");
            genre = result.getString("Genre");
            rating = result.getString("imdbRating");
            Plot = result.getString("Plot");
            Poster = result.getString("Poster");

        } catch (JSONException e) {
            Log.e("ERROR", e.getMessage());
        }
        //** Populate Form*/
        TextView textview_title = findViewById(R.id.textView_movieName);
        textview_title.setText(title);
        TextView textview_year = findViewById(R.id.textView_MovieYear);
        textview_year.setText(year);
        TextView textview_duration = findViewById(R.id.textView_MovieDuration);
        textview_duration.setText(duration);
        TextView textview_genre = (findViewById(R.id.textView_MovieType));
        textview_genre.setText(genre);
        TextView textview_rating = (findViewById(R.id.textView_MovieRating));
        textview_rating.setText(rating);
        TextView textview_plot = (findViewById(R.id.textView_MoviePlot));
        textview_plot.setText(Plot);
        //IMAGEM
        ImageView imageView_MainMoview = (findViewById(R.id.imageView_MainMovie));
        Picasso.get().load(Poster).into(imageView_MainMoview);
    }

    /**
     * Open youtube video
     */
    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        appIntent.putExtra("force_fullscreen", true);
        context.startActivity(appIntent);
    }

    /**
     * API youtube String builder, Request API youtube for Trailer ID, play Trailer
     */
    public StringRequest searchNameStringRequest(String name, String year) {
        final String LINK = "https://www.googleapis.com/youtube/v3/search/?key=AIzaSyCodSEk_8xSPlnZ1_Vfc3RWZjpaCPgrBqg&part=snippet";
        final String NAME_SEARCH = "&q=";
        String url = LINK + NAME_SEARCH + "trailer" + name + year + "&max_results=1";
        //Log.i("myTag", url);
        return new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    JSONArray resultList = result.getJSONArray("items");
                    JSONObject idlist = resultList.getJSONObject(0);
                    String Listid = idlist.getString("id");
                    JSONObject LastID = new JSONObject(Listid);
                    String VideoId = LastID.getString("videoId");
                    /**Play trailer*/
                    watchYoutubeVideo(ConsultaActivity.this, VideoId);

                } catch (JSONException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ConsultaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * to populate the recommended for you
     */
    public void populateRecommended(ArrayList<String> moviesList, String selected) {
        IMAGES = new ArrayList<String>();
        NAMES = new ArrayList<String>();
        YEARS = new ArrayList<String>();
        DURATIONS = new ArrayList<String>();
        CATEGORIES = new ArrayList<String>();
        List<String> selectedGenreList;
        try {
            JSONObject result = new JSONObject(selected);
            String genreRecomemended = result.getString("Genre");
            selectedGenreList = Arrays.asList(genreRecomemended.split(","));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //List of movies
        for (String movies : moviesList) {
            try {
                JSONObject result = new JSONObject(movies);
                String genreRecomemended = result.getString("Genre");
                String titleRecomemended = result.getString("Title");
                String yearRecomemended = result.getString("Year");
                String durationRecomemended = result.getString("Runtime");
                String imageRecomemended = result.getString("Poster");
                boolean findGenre1, findGenre2;
                findGenre1 = false;
                findGenre2 = false;
                //List of genre selected
                for (String genre : selectedGenreList) {
                    if (genreRecomemended.toLowerCase().contains(genre.toLowerCase())) {
                        if (findGenre1 == true) {
                            findGenre2 = true;
                        }
                        findGenre1 = true;
                    }
                }

                if (findGenre1 && findGenre2) {
                    //IMAGEM
                    IMAGES.add(imageRecomemended);
                    NAMES.add(titleRecomemended);
                    YEARS.add(yearRecomemended);
                    DURATIONS.add(durationRecomemended);
                    CATEGORIES.add(genreRecomemended);
                }
            } catch (JSONException e) {
                Log.e("ERROR", e.getMessage());
            }
        }
    }

    /**
     * Parse string in correct format
     */
    public String parseNameYear(String movieName, String movieYear) {
        //Remove spaces
        movieYear = movieYear.replaceAll("\\s+", "");
        //remove accents
        movieName = Normalizer.normalize(movieName, Normalizer.Form.NFD);
        movieName = movieName.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        //to lowercase
        movieYear = movieYear.toLowerCase();
        return movieName + movieYear;
    }

    /**
     * For the gestures
     */
    @Override
    public void onGesturePerformed(GestureOverlayView erlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
                if (prediction.name.equals("movie")) {
                    Button button_playMovie = findViewById(R.id.button_playMovie);
                    button_playMovie.performClick();
                    break;
                } else if (prediction.name.equals("trailer")) {

                    Button button_playTrailer = findViewById(R.id.button_playTrailer);
                    button_playTrailer.performClick();
                    break;
                }
            }
        }
    }

    /**
     * For the recommend list
     */
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
            return 0;
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
            textViewName.setTextSize(16);
            textViewYear.setText(YEARS.get(position));
            textViewCategorie.setText(CATEGORIES.get(position));
            textViewDuration.setText(DURATIONS.get(position));
            return convertView;
        }
    }

}