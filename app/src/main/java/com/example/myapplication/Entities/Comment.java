package com.example.myapplication.Entities;

public class Comment {
    String googleID;
    String videoID;
    String content;
    String[] like;
    String commentID;
    User owner;

    public Comment(String googleID, String videoID, String content, String[] like, String commentID, User owner) {
        this.googleID = googleID;
        this.videoID = videoID;
        this.content = content;
        this.like = like;
        this.commentID = commentID;
        this.owner = owner;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getLike() {
        return like;
    }

    public void setLike(String[] like) {
        this.like = like;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
