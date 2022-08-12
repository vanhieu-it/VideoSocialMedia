package com.example.myapplication;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;

import com.example.myapplication.Entities.Video;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends ActivityGroup {
    public static ArrayList<Video> listVideosTrending = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup(this.getLocalActivityManager());
        TabHost.TabSpec spec;
        Intent intent;

        // Home tab
        spec = tabHost.newTabSpec("home");
        spec.setIndicator("HOME");
        intent = new Intent(this, HomeActivity.class);
        spec.setContent(intent);
        tabHost.addTab(spec);

        // Channel tab
        spec = tabHost.newTabSpec("channels");
        spec.setIndicator("CHANNELS");
        intent = new Intent(this, ChannelActivity.class);
        spec.setContent(intent);
        tabHost.addTab(spec);

        // Trending tab
        spec = tabHost.newTabSpec("trending");
        spec.setIndicator("TRENDING");
        intent = new Intent(this, TrendingActivity.class);
        spec.setContent(intent);
        tabHost.addTab(spec);

    }
}