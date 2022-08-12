package com.example.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.myapplication.DetailedVideoActivity;
import com.example.myapplication.Entities.Comment;
import com.example.myapplication.FullCommentsActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;

public class CommentsAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Comment> commentList;
    int layoutId;

    public CommentsAdapter(Context context, ArrayList<Comment> commentList, int layoutId) {
        super(context, layoutId);
        this.context = context;
        this.commentList = commentList;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_comments, null);

            ImageView icon = convertView.findViewById(R.id.icon);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(commentList.get(position).getOwner() != null)
                        Toast.makeText(getContext(), commentList.get(position).getOwner().getName(), Toast.LENGTH_SHORT).show();
                }
            });

            URL newurl = null;
            try {
                newurl = new URL(commentList.get(position).getOwner().getImage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            icon.setImageBitmap(mIcon_val);

            TextView content = convertView.findViewById(R.id.tvContent);
            TextView numLikeCmt = convertView.findViewById(R.id.tvLikeNumCmt);
            content.setText(commentList.get(position).getContent());
            ImageButton ibtnLikeCmt = convertView.findViewById(R.id.btnLikeCmt);
            ibtnLikeCmt.setTag("LIKE");
            ibtnLikeCmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://video-vds.herokuapp.com/like/comment/" + commentList.get(position).getCommentID();
                    if(v.getId() == R.id.btnLikeCmt){
                        if(ibtnLikeCmt.getTag().equals("LIKE")){ // Check if whether user has not liked comment before
                            Boolean postSuccess;
                            if(context instanceof DetailedVideoActivity){
                                postSuccess = ((DetailedVideoActivity) context).postService(url, null);
                            }
                            else postSuccess = ((FullCommentsActivity) context).postService(url, null);

                            if(postSuccess){
                                Toast.makeText(getContext(), "Like comment successfully", Toast.LENGTH_SHORT).show();
                                ibtnLikeCmt.setImageResource(R.drawable.thumb_up);
                                ibtnLikeCmt.setTag("LIKED");
                                // Update no. like
                                int numLike;
                                String likeStr = numLikeCmt.getText().toString();
                                if("".equals(likeStr)) numLike = 1;
                                else numLike = Integer.valueOf(likeStr) + 1;
                                likeStr = String.valueOf(numLike);
                                numLikeCmt.setText(likeStr);
                            }
                            else Toast.makeText(getContext(), "Like comment failed!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Boolean deleteSuccess;
                            if(context instanceof DetailedVideoActivity)
                                deleteSuccess = ((DetailedVideoActivity) context).deleteService(url);
                            else deleteSuccess = ((FullCommentsActivity) context).deleteService(url);

                            if(deleteSuccess){
                                ibtnLikeCmt.setImageResource(R.drawable.thumb_up_trans);
                                Toast.makeText(getContext(),"Unlike comment successfully!", Toast.LENGTH_SHORT).show();
                                ibtnLikeCmt.setTag("LIKE");
                                // Update no. like
                                int numLike;
                                String likeStr = numLikeCmt.getText().toString();
                                numLike = Integer.valueOf(likeStr) - 1;
                                if(numLike > 0)
                                    numLikeCmt.setText(Integer.toString(numLike));
                                else numLikeCmt.setText("");
                            }
                            else
                                Toast.makeText(getContext(), "Unlike comment failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            // Check this user has already liked comments yet
            String[] likes = commentList.get(position).getLike();
            String userId = LoginActivity.googleId;
            if(likes != null){
                if(likes.length>0) {
                    numLikeCmt.setText(Integer.toString(likes.length));
                    if(Arrays.asList(likes).contains(userId)){
                        ibtnLikeCmt.setImageResource(R.drawable.thumb_up);
                        ibtnLikeCmt.setTag("LIKED");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}