package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Entities.Video;
import com.example.myapplication.fragment.ChannelFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.TrendingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home_main extends AppCompatActivity {
    public static ArrayList<Video> listVideosTrending = new ArrayList<>();
    public static ArrayList<Video> listVideosHome = new ArrayList<>();
    //=== PRAGMENT ===
    private HomeFragment homeFragment = new HomeFragment();
    private ChannelFragment allChannelFragment = new ChannelFragment();
    private TrendingFragment trendingFragment = new TrendingFragment();

    private BottomNavigationView menuBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);


        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        menuBar = findViewById(R.id.menu_bar);

        setFragment(homeFragment);
        menuBar.setSelectedItemId(R.id.menu_home);
        menuBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.isChecked()){
                    return true;
                } else{
                    switch (item.getItemId()){
                        case R.id.menu_home:
                            setFragment(homeFragment);
                            getSupportActionBar().setTitle("Home");
                            return  true;
                        case R.id.menu_playlist:
                            setFragment(allChannelFragment);
//                            getSupportActionBar().setTitle("All Channel");
                            return  true;
                        case R.id.menu_search:
                            setFragment(trendingFragment);
//                            getSupportActionBar().setTitle("Trending");
                            return  true;
                        default:
                            setFragment(homeFragment);
                            getSupportActionBar().setTitle("Home");
                            return  true;
                    }
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment);
        ft.commit();
    }

    public boolean unSubscribe(String url) {
        try {
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
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean subscribe(String url, HashMap inputParams) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response message", response);
                    Toast.makeText(Home_main.this, response, Toast.LENGTH_LONG).show();
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
                    if (inputParams == null) {
                        HashMap<String, String> params = new HashMap<>();
                        return params;
                    } else
                        return inputParams;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Cookie", LoginActivity.cookies);

                    return params;
                }
            };
            queue.add(request);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}