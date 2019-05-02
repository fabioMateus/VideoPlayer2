package com.example.videoplayer;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    int[] IMAGES = {R.drawable.movie1, R.drawable.movie2, R.drawable.movie3, R.drawable.movie4, R.drawable.movie5, R.drawable.movie6, R.drawable.movie7,
            R.drawable.movie1, R.drawable.movie2, R.drawable.movie3, R.drawable.movie4, R.drawable.movie5, R.drawable.movie6, R.drawable.movie7};

    String[] NAMES = {"Movie 1", "Movie 2", "Movie 3", "Movie 4", "Movie 5", "Movie 6", "Movie 7",
            "Movie 8", "Movie 9", "Movie 10", "Movie 11", "Movie 12", "Movie 13", "Movie 14"};

    String[] DESCRIPTIONS = {"Description BlaBláBlá 1", "Description BlaBláBlá 2", "Description BlaBláBlá 3", "Description BlaBláBlá 4", "Description BlaBláBlá 5",
            "Description BlaBláBlá 6", "Description BlaBláBlá 7", "Description BlaBláBlá 8", "Description BlaBláBlá 9", "Description BlaBláBlá 10", "Description BlaBláBlá 11",
            "Description BlaBláBlá 12", "Description BlaBláBlá 13", "Description BlaBláBlá 14"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView=(ListView)findViewById(R.id.moviesListView);

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

    }


    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, ConsultaActivity.class);
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.elemento_listagem, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.MovieImageView);
            TextView textViewName = (TextView) convertView.findViewById(R.id.MovieNameTextView);
            TextView textViewDescription = (TextView) convertView.findViewById(R.id.MovieDescriptionTextView);

            imageView.setImageResource(IMAGES[position]);
            textViewName.setText(NAMES[position]);
            textViewDescription.setText(DESCRIPTIONS[position]);

            return convertView;
        }
    }

}
