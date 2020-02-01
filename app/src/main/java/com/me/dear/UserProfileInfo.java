package com.me.dear;

import android.opengl.Visibility;
import android.view.View;

public class UserProfileInfo {
    private String title,info;

    public UserProfileInfo(String title, String info){
        this.title = title;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
