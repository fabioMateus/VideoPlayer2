package com.example.videoplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;




public class ConsultaActivity extends AppCompatActivity {
    String JsontoTest="{\"Title\":\"Aquaman\",\"Year\":\"2018\",\"Rated\":\"PG-13\",\"Released\":\"21 Dec 2018\",\"Runtime\":\"143 min\",\"Genre\":\"Action, Adventure, Fantasy, Sci-Fi\",\"Director\":\"James Wan\",\"Writer\":\"David Leslie Johnson-McGoldrick (screenplay by), Will Beall (screenplay by), Geoff Johns (story by), James Wan (story by), Will Beall (story by), Mort Weisinger (Aquaman created by), Paul Norris (Aquaman created by)\",\"Actors\":\"Jason Momoa, Amber Heard, Willem Dafoe, Patrick Wilson\",\"Plot\":\"Arthur Curry, the human-born heir to the underwater kingdom of Atlantis, goes on a quest to prevent a war between the worlds of ocean and land.\",\"Language\":\"English\",\"Country\":\"Australia, USA\",\"Awards\":\"N/A\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BOTk5ODg0OTU5M15BMl5BanBnXkFtZTgwMDQ3MDY3NjM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.2/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"65%\"},{\"Source\":\"Metacritic\",\"Value\":\"55/100\"}],\"Metascore\":\"55\",\"imdbRating\":\"7.2\",\"imdbVotes\":\"236,813\",\"imdbID\":\"tt1477834\",\"Type\":\"movie\",\"DVD\":\"N/A\",\"BoxOffice\":\"N/A\",\"Production\":\"Warner Bros. Pictures\",\"Website\":\"http://www.aquamanmovie.com/\",\"Response\":\"True\"}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        populateData(JsontoTest);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
        //jsonParser(JsontoTest);
    }


    /**Used to parse the json and after parse populate the form with info from json*/
    public void populateData(String jsonToParse){
        String title;
        String year;
        String duration;
        String genre;
        String rating;
        String Plot;
        try {
            JSONObject result = new JSONObject(jsonToParse);
            //Vai ser preciso fazer aqui a tal verificação se foi selecionado ou nao

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


    }

    public void playMovie(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

}
