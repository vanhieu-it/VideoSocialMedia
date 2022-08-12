package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapters.ChannelsAdapter;
import com.example.myapplication.ChannelActivity;
import com.example.myapplication.Entities.Channel;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.StatisticInChannelActivity;
import com.example.myapplication.UIComponents.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChannelFragment extends Fragment {
    ListView listViewChannels;
    ArrayList<Channel> listAllChannels = new ArrayList<>();
    String info = "";

    public ChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_allchannel,container,false);
        listViewChannels = view.findViewById(R.id.allChannels);
        // Create loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.buildDialog();
        // These lines of code are used with the purpose of avoiding asynchronous thread exception
        // Also avoid os.NetworkOnMainThreadException
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String url = "https://video-vds.herokuapp.com/channel";
        getAllChannels(url);
        return view;
    }

    private ArrayList<Channel> parseListChannelInfo(String info) throws JSONException {
        ArrayList<Channel> channelList = new ArrayList<>();

        JSONArray listChannelJson = new JSONArray(info);
        for (int index = 0; index < listChannelJson.length(); index++) {
            JSONObject channelJson = (JSONObject) listChannelJson.get(index);
            String channelName = channelJson.getString("channelName");
            String userName = channelJson.getString("username");
            String password = channelJson.getString("password");
            String image = channelJson.getString("image");
            String _id = channelJson.getString("_id");
            Boolean subscribed = channelJson.getBoolean("subscribed");

            Channel channel = new Channel(channelName, userName, password, image, _id, subscribed);
            channelList.add(channel);
        }

        return channelList;
    }

    private void getAllChannels(String url) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("All channels", response);
                    if (response.length() > 0) {
                        info = response;
                        if (info.isEmpty()) return;
                        else if (info.equals("[]")) return;
                        try {
                            listAllChannels = parseListChannelInfo(info);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ChannelsAdapter channelsAdapter;
                        channelsAdapter = new ChannelsAdapter(getActivity(), listAllChannels, R.layout.list_channels);
                        listViewChannels.setAdapter(channelsAdapter);
                        listViewChannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(), StatisticInChannelActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("channelId", listAllChannels.get(position).getId());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "SUBSCRIBE CHANNELS TO WATCH MORE VIDEOS", Toast.LENGTH_SHORT).show();
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

//    public boolean unSubscribe(String url) {
//        try {
//            RequestQueue queue = Volley.newRequestQueue(getActivity());
//            StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d("response message", response.toString());
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    error.printStackTrace();
//                    if (error.networkResponse.data != null) {
//                        try {
//                            Log.e("Error", "onErrorResponse: " + new String(error.networkResponse.data, "UTF-8"));
//                            throw new RuntimeException("Runtime error");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    HashMap<String, String> params = new HashMap<>();
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("Cookie", LoginActivity.cookies);
//
//                    return params;
//                }
//            };
//            queue.add(request);
//            return true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean subscribe(String url, HashMap inputParams) {
//        try {
//            RequestQueue queue = Volley.newRequestQueue(getActivity());
//            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d("response message", response);
//                    Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    error.printStackTrace();
//                    if (error.networkResponse.data != null) {
//                        try {
//                            Log.e("Error", "onErrorResponse: " + new String(error.networkResponse.data, "UTF-8"));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    if (inputParams == null) {
//                        HashMap<String, String> params = new HashMap<>();
//                        return params;
//                    } else
//                        return inputParams;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("Cookie", LoginActivity.cookies);
//
//                    return params;
//                }
//            };
//            queue.add(request);
//            return true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//    }
}
