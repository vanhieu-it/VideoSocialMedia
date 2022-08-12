package com.example.myapplication.Entities;
import java.util.*;

public class User implements Comparable<User>{
    String userID;
    String email;
    String name;
    String image;
    int totalLike;
    int totalCmt;
    public User(String userID, String email, String name,  String image, int totalLike) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.image = image;
        this.totalLike= totalLike;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTotalLike(){return totalLike;}

    public void setTotalLike(int totalLike){this.totalLike=totalLike;}

    @Override
    public int compareTo(User user) {
        if (totalLike == user.totalLike)
            return 0;
        else if (totalLike > user.totalLike)
            return -1;
        else
            return 1;
    }
}
