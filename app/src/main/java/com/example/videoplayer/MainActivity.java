package com.example.videoplayer;

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

public class MainActivity extends AppCompatActivity {

    int[] IMAGES = {R.drawable.movie1, R.drawable.movie2, R.drawable.movie3, R.drawable.movie5, R.drawable.movie6, R.drawable.movie7,
            R.drawable.movie1, R.drawable.movie2, R.drawable.movie3, R.drawable.movie5, R.drawable.movie6, R.drawable.movie7};

    String[] NAMES = {"Movie 1", "Movie 2", "Movie 3", "Movie 5", "Movie 6", "Movie 7",
            "Movie 8", "Movie 9", "Movie 10", "Movie 12", "Movie 13", "Movie 14"};

    String[] YEARS = {"2015","2015","2015","2015","2015","2015","2015","2015","2015","2015","2015","2015"};

    String[] DURATIONS = {"215","215","215","015","205","215","15","205","215","015","205","201"};

    String[] CATEGORIES = {"Action, Comedy","Action, Comedy","Action, Comedy","Action, Comedy","Action, Comedy",
            "Action, Comedy","Action, Comedy","Action, Comedy","Action, Comedy","Action, Comedy","Action, Comedy","Action, Comedy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ListView listView=(ListView)findViewById(R.id.moviesListView);



        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Seu codigo aqui
                Log.d("TEsSS", "onItemClick: " + id);
                String data = "Some Data";
                previewMovie(view, data);
            }
        });
    }




    /** Called when the user taps the Send button */
    public void previewMovie(View view, String data) {
        String selected="{\"Title\":\"aquaman\",\"Year\":\"2018\",\"Rated\":\"PG-13\",\"Released\":\"21 Dec 2018\",\"Runtime\":\"143 min\",\"Genre\":\"Action, Adventure, Fantasy, Sci-Fi\",\"Director\":\"James Wan\",\"Writer\":\"David Leslie Johnson-McGoldrick (screenplay by), Will Beall (screenplay by), Geoff Johns (story by), James Wan (story by), Will Beall (story by), Mort Weisinger (Aquaman created by), Paul Norris (Aquaman created by)\",\"Actors\":\"Jason Momoa, Amber Heard, Willem Dafoe, Patrick Wilson\",\"Plot\":\"Arthur Curry, the human-born heir to the underwater kingdom of Atlantis, goes on a quest to prevent a war between the worlds of ocean and land.\",\"Language\":\"English\",\"Country\":\"Australia, USA\",\"Awards\":\"N/A\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BOTk5ODg0OTU5M15BMl5BanBnXkFtZTgwMDQ3MDY3NjM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.2/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"65%\"},{\"Source\":\"Metacritic\",\"Value\":\"55/100\"}],\"Metascore\":\"55\",\"imdbRating\":\"7.2\",\"imdbVotes\":\"236,813\",\"imdbID\":\"tt1477834\",\"Type\":\"movie\",\"DVD\":\"N/A\",\"BoxOffice\":\"N/A\",\"Production\":\"Warner Bros. Pictures\",\"Website\":\"http://www.aquamanmovie.com/\"}";

        String moviesList="[{\"Title\":\"aquaman\",\"Year\":\"2018\",\"Rated\":\"PG-13\",\"Released\":\"21 Dec 2018\",\"Runtime\":\"143 min\",\"Genre\":\"Action, Adventure, Fantasy, Sci-Fi\",\"Director\":\"James Wan\",\"Writer\":\"David Leslie Johnson-McGoldrick (screenplay by), Will Beall (screenplay by), Geoff Johns (story by), James Wan (story by), Will Beall (story by), Mort Weisinger (Aquaman created by), Paul Norris (Aquaman created by)\",\"Actors\":\"Jason Momoa, Amber Heard, Willem Dafoe, Patrick Wilson\",\"Plot\":\"Arthur Curry, the human-born heir to the underwater kingdom of Atlantis, goes on a quest to prevent a war between the worlds of ocean and land.\",\"Language\":\"English\",\"Country\":\"Australia, USA\",\"Awards\":\"N/A\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BOTk5ODg0OTU5M15BMl5BanBnXkFtZTgwMDQ3MDY3NjM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.2/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"65%\"},{\"Source\":\"Metacritic\",\"Value\":\"55/100\"}],\"Metascore\":\"55\",\"imdbRating\":\"7.2\",\"imdbVotes\":\"236,813\",\"imdbID\":\"tt1477834\",\"Type\":\"movie\",\"DVD\":\"N/A\",\"BoxOffice\":\"N/A\",\"Production\":\"Warner Bros. Pictures\",\"Website\":\"http://www.aquamanmovie.com/\"}, {\"Title\":\"aquaman\",\"Year\":\"2018\",\"Rated\":\"PG-13\",\"Released\":\"21 Dec 2018\",\"Runtime\":\"143 min\",\"Genre\":\"Action, Adventure, Fantasy, Sci-Fi\",\"Director\":\"James Wan\",\"Writer\":\"David Leslie Johnson-McGoldrick (screenplay by), Will Beall (screenplay by), Geoff Johns (story by), James Wan (story by), Will Beall (story by), Mort Weisinger (Aquaman created by), Paul Norris (Aquaman created by)\",\"Actors\":\"Jason Momoa, Amber Heard, Willem Dafoe, Patrick Wilson\",\"Plot\":\"Arthur Curry, the human-born heir to the underwater kingdom of Atlantis, goes on a quest to prevent a war between the worlds of ocean and land.\",\"Language\":\"English\",\"Country\":\"Australia, USA\",\"Awards\":\"N/A\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BOTk5ODg0OTU5M15BMl5BanBnXkFtZTgwMDQ3MDY3NjM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.2/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"65%\"},{\"Source\":\"Metacritic\",\"Value\":\"55/100\"}],\"Metascore\":\"55\",\"imdbRating\":\"7.2\",\"imdbVotes\":\"236,813\",\"imdbID\":\"tt1477834\",\"Type\":\"movie\",\"DVD\":\"N/A\",\"BoxOffice\":\"N/A\",\"Production\":\"Warner Bros. Pictures\",\"Website\":\"http://www.aquamanmovie.com/\"}]";


        Intent intent = new Intent(this, ConsultaActivity.class);
        intent.putExtra("MoviesList", moviesList);
        intent.putExtra("Selected", selected);
        startActivity(intent);
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return IMAGES.length;
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

            imageView.setImageResource(IMAGES[position]);
            textViewName.setText(NAMES[position]);
            textViewYear.setText(YEARS[position]);
            textViewCategorie.setText(CATEGORIES[position]);
            textViewDuration.setText(DURATIONS[position]+"min");

            return convertView;
        }
    }

}
