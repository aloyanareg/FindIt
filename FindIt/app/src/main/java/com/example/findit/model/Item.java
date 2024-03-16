package com.example.findit.model;

public class Item {
    private String title, color, location, ownerID, photoUrl;
    private boolean isLost;

    public Item() {
    }

    public Item(String title, String color, String location, String ownerID, boolean isLost,String  photoUrl) {
        this.title = title;
        this.color = color;
        this.location = location;
        this.ownerID = ownerID;
        this.isLost = isLost;
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLocation() {
        return location;
    }
    public String getPhotoUrl(){return photoUrl;}
    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean lost) {
        isLost = lost;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
