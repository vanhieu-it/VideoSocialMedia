package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapters.VideosAdapter;
import com.example.myapplication.Entities.Video;
import com.example.myapplication.UIComponents.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ListView listViewVideo;
    ArrayList<Video> listAllVideos = new ArrayList<>();
    String info = "";
    Spinner spinner;
    int flag_spinner = 0; /// 0-none, 1- A->Z, 2- Sort  theo like
    VideosAdapter videosAdapter;

    public int getFlag_spinner() {
        return flag_spinner;
    }

    public void setFlag_spinner(int flag_spinner) {
        this.flag_spinner = flag_spinner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.home);

        setControl();
        setSpinner();
        String url = "https://video-vds.herokuapp.com/video";
        getAllVideosInfo(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(HomeActivity.this);
        loadingDialog.buildDialog();

        setControl();
        String url = "https://video-vds.herokuapp.com/video";
        listAllVideos.clear();
        MenuActivity.listVideosTrending.clear();
        getAllVideosInfo(url);
    }

    public void setSpinner(){
        ////set spinner item.
        spinner.setOnItemSelectedListener(HomeActivity.this);
        List<String> itemSpinner = new ArrayList<>();
        itemSpinner.add("none");
        itemSpinner.add("A -> Z");
        itemSpinner.add("By like");
        /////Tạo adapter
        ArrayAdapter<String> spinerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, itemSpinner);
        spinner.setAdapter(spinerAdapter);
    }

    private void setControl() {
        listViewVideo = findViewById(R.id.listVideos);
        spinner = findViewById(R.id.spinner_sort);
    }

    public void getAllVideosInfo(String url) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("All videos", response);
                    if (response.length() > 0) {
                        info = response;
                        if (info.isEmpty()) return;
                        else if (info.equals("[]")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setMessage("Please subscribe channels to explore");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            return;
                        }
                        try {
                            listAllVideos = parseListVideo(info);
                            Log.d("Trending", String.valueOf(MenuActivity.listVideosTrending.size()));
                            // Sort by videos' views descending
                            Collections.sort(MenuActivity.listVideosTrending, Video.VideoViewComparator);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        VideosAdapter videosAdapter = new VideosAdapter(HomeActivity.this, listAllVideos, R.layout.list_videos);
                        listViewVideo.setAdapter(videosAdapter);
                        if(flag_spinner==1) {
                            Collections.sort(listAllVideos, Video.VideoViewComparator_byName);
                            videosAdapter.notifyDataSetChanged();
                        }else if (flag_spinner==2){
                            Collections.sort(listAllVideos, Video.VideoViewComparator_byLike);
                            videosAdapter.notifyDataSetChanged();
                        }

                        listViewVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getApplicationContext(), "Go to watch video", Toast.LENGTH_SHORT).show();
                                String videoId = listAllVideos.get(position).getVideoId();
                                String channelId = listAllVideos.get(position).getChannelId();
                                Intent intent = new Intent(getApplicationContext(), DetailedVideoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("videoId", videoId);
                                bundle.putString("channelId", channelId);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "SUBSCRIBE CHANNELS TO WATCH MORE VIDEOS", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if (error.networkResponse.data != null) {
                        try {
                            Log.e("Error", "onErrorResponse: " + new String(error.networkResponse.data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Cookie", LoginActivity.cookies);

                    return params;
                }
            };
            queue.add(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<Video> parseListVideo(String listInfo) throws JSONException {
        if (listInfo.equals("[]"))
            return null;

        JSONArray videoOfAllChannelsJson = new JSONArray(listInfo);
        ArrayList<Video> listVideos = new ArrayList<>();
        for (int pos = 0; pos < videoOfAllChannelsJson.length(); pos++) {
            JSONObject videoListInfo = (JSONObject) videoOfAllChannelsJson.get(pos);
            JSONArray videoListAll = (JSONArray) videoListInfo.getJSONArray("allVideo");
            int numOfVideo = videoListAll.length();
            if (numOfVideo > 0) {
                for (int index = 0; index < videoListAll.length(); index++) {
                    JSONObject videoJson = (JSONObject) videoListAll.get(index);
                    String channelId = videoJson.getString("channelId");
                    String title = videoJson.getString("title");
                    String description = videoJson.getString("description");
                    int view = videoJson.getInt("view");
                    JSONArray videoPaths = videoJson.getJSONArray("videoPath");
                    String[] paths = null;
                    if (videoPaths.length() > 0) {
                        paths = new String[videoPaths.length()];
                        for (int position = 0; position < videoPaths.length(); position++)
                            paths[position] = String.valueOf(videoPaths.get(position));
                    }
                    JSONArray likeArr = videoJson.getJSONArray("like");
                    // Process like array
                    String[] likes = null;
                    if (likeArr.length() > 0) {
                        likes = new String[likeArr.length()];
                        for (int position = 0; position < likeArr.length(); position++)
                            likes[position] = String.valueOf(likeArr.get(position));
                    }
                    String imagePath = videoJson.getString("imagePath");
                    String videoId = videoJson.getString("_id");

                    Video video = new Video(channelId, title, description, view, paths, likes, imagePath, videoId);
                    listVideos.add(video);
                }
            }
            // Get video trending of all subscribed channels
            JSONObject videoTrendingJson = videoListInfo.getJSONObject("trendy");
            if (videoTrendingJson != null && !videoTrendingJson.isNull("channelId")) {
                String channelId = videoTrendingJson.getString("channelId");
                String title = videoTrendingJson.getString("title");
                String description = videoTrendingJson.getString("description");
                int view = videoTrendingJson.getInt("view");
                JSONArray videoPaths = videoTrendingJson.getJSONArray("videoPath");
                String[] paths = null;
                if (videoPaths.length() > 0) {
                    paths = new String[videoPaths.length()];
                    for (int position = 0; position < videoPaths.length(); position++)
                        paths[position] = String.valueOf(videoPaths.get(position));
                }
                JSONArray likeArr = videoTrendingJson.getJSONArray("like");
                // Process like array
                String[] likes = null;
                if (likeArr.length() > 0) {
                    likes = new String[likeArr.length()];
                    for (int position = 0; position < likeArr.length(); position++)
                        likes[position] = String.valueOf(likeArr.get(position));
                }
                String imagePath = videoTrendingJson.getString("imagePath");
                String videoId = videoTrendingJson.getString("_id");

                Video videoTrending = new Video(channelId, title, description, view, paths, likes, imagePath, videoId);
                if (!isVideoTrendingExisted(videoTrending))
                    MenuActivity.listVideosTrending.add(videoTrending);
            }
        }

        return listVideos;
    }

    private boolean isVideoTrendingExisted(Video video) {
        for (Video v : MenuActivity.listVideosTrending) {
            if (v.getVideoId().equals(video.getVideoId()))
                return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), "Bạn đã chọn : " + item, Toast.LENGTH_LONG).show();
        if (item.compareTo("A -> Z")==0){
            flag_spinner = 1;
            HomeActivity.this.onResume();
        }

        else if (item.compareTo("none")==0){
            flag_spinner = 0;
            HomeActivity.this.onResume();
        }

        else if (item.compareTo("By like")==0) {
            flag_spinner = 2;
            HomeActivity.this.onResume();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}