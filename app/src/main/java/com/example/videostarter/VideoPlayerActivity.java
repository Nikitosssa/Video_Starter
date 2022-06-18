package com.example.videostarter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class VideoPlayerActivity extends AppCompatActivity {

    private String videoName = ClientActivity.message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
    }
}