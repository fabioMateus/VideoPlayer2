package com.example.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;


import java.util.ArrayList;


public class PlayerActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    String movie_name;     // saves the movie file name that was received by the previous activity
    int pausedMilliSec;         // saves the movie current time when leaving the activity
    boolean resumedActivity;    // indicates if the activity was paused
    boolean canShowController = false;

    MediaController hiddenmediacontroller;
    private VideoView vv;
    private Uri uri;
    private boolean isContinuously = false;
    private ProgressBar progressBar;

    /** Gestures  */
    private GestureLibrary gestureLib;

    /** Shake detection */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get the data from  MainActivity
        movie_name = getIntent().getStringExtra("movie");
        Log.d("MOVIE_NAME_RECIEVED", movie_name);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                handleShakeEvent(count);
            }
        });

        //Force landscape orientation
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_player);

        final View overlay = findViewById(R.id.playerLayout);

        //enable android immersive mode
        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        //listen for changes in the UI and hides it after a 3 seconds (status and navigation bar)
        overlay.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
                     @Override
                     public void onSystemUiVisibilityChange(int visibility) {
                         if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                             Handler handler = new Handler();
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                                             | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                             | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                             | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                             | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                             | View.SYSTEM_UI_FLAG_FULLSCREEN);
                                 }
                             }, 3000);


                         }
                     }
                 });


        progressBar = new ProgressBar(this);
        vv = findViewById(R.id.vv);

        hiddenmediacontroller = new MediaController(this) {
            @Override
            public void show()
            {
                hiddenmediacontroller.hide();
            }
        };
        hiddenmediacontroller.setAnchorView(vv);

        uri = Uri.parse("android.resource://"+getPackageName()+"/raw/" + getFileName(movie_name));
        Log.d("MOVIE_PATH", uri.toString());

        //starts playing the video
        isContinuously = true;
        progressBar.setVisibility(View.VISIBLE);
        vv.setVideoURI(uri);
        vv.requestFocus();

        vv.setMediaController(hiddenmediacontroller);

        //if is the first time playing a video shows the possible gestures
        checkFirstRun();

        vv.start();

        //Listens for clicks on the screen
        vv.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent)
            {
                int x = (int)motionEvent.getX();
                int y = (int)motionEvent.getY();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (y > height - 75) { //shows media controller if the user clicks the bottom of screen
                            showMediaController();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

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

                progressBar.setVisibility(View.GONE);
                mp.start();
            }
        });


        /* Gestures */
        GestureOverlayView gestureOverlayView = findViewById(R.id.gestures);
        /* hides the gesture drawn line */
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
                switch(prediction.name){
                    case "play":
                        showImageToast("play");
                        vv.start();
                        break;
                    case "pause":
                        showImageToast("pause");
                        vv.pause();
                        break;
                    case "stop":
                        showImageToast("stop");
                        vv.seekTo(0);
                        vv.pause();
                        break;
                    case "step_forward":
                        showImageToast("fastforward");
                        vv.seekTo(vv.getCurrentPosition() + 10000);
                        break;
                    case "step_back":
                        showImageToast("rewind");
                        vv.seekTo(vv.getCurrentPosition() - 10000);
                        break;
                    default:
                        Log.e("GESTURE_PERFORMED", "Gesture unknown");
                }
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);

        if (vv != null ) {
            resumedActivity = true;
            pausedMilliSec = vv.getCurrentPosition();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    /**Get file name for the movie, removes spaces and applies lowercase to all letters*/
    public String getFileName(String movieName){
        String file_name = movieName.toLowerCase().replace(" ", "");
        Log.d("FILE_NAME_RECIEVED", file_name);
        return file_name;
    }

    /**Shows an image when a gesture is performed*/
    public void showImageToast(String name){
        int resourceId = this.getResources().getIdentifier(name, "drawable", this.getPackageName());

        ImageView view = new ImageView(getApplicationContext());
        view.setImageResource(resourceId);

        final Toast toast = new Toast(getApplicationContext());
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 200);
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun){
            Intent intent = new Intent(this, FullScreenImage.class);
            startActivity(intent);

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public void showMediaController() {
        canShowController = true;
        MediaController newmediacontroller = new MediaController(this);
        newmediacontroller.setAnchorView(vv);
        vv.setMediaController(newmediacontroller);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vv.setMediaController(hiddenmediacontroller);
            }
        }, 3000);
    }

    /** Pauses/resumes the video if the phone was shaken */
    public void handleShakeEvent(int count){
        if (count > 1){
            if (vv.isPlaying()){
                vv.pause();
                resumedActivity = true;
                pausedMilliSec = vv.getCurrentPosition();
                showImageToast("pause");
            }else{
                vv.resume();
                showImageToast("play");
            }
        }
    }
}
