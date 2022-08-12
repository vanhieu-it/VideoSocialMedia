package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.Adapters.VideosAdapter;
import com.example.myapplication.Entities.Video;
import com.example.myapplication.UIComponents.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TrendingActivity extends AppCompatActivity {
    ListView listViewTrending;
    ArrayList<Video> listTrendingVideos = new ArrayList<>();
    VideosAdapter videosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        setControl();
        setEvent();
        initListTrending();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListTrending();
    }

    private void setControl(){
        listViewTrending = findViewById(R.id.listTrendingVideos);
    }

    private void initListTrending(){
        listTrendingVideos.clear();
        listTrendingVideos.addAll(MenuActivity.listVideosTrending);
        videosAdapter.notifyDataSetChanged();
    }

    private void setEvent(){
        videosAdapter = new VideosAdapter(TrendingActivity.this, listTrendingVideos, R.layout.list_videos);
        listViewTrending.setAdapter(videosAdapter);
        listViewTrending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Go to watch video", Toast.LENGTH_SHORT).show();
                String videoId = listTrendingVideos.get(position).getVideoId();
                String channelId = listTrendingVideos.get(position).getChannelId();
                Intent intent = new Intent(getApplicationContext(), DetailedVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("videoId", videoId);
                bundle.putString("channelId", channelId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}