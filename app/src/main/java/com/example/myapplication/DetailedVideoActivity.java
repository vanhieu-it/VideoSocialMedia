package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapters.CommentsAdapter;
import com.example.myapplication.Entities.Comment;
import com.example.myapplication.Entities.User;
import com.example.myapplication.Entities.Video;
import com.example.myapplication.UIComponents.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DetailedVideoActivity extends AppCompatActivity {
    VideoView videoView;
    ListView customList;
    ArrayList<Comment> commentList = new ArrayList<>();
    CommentsAdapter commentsAdapter;

    TextView tvTitle, tvChannelInfo, tvLikeNum, tvViewNum;
    ImageButton ibtnLike, ibtnComments, ibtnShare, ibtnSend, ibtnChannelImg;
    AppCompatButton btnSubscribe;
    EditText edtComment;

    private String endpoint = "https://video-vds.herokuapp.com/video/";
    public String videoId = "";
    public String videoPath = "";
    public String channelId = "";
    int currentVideoPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_video);
        this.getSupportActionBar().hide();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        videoId = bundle.getString("videoId");
        String channelIdSubscribed = bundle.getString("channelId");
//        videoId = "jqZR9GiGjBWdvEoQ";
//        String channelIdSubscribed = "suSMSjAvbBfDGomh";
        // These lines of code are used with the purpose of avoiding asynchronous thread exception
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setControl();
        setEvent();
        int positionOfStreaming = getIntent().getIntExtra("positionVideo", 0);
        Log.d("videoPos", String.valueOf(positionOfStreaming));
        try {
            String videoInfo = getVideoInfo(videoId);
            Log.d("Video Info: ", videoInfo);

            // Parse video info
            Video video = getVideo(videoInfo);

            String[] videoPathList = video.getVideoPaths();
            videoPath = videoPathList[0];
            renderVideo(videoPath);

            String[] likes = video.getLikes();
            // Check whether user has liked video before
            int numLike = 0;
            if(likes != null){
                if(likes.length>0){
                    numLike = likes.length;
                    // Remove the double the begin and end double quote in login ID
                    String userId = LoginActivity.googleId;
                    if(Arrays.asList(likes).contains(userId)){
                        ibtnLike.setImageResource(R.drawable.thumb_up);
                        isButtonLikeVideoClicked = true;
                    }
                }
            }
            renderVideoInfo(video.getTitle(),video.getDescription(), video.getView(), numLike);

            channelId = video.getChannelId();
            renderChannelInfo(channelId);
            // Check whether user has subscribed channel before
            if(channelId.equals(channelIdSubscribed)){
                btnSubscribe.setBackgroundResource(R.color.teal_200);
                btnSubscribe.setText(R.string.Subscribed);
            }

            initListComment(videoId);
            renderListComment();

            if(positionOfStreaming > 0) recoverVideo(positionOfStreaming);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadingDialog loadingDialog = new LoadingDialog(DetailedVideoActivity.this);
        loadingDialog.buildDialog();
        commentList.clear();
        initListComment(videoId);
        renderListComment();
        if(currentVideoPosition > 0) recoverVideo(currentVideoPosition);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setControl(){
        videoView = findViewById(R.id.video);
        tvTitle = findViewById(R.id.title);
        tvChannelInfo = findViewById(R.id.tvChannelInfo);
        tvLikeNum = findViewById(R.id.tvLikeNum);
        tvViewNum = findViewById(R.id.tvViewNum);
        customList = findViewById(R.id.listComment);
        ibtnLike = findViewById(R.id.btnLike);
        ibtnComments = findViewById(R.id.btnComment);
        ibtnShare = findViewById(R.id.btnShare);
        ibtnSend = findViewById(R.id.btnSend);
        btnSubscribe = findViewById(R.id.btnSubscribe);
        edtComment = findViewById(R.id.edtCmt);
        ibtnChannelImg = findViewById(R.id.channelImg);
    }

    boolean isButtonLikeVideoClicked = false; // remains the state of the button
    private void setEvent() {
        ibtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://video-vds.herokuapp.com/like/video/" + videoId;
                if(v.getId() == R.id.btnLike){
                    isButtonLikeVideoClicked = !isButtonLikeVideoClicked; // toggle the state of button
                    if(isButtonLikeVideoClicked){
                        Boolean postSuccess = postService(url, null);
                        if(postSuccess){
                            Toast.makeText(getApplication(), "Like video successfully", Toast.LENGTH_SHORT).show();
                            ibtnLike.setImageResource(R.drawable.thumb_up);
                            // update number of likes
                            String likeNumStr = tvLikeNum.getText().toString();
                            int like = Integer.valueOf(likeNumStr.split(" ")[0]);
                            like += 1;
                            if(like > 1)
                                tvLikeNum.setText(like + " likes");
                            else tvLikeNum.setText(like + " like");
                        }
                    }
                    else{
                        Boolean deleteSuccess = deleteService(url);
                        if(deleteSuccess){
                            ibtnLike.setImageResource(R.drawable.thumb_up_trans);
                            Toast.makeText(getApplication(),"Unlike video successfully!", Toast.LENGTH_SHORT).show();
                            // update number of likes
                            String likeNumStr = tvLikeNum.getText().toString();
                            int like = Integer.valueOf(likeNumStr.split(" ")[0]);
                            if(like <= 0)   return;
                            like -= 1;
                            if(like > 1)
                                tvLikeNum.setText(like + " likes");
                            else tvLikeNum.setText(like + " like");
                        }
                        else
                            Toast.makeText(getApplication(), "Unlike this video failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        ibtnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVideoPosition = videoView.getCurrentPosition();
                Toast.makeText(getApplication(), "Go to see all comments", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailedVideoActivity.this, FullCommentsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("videoId", videoId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ibtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "External Share", Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Insert Subject here");
                String app_url = endpoint + videoId;
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://video-vds.herokuapp.com/subscribe/" + channelId;
                if(btnSubscribe.getText().toString().trim().equals(getApplicationContext().getResources().getString(R.string.Subscribed))){
                    // Unsubscribe a channel
                    // Call Delete request.
                    Boolean deleteSuccess = deleteService(url);
                    if(deleteSuccess){
                        btnSubscribe.setBackgroundResource(R.color.purple_200);
                        btnSubscribe.setText(R.string.Subscribe);
                        Toast.makeText(getApplication(),"Unsubscribe channel successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(getApplication(),"Unsubscribe channel failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // subscribe a channel
                Boolean postSuccess = postService(url, null);
                if(postSuccess){
                    Toast.makeText(getApplication(), "Subscribe channel successfully", Toast.LENGTH_SHORT).show();
                    btnSubscribe.setBackgroundResource(R.color.teal_200);
                    btnSubscribe.setText(R.string.Subscribed);
                }
            }
        });
        ibtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String content = edtComment.getText().toString().trim();
                    if(!content.equals("")){
                        int contentLen = content.length();
                        if(contentLen<4 || contentLen>100){
                            Toast.makeText(getApplicationContext(), "Comment's length from 5 to 100", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Post comment
                        HashMap<String, String> params = new HashMap();
                        params.put("content", content);
                        params.put("videoId", videoId);
                        String url = "https://video-vds.herokuapp.com/comment";
                        Boolean postSuccess = postService(url, params);
                        if(postSuccess){
                            Toast.makeText(getApplication(), "Post a comment successfully", Toast.LENGTH_SHORT).show();
                            // Update list of comments
                            // Waiting for posting data completion (latency time)
                            try {
                                //set time in milliseconds
                                Thread.sleep(3000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            ArrayList<Comment> newListCmt = getAllComments(videoId);
                            commentList.clear();
                            commentList.addAll(newListCmt);
                            commentsAdapter.notifyDataSetChanged();
                        }
                    }
                    else Toast.makeText(getApplicationContext(), R.string.cmtWarningEmpty, Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Log.d("exception", ex.toString());
                    ex.printStackTrace();
                    Toast.makeText(getApplication(), "Go to see all comments", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailedVideoActivity.this, FullCommentsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("videoId", videoId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    public static String getService(String resource){
        String info = "";
        try{
            URL url = new URL(resource);
            // make connection
            URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("Content-Type", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String l = null;
            while ((l=br.readLine())!=null) {
                info += l;
            }
            br.close();
        }
        catch (IOException io) {
            Log.d("exception", "IO exception");
        }
        catch (Exception ex){
            Log.d("exception", ex.toString());
        }
        return info;
    }

    public boolean postService(String url, HashMap inputParams){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response message", response);
                    Toast.makeText(DetailedVideoActivity.this,response,Toast.LENGTH_LONG).show();
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
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    if(inputParams == null){
                        HashMap<String, String> params = new HashMap<>();
                        return params;
                    }
                    else
                        return inputParams;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Cookie", LoginActivity.cookies);

                    return params;
                }};
            queue.add(request);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteService(String url){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response message", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if (error.networkResponse.data != null) {
                        try {
                            Log.e("Error", "onErrorResponse: " + new String(error.networkResponse.data, "UTF-8"));
                            throw new RuntimeException("Runtime error");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }){
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
                }};
            queue.add(request);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public static String getChannelInfo(String channelId){
        String resource = "https://video-vds.herokuapp.com/channel/" + channelId, info = getService(resource);
        return info;
    }

    private void renderChannelInfo(String channelId) throws IOException {
        String channelInfo = getChannelInfo(channelId), name = "", image="";
        try {
            JSONObject channel = new JSONObject(channelInfo) ;
            name = channel.getString("channelName");
            image = channel.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvChannelInfo.setText(name);
        image = "https://video-vds.herokuapp.com" + image;
        URL newUrl = null;
        try {
            newUrl = new URL(image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap mIcon_val = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
        ibtnChannelImg.setImageBitmap(mIcon_val);
    }

    private void renderVideoInfo(String title, String description, int views, int likes) {
        String info = "", numView = "", numLike = "";
        info += title + "\n";
        info += "Description: " + description ;
        if(views+1 > 1){
            numView += (views+1) + " views";
        }
        else numView += (views+1) + " view";

        if(likes > 1){
            numLike += likes + " likes";
        }
        else numLike += likes + " like";

        tvTitle.setText(info);
        tvViewNum.setText(numView);
        tvLikeNum.setText(numLike);
    }

    private void renderVideo(String videoPath){
        String videoAsset = "https://video-vds.herokuapp.com" + videoPath;
        // Creating media controller
        CustomMediaController mediaController = new CustomMediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        // Get video from resource
        Uri resource = Uri.parse(videoAsset);

        // Setting for starting video
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(resource);
        videoView.requestFocus();
        videoView.start();
    }

    public void recoverVideo(int position){
        if(position > 0){
            videoView.seekTo(position); videoView.start();
        }
    }

    private void renderListComment(){
        commentsAdapter = new CommentsAdapter(DetailedVideoActivity.this, commentList, R.layout.list_comments);
        customList.setAdapter(commentsAdapter);
        customList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                User user = commentList.get(position).getOwner();
                String ownerId = LoginActivity.googleId;
                if(user.getUserID().equals(ownerId)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailedVideoActivity.this);
                    builder.setMessage("Do you want do delete?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Deleting", Toast.LENGTH_SHORT).show();
                            String url = "https://video-vds.herokuapp.com/comment/" + commentList.get(position).getCommentID();
                            boolean deleteSuccess = deleteService(url);
                            if(deleteSuccess){
                                Toast.makeText(getApplicationContext(), "Delete comment successfully!", Toast.LENGTH_SHORT).show();
                                // Update list of comments
                                commentList.remove(position);
                                commentsAdapter.notifyDataSetChanged();
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else Toast.makeText(getApplication(), "Just owner can delete", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private String getVideoInfo(String id) throws IOException {
        String resource = endpoint + id, info = getService(resource);
        return info;
    }

    public static String getCommentInfo(String videoId) throws IOException {
        String resource = "https://video-vds.herokuapp.com/comment/" + videoId, info = getService(resource);
        return info;
    }

    // Parse Json to entities
    private Video getVideo(String videoInfo)  {
        Video video = null;
        try{
            // Parse video info with Json
            JSONArray videoList = new JSONArray(videoInfo);
            JSONObject videoJsonObj = (JSONObject) videoList.get(0);

            // Parse info
            String channelID = videoJsonObj.getString("channelId");
            String title = videoJsonObj.getString("title");
            String description = videoJsonObj.getString("description");
            int views = videoJsonObj.getInt("view");

            JSONArray arrayPath = videoJsonObj.getJSONArray("videoPath");
            // Process path array
            String[] paths = null;
            if(arrayPath.length() >  0){
                paths = new String[arrayPath.length()];
                for(int position=0; position<arrayPath.length(); position++)
                    paths[position] = String.valueOf(arrayPath.get(position));
            }

            JSONArray arrayLike = videoJsonObj.getJSONArray("like");
            // Process like array
            String[] likes = null;
            if(arrayLike.length() >  0){
                likes = new String[arrayLike.length()];
                for(int position=0; position<arrayLike.length(); position++)
                    likes[position] = String.valueOf(arrayLike.get(position));
            }

            String imagePath = videoJsonObj.getString("imagePath");

            video = new Video(channelID, title, description, views, paths, likes,  imagePath, videoId);
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
        }
        return video;
    }

    private void initListComment(String videoID){
        commentList = getAllComments(videoID);
    }

    public static ArrayList<Comment> getAllComments(String videoID){
        ArrayList<Comment> listCmt = new ArrayList<>();
        try {
            String jsonCmt = getCommentInfo(videoID);
            Log.d("Comments' Info: ", jsonCmt);

            // Parse video info with Json
            JSONArray commentListJson = new JSONArray(jsonCmt);

            // convert to Comment object
            for(int index=0; index<commentListJson.length(); index++){
                JSONObject commentJSONObj = (JSONObject) commentListJson.get(index);
                String googleId = commentJSONObj.getString("googleId");
                String videoId = commentJSONObj.getString("videoId");
                String content = commentJSONObj.getString("content");
                JSONArray arrayLike = commentJSONObj.getJSONArray("like");

                // Process like array
                String[] like = null;
                if(arrayLike.length() >  0){
                    like = new String[arrayLike.length()];
                    for(int position=0; position<arrayLike.length(); position++)
                        like[position] = String.valueOf(arrayLike.get(position));
                }
                // -----------------------------------

                String commentID = commentJSONObj.getString("_id");

                // Create user object
                JSONObject userJsonObj = commentJSONObj.getJSONObject("user");
                String userID = userJsonObj.getString("_id");
                String email = userJsonObj.getString("email");
                String name = userJsonObj.getString("name");
                String image = userJsonObj.getString("image");

                User user = new User(userID, email, name, image, 0);

                // Create comment object
                Comment comment = new Comment(googleId, videoId, content, like, commentID, user);
                listCmt.add(comment);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return listCmt;
    }
}
