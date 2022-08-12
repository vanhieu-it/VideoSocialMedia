package com.example.myapplication.Entities;

import java.util.Comparator;

public class Video {
    private String channelId;
    private String title;
    private String description;
    private int view;
    private String[] videoPaths;
    private String[] likes;
    private String imagePath;
    private String videoId;

    public Video() {}

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String[] getVideoPaths() {
        return videoPaths;
    }

    public void setVideoPaths(String[] videoPaths) {
        this.videoPaths = videoPaths;
    }

    public String[] getLikes() {
        return likes;
    }

    public void setLikes(String[] likes) {
        this.likes = likes;
    }

    public int getLengthLike(){
        int length = 0;
        if (getLikes() != null)
            length = getLikes().length;
        return length;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Video(String channelId, String title, String description, int view, String[] videoPaths, String[] likes, String imagePath, String videoId) {
        this.channelId = channelId;
        this.title = title;
        this.description = description;
        this.view = view;
        this.videoPaths = videoPaths;
        this.likes = likes;
        this.imagePath = imagePath;
        this.videoId = videoId;
    }

    // Sort by videos' views descending
    public static Comparator<Video> VideoViewComparator = new Comparator<Video>() {
        @Override
        public int compare(Video o1, Video o2) {
            return o2.getView() - o1.getView();
        }
    };

    public static Comparator<Video> VideoViewComparator_byName = new Comparator<Video>() {
        @Override
        public int compare(Video o1, Video o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
    };

    public static Comparator<Video> VideoViewComparator_byLike = new Comparator<Video>() {
        @Override
        public int compare(Video o1, Video o2) {
            return (o2.getLengthLike() - o1.getLengthLike());
        }
    };
}
