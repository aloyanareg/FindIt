package com.example.findit.model;

import java.util.List;

public class User {
    String userID, userName;
    List<String> hasChatWith;
    public User(){

    }
    public User(String userId,  List<String> hasChatWith, String userName) {
        this.userID = userId;
        this.hasChatWith = hasChatWith;
        this.userName = userName;
    }
    public String getUserID(){
        return userID;
    }
    public String getUserName(){
        return userName;
    }
    public List<String> getChatWith(){
        return hasChatWith;
    }
    public void setUserID(String userId){
        this.userID = userId;
    }
    public void setChatWith(List<String> hasChatWith){
        this.hasChatWith = hasChatWith;
    }
}
