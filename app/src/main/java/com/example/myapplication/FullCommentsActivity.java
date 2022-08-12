package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FullCommentsActivity extends AppCompatActivity {
    TextView tvCommentNum;
    ListView listViewFullComments;
    ArrayList<Comment> commentList = new ArrayList<>();
    CommentsAdapter commentsAdapter;
    String videoId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_comments);
        this.getSupportActionBar().setTitle("All Comments");
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#343d46")));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        videoId = bundle.getString("videoId");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setControl();
        initListComments();
        setEvent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setControl(){
        listViewFullComments = findViewById(R.id.listFullComments);
        tvCommentNum = findViewById(R.id.tvCommentNum);
    }

    private void setEvent(){
        commentsAdapter = new CommentsAdapter(FullCommentsActivity.this, commentList, R.layout.list_comments);
        listViewFullComments.setAdapter(commentsAdapter);
        listViewFullComments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                User user = commentList.get(position).getOwner();
                String ownerId = LoginActivity.googleId.replaceAll("^\"|\"$", "");
                if(user.getUserID().equals(ownerId)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(FullCommentsActivity.this);
                    builder.setMessage("Do you want do delete?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Deleting", Toast.LENGTH_SHORT).show();
                            String url = "https://video-vds.herokuapp.com/comment/" + commentList.get(position).getCommentID();
                            boolean deleteSuccess = deleteService(url);
                            if(deleteSuccess){
                                Toast.makeText(getApplicationContext(), "Delete comment successfully!", Toast.LENGTH_SHORT).show();
                                commentList.remove(position);
                                commentsAdapter.notifyDataSetChanged();

                                // Update number of comments
                                String infoNumComment = "";
                                int no_comments = commentList.size();
                                if(no_comments>1)
                                    infoNumComment = no_comments + " comments";
                                else
                                    infoNumComment = no_comments + " comment";
                                tvCommentNum.setText(infoNumComment);
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

    private void initListComments(){
        commentList = DetailedVideoActivity.getAllComments(videoId);
        int numCmt = commentList.size();
        String commentNumStr="";
        if(numCmt > 1)
            commentNumStr = commentList.size() + " comments";
        else commentNumStr = commentList.size() + " comment";
        tvCommentNum.setText(commentNumStr);
    }

    public boolean postService(String url, HashMap inputParams){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response message", response);
                    Toast.makeText(FullCommentsActivity.this,response,Toast.LENGTH_LONG).show();
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
}
