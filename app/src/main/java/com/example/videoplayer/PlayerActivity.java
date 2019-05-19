package com.example.videoplayer;

import android.content.pm.ActivityInfo;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    String movie_file_name;     // saves the movie file name that was received by the previous activity
    int pausedMilliSec;         // saves the movie current time when leaving the activity
    boolean resumedActivity;    // indicates if the activity was paused
    //int videoWidth;
    //int videoHeight;

    private VideoView vv;
    private MediaController mediacontroller;
    private Uri uri;
    private boolean isContinuously = false;
    private ProgressBar progressBar;

    /** Gestures  */
    private GestureLibrary gestureLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**Get the data from  MainActivity*/
        //TODO: change movie_file_name before push
        //movie_file_name= getIntent().getStringExtra("movie");
        movie_file_name= "Aquaman2018";
        Log.d("FILE_NAME_RECIEVED", movie_file_name);

        /**Force landscape orientation*/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_player);

        final View overlay = findViewById(R.id.playerLayout);

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        //listen for changes in the UI and hides it after a few seconds (status and navigation bar)
        overlay.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
                     @Override
                     public void onSystemUiVisibilityChange(int visibility) {
                         // Note that system bars will only be "visible" if none of the
                         // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                         if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                             // The system bars are visible. Make any desired
                             // adjustments to your UI, such as showing the action bar or
                             // other navigational controls.
                             Handler handler = new Handler();
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                                             // Set the content to appear under the system bars so that the
                                             // content doesn't resize when the system bars hide and show.
                                             | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                             | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                             | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                             // Hide the nav bar and status bar
                                             | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                             | View.SYSTEM_UI_FLAG_FULLSCREEN);
                                 }
                             }, 3000);


                         } else {
                             // The system bars are NOT visible. Make any desired
                             // adjustments to your UI, such as hiding the action bar or
                             // other navigational controls.
                         }
                     }
                 });

        progressBar = new ProgressBar(this);
        vv = (VideoView) findViewById(R.id.vv);

        mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(vv);

        uri = Uri.parse("android.resource://"+getPackageName()+"/raw/" + movie_file_name.toLowerCase());
        Log.d("MOVIE_PATH", uri.toString());

        //starts playing the video
        isContinuously = true;
        progressBar.setVisibility(View.VISIBLE);
        // media controller shoes video controls on click but interrupts immersive mode
        // vv.setMediaController(mediacontroller);
        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.start();

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(isContinuously){
                    vv.start();
                }
            }
        });

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                // Resumes the video where it was when leaving the activity
                if (resumedActivity) {
                    mp.seekTo(pausedMilliSec);
                }

//                videoWidth = mp.getVideoWidth();
//                videoHeight = mp.getVideoHeight();
//                changeVideoSize(overlay);

                progressBar.setVisibility(View.GONE);
                mp.start();
            }
        });


        /** Gestures */
        GestureOverlayView gestureOverlayView = findViewById(R.id.gestures);
        /** hides the gesture drawn line */
        gestureOverlayView.setGestureColor(Color.YELLOW);
        gestureOverlayView.setUncertainGestureColor(Color.YELLOW);

        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestureplayer);
        if (!gestureLib.load()) {
            finish();
        }
    }

    /** onGesturePerformed event */
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
                if (prediction.name.equals("play")) {
                    Toast toast=Toast.makeText(getApplicationContext(),"Play",Toast.LENGTH_SHORT);
                    toast.show();
                    vv.start();
                    break;
                } else if (prediction.name.equals("pause")) {
                    Toast toast=Toast.makeText(getApplicationContext(),"Pause",Toast.LENGTH_SHORT);
                    toast.show();
                    vv.pause();
                    break;
                } else if (prediction.name.equals("stop")) {
                    Toast toast=Toast.makeText(getApplicationContext(),"Stop",Toast.LENGTH_SHORT);
                    toast.show();
                    vv.stopPlayback();
                    break;
                } else if (prediction.name.equals("step_forward")) {
                    Toast toast=Toast.makeText(getApplicationContext(),"+10s",Toast.LENGTH_SHORT);
                    toast.show();
                    vv.seekTo(vv.getCurrentPosition() + 10000);
                    break;
                } else if (prediction.name.equals("step_back")) {
                    Toast toast=Toast.makeText(getApplicationContext(),"-10s",Toast.LENGTH_SHORT);
                    toast.show();
                    vv.seekTo(vv.getCurrentPosition() - 10000);
                    break;
                }
            }
        }
    }

    public void pauseMovie(View view){
        vv.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (vv != null ) {
            resumedActivity = true;
            pausedMilliSec = vv.getCurrentPosition();
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        final View overlay = findViewById(R.id.playerLayout);
//        changeVideoSize(overlay);
//    }

    /**Get file id for the movie*/
    public int getMovieFile(String movieName){
        int file_id;
        file_id = getResources().getIdentifier("" + movieName.toLowerCase() + ".mp4" , "raw", getApplicationContext().getPackageName());
        Log.d("FILE_ID", Integer.toString(file_id));
        return file_id;
    }

//    public void changeVideoSize(View overlay){
//        float scaleX, scaleY;
//        int pivotPointX, pivotPointY;
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//
//        //dont stretch video if its in portrait mode
//        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            //scaleY = 0.5f;
//
//            scaleY = (float)videoHeight / screenHeight;
//            scaleX = (float)screenWidth  / screenWidth;
//
//            Log.d("VIDEO_TESTE", videoHeight + " / " + screenHeight + " = " + scaleY);
//
//            pivotPointX = (int) (screenWidth / 2);
//            pivotPointY = (int) (screenHeight / 2);
//        }else{
//            scaleY = 1.0f;
//            scaleX = (videoWidth * screenHeight / videoHeight) / screenWidth;
//
//            pivotPointX = (int) (screenWidth / 2);
//            pivotPointY = (int) (screenHeight / 2);
//        }
//        overlay.setScaleX(scaleX);
//        overlay.setScaleY(scaleY);
//        overlay.setPivotX(pivotPointX);
//        overlay.setPivotY(pivotPointY);
//    }

}
