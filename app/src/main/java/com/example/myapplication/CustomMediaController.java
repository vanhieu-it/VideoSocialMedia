package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;

class CustomMediaController extends MediaController {
    private ImageButton ib_FullScreen;
    private String isFullScreen;

    public CustomMediaController(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        //image button for full screen to be added to media controller
        ib_FullScreen = new ImageButton(super.getContext());

        LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 80;
        addView(ib_FullScreen, params);

        //fullscreen indicator from intent
        isFullScreen = ((Activity) getContext()).getIntent().getStringExtra("fullScreenInd");

        if ("y".equals(isFullScreen)) {
            ib_FullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
        } else {
            ib_FullScreen.setImageResource(R.drawable.ic_fullscreen);
        }

        // add listener to image button to handle full screen and exit full screen events
        ib_FullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent;
                    if ("y".equals(isFullScreen)) {
                        intent = new Intent(getContext(), DetailedVideoActivity.class);
//                        intent.putExtra("fullScreenInd", "");
                        intent.putExtra("positionVideo", ((FullScreenVideoActivity) getContext()).videoView.getCurrentPosition());
                        intent.putExtra("videoId", ((FullScreenVideoActivity) getContext()).videoId);
                        intent.putExtra("channelId", ((FullScreenVideoActivity) getContext()).channelId);
                    } else {
                        intent = new Intent(getContext(), FullScreenVideoActivity.class);
                        intent.putExtra("fullScreenInd", "y");
                        intent.putExtra("videoPath", ((DetailedVideoActivity) getContext()).videoPath);
                        intent.putExtra("currentPosition", ((DetailedVideoActivity) getContext()).videoView.getCurrentPosition());
                        intent.putExtra("videoId", ((DetailedVideoActivity) getContext()).videoId);
                        intent.putExtra("channelId", ((DetailedVideoActivity) getContext()).channelId);
                    }
                    ((Activity) getContext()).startActivity(intent);
                } catch (Exception e) {
                    Log.d("media", e.toString());
                }
            }
        });
    }
}