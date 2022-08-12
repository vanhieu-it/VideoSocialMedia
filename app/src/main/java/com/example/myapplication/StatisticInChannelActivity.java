package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.Adapters.TopUsersAdapter;
import com.example.myapplication.Entities.User;
import com.example.myapplication.Entities.Video;
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class StatisticInChannelActivity extends AppCompatActivity {
    ListView lTopLikeCmt;
    VideoView videoView;
    TextView tvVideoInfo, tvAllTopUsers;
    BarChart barChart;
    Button btnLikeMost;
    Button btnLikeCmt;
    public static int totalLikes = 0;
    public static String channelId = "";
    ArrayList<User> listTopUsers = new ArrayList<>();
    ArrayList<BarEntry> usersChart= new ArrayList<>();
    ArrayAdapter userArrayAdapter;
    int stateClick= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_in_channel);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // These lines of code are used with the purpose of avoiding asynchronous thread exception
        // Also avoid os.NetworkOnMainThreadException
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setControl();
        Bundle bundle = getIntent().getExtras();
        channelId = bundle.getString("channelId");
        String endpoint = String.format("https://video-vds.herokuapp.com/channel/%s/top", channelId);
        listTopUsers = getTopUser(endpoint);

        initBarChart(usersChart,listTopUsers);
//        Event button change chart
        ArrayList<Video> videos= getVideoOfChannel(String.format("https://video-vds.herokuapp.com/video?channelId=%s", channelId));

        ArrayList<User> userlikesmost= userLikesMost(listTopUsers,videos);
        System.err.println("userlikemost:" +userlikesmost);
        clickLikeMost(usersChart,userlikesmost);
        clickLikeCmtMost(usersChart,listTopUsers);

        setEvent();
        // Get video feature
        Video topVideo = getTopVideo(channelId);
        renderTopVideo(topVideo);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setControl(){
        videoView = findViewById(R.id.videoTopChannel);
        tvVideoInfo = findViewById(R.id.topVideoInfo);
        tvAllTopUsers = findViewById(R.id.tvAllTopUsers);
//        lvTopUsers = findViewById(R.id.listTopUsers);
        barChart= findViewById(R.id.barChart);
        btnLikeMost= findViewById(R.id.btnlike);
        btnLikeCmt= findViewById(R.id.btnlikecmt);
        lTopLikeCmt= findViewById(R.id.listTop);
    }

    private void setEvent(){
        tvAllTopUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StatisticInChannelActivity.this, "Full Top Users", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(StatisticInChannelActivity.this, FullTopUsersActivity.class);
                startActivity(intent);
            }
        });
    }

    public static ArrayList<User> getTopUser(String url){
        String infoJson = DetailedVideoActivity.getService(url);
        System.err.println("infoJson:"+infoJson);
        ArrayList<User> list = new ArrayList<>();
        // Parse json to user object
        try {
            JSONArray jsonArray = new JSONArray(infoJson);
            for(int index=0; index<jsonArray.length();index++){
                JSONObject userJsonObj = (JSONObject) jsonArray.get(index);
                if(userJsonObj != null) {
                    String _id = userJsonObj.getString("_id");
                    String email = userJsonObj.getString("email");
                    String name = userJsonObj.getString("name");
                    String image = userJsonObj.getString("image");
                    totalLikes = userJsonObj.getInt("totalLike");
                    User user = new User(_id, email, name, image, totalLikes);
                    list.add(user);
                }
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return list;
    }

    private Video getTopVideo(String channelID){
        for(Video v : Home_main.listVideosTrending){
            if(v.getChannelId().equals(channelID))
                return v;
        }
        return null;
    }

    private void renderTopVideo(Video video){
        if(video == null)   return;

        String videoPath = video.getVideoPaths()[0];
        String videoAsset = "https://video-vds.herokuapp.com" + videoPath;
        // Creating media controller
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        // Get video from resource
        Uri resource = Uri.parse(videoAsset);

        // Setting for starting video
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(resource);
        videoView.requestFocus();
        videoView.start();

        // Render Info
        String info = decodeUTF8(video.getTitle());
        info += "\nDescription: " + decodeUTF8(video.getDescription());
        info += "\n Views: " + video.getView();
        String[] likes = video.getLikes();
        if(likes != null){
            int numLike = likes.length;
            if(numLike>1){
                numLike = likes.length;
                info += "\t- " + numLike + " likes";
            }
            else info += "\t- " + numLike + " like";
        }
        tvVideoInfo.setText(info);
    }

    public String decodeUTF8(String str){
        try {
            // Avoid font error when displaying
            str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Html.fromHtml(str).toString();
    }
    public void initBarChart( ArrayList<BarEntry> usersChart, ArrayList<User> listTopUsers ){
        usersChart= new ArrayList<>();
        try {
            Collections.sort(listTopUsers);
            if (listTopUsers.size() <= 5) {
                for (int i = 0; i < listTopUsers.size(); i++) {
                    usersChart.add(new BarEntry(i, listTopUsers.get(i).getTotalLike()));
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    usersChart.add(new BarEntry(i, listTopUsers.get(i).getTotalLike()));
                }
            }
            BarDataSet barDataSet = new BarDataSet(usersChart, "User");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(16f);

            BarData barData = new BarData(barDataSet);
            barChart.setFitBars(true);
            barChart.setData(barData);
            barChart.animateY(2000);
            userArrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getNameUserArray(listTopUsers));
            lTopLikeCmt.setAdapter(userArrayAdapter);
            lTopLikeCmt.invalidateViews();
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    public void clickLikeCmtMost(ArrayList<BarEntry> usersChart, ArrayList<User> listTopUsers){
        btnLikeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initBarChart( usersChart,  listTopUsers);
            }
        });

    }
    public void clickLikeMost(ArrayList<BarEntry> usersChart, ArrayList<User> userlikesmost){
        btnLikeMost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initBarChart( usersChart,  userlikesmost);
            }
        });

    }
    public ArrayList<String> getNameUserArray(ArrayList<User> listTopUsers){
        ArrayList<String> nameUser= new ArrayList<>();
        for (User u: listTopUsers){
            nameUser.add(u.getName());
        }
        return nameUser;
    }
    public ArrayList<Video> getVideoOfChannel(String url){
        String infoJson = DetailedVideoActivity.getService(url);
        ArrayList<Video> list = new ArrayList<>();
        // Parse json to user object
        try {
            JSONObject jsonObject= new JSONObject(infoJson);
            JSONArray jsonArray= jsonObject.getJSONArray("allVideo");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject video= jsonArray.getJSONObject(i);
                String channelId= video.getString("channelId");
                int view=video.getInt("view");
                JSONArray arrayLike = video.getJSONArray("like");
                // Process like array
                String[] likes = null;
                if(arrayLike.length() >  0){
                    likes = new String[arrayLike.length()];
                    for(int position=0; position<arrayLike.length(); position++) {
                        likes[position] = String.valueOf(arrayLike.get(position));
                        System.err.println("likes:" + likes[position]);
                    }
                }

                String videoId=video.getString("_id");
                Video v= new Video(channelId,null,null,view,null,likes,null,videoId);
                list.add(v);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return list;
    }
    public ArrayList<User> userLikesMost(ArrayList<User> listTopUsers,ArrayList<Video> videos){
        ArrayList<User> list= new ArrayList<>();
        for(User u:listTopUsers){
            String name=u.getName(); int like=0;
            User temp= new User(u.getUserID(), null,name,null,like);
            for(Video v: videos){
                for(int i=0;i<v.getLengthLike();i++){
                    if(v.getLikes()[i].equals(u.getUserID())){
                        like++;
                    }
                }
            }
            temp.setTotalLike(like);
            if(temp.getTotalLike()>0) list.add(temp);
        }
        return list;
    }
}