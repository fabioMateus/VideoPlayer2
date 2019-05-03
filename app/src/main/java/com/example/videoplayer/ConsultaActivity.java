package com.example.videoplayer;
import android.content.Intent;
import android.graphics.Movie;
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
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsultaActivity extends AppCompatActivity {
    //JsonFile To Test
    //String JsontoTest;

     String[] MoviesList;
     String Selected;
    /**To the scrollbar*/
    ArrayList <Integer> IMAGES = new ArrayList <Integer>();
    ArrayList<String> NAMES =new ArrayList <String>();
    ArrayList<String> YEARS = new ArrayList <String>();
    ArrayList<String> DURATIONS = new ArrayList <String>();
    ArrayList<String> CATEGORIES = new ArrayList <String>();
    /**To populate details*/
    String title;
    String year;
    String duration;
    String genre;
    String rating;
    String Plot;
    private RequestQueue queue;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        MoviesList= getIntent().getStringArrayExtra("MoviesList");
        Selected=getIntent().getStringExtra("Selected");
        Log.d("ssd",MoviesList+"");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        populateData(Selected);
        populateRecomemnded(MoviesList,Selected);
        //Vai ser preciso fazer aqui a tal verificação se foi selecionado ou nao
        queue = Volley.newRequestQueue(this); //para a api
        ListView listView=(ListView)findViewById(R.id.moviesListView);
        ConsultaActivity.CustomAdapter customAdapter = new ConsultaActivity.CustomAdapter();
        listView.setAdapter(customAdapter);

    }
    /** Called when the user taps the Play trailer button */
    public void PlayTrailer(View view) {

        StringRequest stringRequest = searchNameStringRequest(title,year);

        queue.add(stringRequest);
    }
    /**Used to parse the json and after parse populate the form with info from json*/
    public void populateData(String jsonToParse){

        try {
            JSONObject result = new JSONObject(jsonToParse);
            //Log.d("aa",result.toString())
            title = result.getString("Title");
            year = result.getString("Year");
            duration = result.getString("Runtime");
            genre = result.getString("Genre");
            rating = result.getString("imdbRating");
            Plot = result.getString("Plot");


        } catch (JSONException e) {
            throw new RuntimeException(e);
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
        imageView_MainMoview.setImageResource(R.drawable.movie5);


    }
    /**Open activity  player*/
    public void playMovie(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }
    /**Open youtube video*/
    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        appIntent.putExtra("force_fullscreen",true);

        context.startActivity(appIntent);
    }
    /**API youtube String builder, Request API youtube for Trailer ID, play Trailer */
    public StringRequest searchNameStringRequest(String name, String year) {
        final String LINK = "https://www.googleapis.com/youtube/v3/search/?key=AIzaSyCodSEk_8xSPlnZ1_Vfc3RWZjpaCPgrBqg&part=snippet";
        final String NAME_SEARCH = "&q=";
        String url = LINK + NAME_SEARCH + "trailer"+name + year+"&max_results=1";
        Log.i("myTag", url);
        return new StringRequest( Request.Method.GET, url, new Response.Listener<String>() {
                   @Override
                    public void onResponse(String response) {
                                              try {
                            JSONObject result = new JSONObject(response);
                            JSONArray resultList =result.getJSONArray("items");
                            JSONObject idlist = resultList.getJSONObject(0);
                            String Listid= idlist.getString("id");
                            JSONObject LastID = new JSONObject(Listid);
                            String VideoId= LastID.getString("videoId");
                            /**Play trailer*/
                            watchYoutubeVideo(ConsultaActivity.this,VideoId);

                        } catch (JSONException e) {
                            Toast.makeText(ConsultaActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ConsultaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });}
    /** to populate the recommended for you */
    public void populateRecomemnded(String[] moviesList,String selected) {
        List<String>  selectedGenreList;
        try {
            JSONObject result = new JSONObject(selected);
            String genreRecomemended = result.getString("Genre");
            selectedGenreList = Arrays.asList(genreRecomemended.split(","));
            Log.d("teste",selectedGenreList.toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //Liat of movies
        for (String movies : moviesList) {
            try {
                JSONObject result = new JSONObject(movies);
                //Log.d("aa",result.toString())

                String genreRecomemended = result.getString("Genre");
                String titleRecomemended = result.getString("Title");
                String yearRecomemended = result.getString("Year");
                String durationRecomemended = result.getString("Runtime");
                boolean findGenre;
                findGenre=false;
                //List of genre selected
                for(String genre :selectedGenreList )
                {
                    if ( genreRecomemended.toLowerCase().indexOf(genre.toLowerCase()) != -1 ) {
                        findGenre = true;
                    }
                }
                if(findGenre==true)
                {
                    //IMAGEM
                    IMAGES.add(R.drawable.movie1);
                    NAMES.add(titleRecomemended);
                    YEARS.add(yearRecomemended);
                    DURATIONS.add(durationRecomemended);
                    CATEGORIES.add(genreRecomemended);
                }







            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**For creating the recomendations list*/
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

            imageView.setImageResource(IMAGES.get(position));
            textViewName.setText(NAMES.get(position));
            textViewYear.setText(YEARS.get(position));
            textViewCategorie.setText(CATEGORIES.get(position));
            textViewDuration.setText(DURATIONS.get(position) + "min");

            return convertView;
        }
    }


}



