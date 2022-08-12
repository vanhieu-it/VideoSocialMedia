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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.ChannelActivity;
import com.example.myapplication.Entities.Channel;
import com.example.myapplication.Home_main;
import com.example.myapplication.R;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChannelsAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Channel> listAllChannels;
    int layoutId;

    public ChannelsAdapter(Context context, ArrayList listAllChannels, int layoutId) {
        super(context, layoutId);
        this.context = context;
        this.listAllChannels = listAllChannels;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return listAllChannels.size();
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            // These lines of code are used with the purpose of avoiding asynchronous thread exception
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_channels, null);

            ImageView ivChannel = convertView.findViewById(R.id.iconChannel);
            TextView tvChannelName = convertView.findViewById(R.id.tvChannelName);
            AppCompatButton btnSubscribeChannel = convertView.findViewById(R.id.btnSubChannel);
            btnSubscribeChannel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    String channelId = listAllChannels.get(position).getId();
                    String url = "https://video-vds.herokuapp.com/subscribe/" + channelId;

                    String text = btnSubscribeChannel.getText().toString();
                    Log.d("text", text);
                    if (btnSubscribeChannel.getText().toString().trim().equals(context.getResources().getString(R.string.Subscribed))) {
                        // Unsubscribe a channel
                        // Call Delete request.

//                        Boolean deleteSuccess = ((ChannelActivity) context).unSubscribe(url);
                        Boolean deleteSuccess = ((Home_main) context).unSubscribe(url);
                        if (deleteSuccess) {
                            btnSubscribeChannel.setText(R.string.Subscribe);
                            btnSubscribeChannel.setTextColor(R.color.black);
                            Toast.makeText(getContext(), "Unsubscribe channel successfully!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), "Unsubscribe channel failed!", Toast.LENGTH_SHORT).show();
                        return;

                    }
                    // subscribe a channel
//                    Boolean postSuccess = ((ChannelActivity) context).subscribe(url, null);
                    Boolean postSuccess = ((Home_main) context).subscribe(url, null);
                    if (postSuccess) {
                        Toast.makeText(getContext(), "Subscribe channel successfully", Toast.LENGTH_SHORT).show();
                        btnSubscribeChannel.setText(R.string.Subscribed);
                        btnSubscribeChannel.setTextColor(R.color.green);
                    }
                }
            });

            String imagePath = listAllChannels.get(position).getImage();
            imagePath = "https://video-vds.herokuapp.com" + imagePath;

            URL newUrl = null;
            try {
                newUrl = new URL(imagePath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap mIcon_val = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            ivChannel.setImageBitmap(mIcon_val);

            String channelName = "";
            try {
                // Avoid font error when displaying
                channelName = new String(listAllChannels.get(position).getChannelName().getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String decodedName = Html.fromHtml(channelName).toString();
            tvChannelName.setText(decodedName);

            // Check whether user has subscribed before
            boolean subscribed = listAllChannels.get(position).getSubscribed();
//            Log.d("Subscribed " + position, String.valueOf(subscribed));
            if(subscribed){
                btnSubscribeChannel.setText(R.string.Subscribed);
                btnSubscribeChannel.setTextColor(R.color.green);
            }
        } catch (Exception ex) {
            Log.d("Error in channel list: ", ex.toString());
        }
        return convertView;
    }
}
