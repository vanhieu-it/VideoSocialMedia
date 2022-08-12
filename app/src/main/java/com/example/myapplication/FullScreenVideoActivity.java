package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class FullScreenVideoActivity extends AppCompatActivity {
    public VideoView videoView;
    private MediaController mediaController;
    private int internalPosition = 0;
    String videoId = "", channelId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        videoView = findViewById(R.id.videoView);

        String fullScreen =  getIntent().getStringExtra("fullScreenInd");
        String videoPath = getIntent().getStringExtra("videoPath");
        videoId = getIntent().getStringExtra("videoId");
        channelId = getIntent().getStringExtra("channelId");
        int position=getIntent().getIntExtra("currentPosition", 0);

        if("y".equals(fullScreen)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        Uri videoUri = Uri.parse("https://video-vds.herokuapp.com" + videoPath);

        videoView.setVideoURI(videoUri);
        mediaController = new CustomMediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if(position != 0){
            videoView.seekTo(position);
        }

        videoView.start();
    }

    // When you change direction of phone, this method will be called.
    // It store the state of video (Current position)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store current position.
        savedInstanceState.putInt("currentPosition", videoView.getCurrentPosition());
        videoView.pause();
    }

    // After rotating the phone. This method is called.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        internalPosition = savedInstanceState.getInt("currentPosition");
        videoView.seekTo(internalPosition);
        videoView.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}