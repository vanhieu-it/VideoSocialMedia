package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.Adapters.VideosAdapter;
import com.example.myapplication.DetailedVideoActivity;
import com.example.myapplication.Entities.Video;
import com.example.myapplication.Home_main;
import com.example.myapplication.MenuActivity;
import com.example.myapplication.R;
import com.example.myapplication.TrendingActivity;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {
    ListView listViewTrending;
    ArrayList<Video> listTrendingVideos = new ArrayList<>();
    VideosAdapter videosAdapter;
    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending,container,false);
        listViewTrending = view.findViewById(R.id.listTrendingVideos);
        setEvent();
        initListTrending();
        return view;
    }

    private void initListTrending(){
        listTrendingVideos.clear();
        listTrendingVideos.addAll(Home_main.listVideosTrending);
        videosAdapter.notifyDataSetChanged();
    }

    private void setEvent(){
        videosAdapter = new VideosAdapter(getContext(), listTrendingVideos, R.layout.list_videos);
        listViewTrending.setAdapter(videosAdapter);
        listViewTrending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Go to watch video", Toast.LENGTH_SHORT).show();
                String videoId = listTrendingVideos.get(position).getVideoId();
                String channelId = listTrendingVideos.get(position).getChannelId();
                Intent intent = new Intent(getContext(), DetailedVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("videoId", videoId);
                bundle.putString("channelId", channelId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}