package com.example.videoplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class FullScreenImage extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**Force landscape orientation*/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);
        Intent intent = getIntent();
        ImageView imageView = findViewById(R.id.fullImage);

        imageView.setImageResource(R.drawable.player_gestures);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
