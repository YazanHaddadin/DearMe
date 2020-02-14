package com.me.dear;

import java.util.ArrayList;

public class UserInformation {
    private String userEmail;
    private String userName;
    private String userGender;
    private String userDOB;

    public UserInformation() {

    }

    UserInformation(String userEmail, String userName, String userGender, String userDOB) {
        this.userEmail  = userEmail;
        this.userName   = userName;
        this.userGender = userGender;
        this.userDOB    = userDOB;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(String userDOB) {
        this.userDOB = userDOB;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
