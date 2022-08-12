package com.example.myapplication.Entities;

public class Channel {
    private String channelName;
    private String userName;
    private String password;
    private String image;
    private String id;
    private Boolean subscribed;

    public Channel(){}

    public Channel(String channelName, String userName, String password, String image, String id, Boolean subscribed) {
        this.channelName = channelName;
        this.userName = userName;
        this.password = password;
        this.image = image;
        this.id = id;
        this.subscribed = subscribed;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setName(String name) {
        this.channelName = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }
}
