package com.proj.avatar.entity;

import android.graphics.Color;
import android.view.TextureView;


public class User {
    public String userName;
    public String userId;
    public String roomId;
    public String streamId;
    public boolean isCreator = false;


    public User(String userName, String userId) {
        this.userName = userName;
        this.userId = userId;
    }

}
