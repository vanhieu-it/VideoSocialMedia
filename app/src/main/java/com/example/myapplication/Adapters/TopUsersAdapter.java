package com.example.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Entities.User;
import com.example.myapplication.R;
import com.example.myapplication.StatisticInChannelActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TopUsersAdapter extends ArrayAdapter {
    Context context;
    ArrayList<User> listAllTopUsers;
    int layoutId;

    public TopUsersAdapter(Context context, ArrayList listAllTopUsers, int layoutId) {
        super(context, layoutId);
        this.context = context;
        this.listAllTopUsers = listAllTopUsers;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return listAllTopUsers.size();
    }

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
            convertView = inflater.inflate(R.layout.list_top_users, null);
            TextView tvTopUserId = convertView.findViewById(R.id.tvTopId);
//        TextView tvTopUserEmail = findViewById(R.id.tvTopEmail);
//        TextView tvTopUserName = findViewById(R.id.tvTopName);
            ImageView imageViewUser = convertView.findViewById(R.id.ivTopUser);

            String image = listAllTopUsers.get(position).getImage();
            URL newUrl = null;
            try {
                newUrl = new URL(image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap mIcon_val = null;
            try {
                mIcon_val = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageViewUser.setImageBitmap(mIcon_val);

            User user = listAllTopUsers.get(position);
            if(user != null) {
                String info = "ID:     " + user.getUserID() + "\n";
                info += "Email: " + user.getEmail() + "\n";
                info += "Name:  " + user.getName() + "\n";
                info += "Likes: " + user.getTotalLike();
                tvTopUserId.setText(info);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Log.d("TopUserException", ex.toString());
        }
        return convertView;
    }
}
