package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_scene extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;
    Animation logo, title, version;
    ImageView ani_logo;
    TextView ani_title, ani_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scene);
        getSupportActionBar().setTitle("NHÓM 23 XIN CHÀO");

        ani_logo = (ImageView) findViewById(R.id.ani_logo);
        ani_title = (TextView) findViewById(R.id.tv_title);
        ani_version = (TextView) findViewById(R.id.tv_version);

        logo = AnimationUtils.loadAnimation(this, R.anim.load_logo);
        title = AnimationUtils.loadAnimation(this, R.anim.load_title);
        version = AnimationUtils.loadAnimation(this, R.anim.load_version);

        ani_logo.startAnimation(logo);
        ani_title.setAnimation(title);
        ani_version.setAnimation(version);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(Splash_scene.this, MainActivity.class);
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);
    }
}