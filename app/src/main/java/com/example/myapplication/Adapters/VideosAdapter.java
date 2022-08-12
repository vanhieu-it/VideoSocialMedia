package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.DetailedVideoActivity;
import com.example.myapplication.Entities.Video;
import com.example.myapplication.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class VideosAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Video> listAllVideos;
    int layoutId;

    public VideosAdapter(Context context, ArrayList listAllVideos, int layoutId) {
        super(context, layoutId);
        this.context = context;
        this.listAllVideos = listAllVideos;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return listAllVideos.size();
    }

    @SuppressLint("LongLogTag")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try{
            // These lines of code are used with the purpose of avoiding asynchronous thread exception
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_videos, null);

            ImageView ivVideo = convertView.findViewById(R.id.imageVideo);
            TextView tvVideoInfo = convertView.findViewById(R.id.tvVideoInfo);

            String imagePath = listAllVideos.get(position).getImagePath();
            imagePath = "https://video-vds.herokuapp.com" + imagePath;
            URL newUrl = null;
            try {
                newUrl = new URL(imagePath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap mIcon_val = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            ivVideo.setImageBitmap(mIcon_val);

            String videoName="", info="", channelName="", channelInfo="";
            int views=0, numLike = 0;
            try {
                // Avoid font error when displaying
                videoName = new String(listAllVideos.get(position).getTitle().getBytes("ISO-8859-1"), "UTF-8");
                channelInfo = DetailedVideoActivity.getChannelInfo(listAllVideos.get(position).getChannelId());
                JSONObject channelJson = new JSONObject(channelInfo) ;
                channelName = channelJson.getString("channelName");
                views = listAllVideos.get(position).getView();
                String[] likes = listAllVideos.get(position).getLikes();
                if(likes != null){
                    if(likes.length>0) {
                        numLike= likes.length;
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String decodedName = Html.fromHtml(videoName).toString();
            info += decodedName + "\n";
            info += channelName + "\t-\t";
            if(views > 1){
                info += views + " views";
            }
            else info += views + " view";

            if(numLike != 0) {
                info += "\t-\t";
                if(numLike > 1){
                    info += numLike + " likes";
                }
                else info += numLike + " like";
            }

            tvVideoInfo.setText(info);
        }
        catch (Exception ex){
            Log.d("Exception in custom list view: ", ex.toString());
        }
        return convertView;
    }
}