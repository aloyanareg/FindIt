package com.example.findit.model;

public class Item {
    private String title, color, location, ownerID, photoUrl, description, ownerPhone;
    private boolean isLost;

    public Item() {
    }

    public Item(String title, String color, String location, String ownerID, boolean isLost,String  photoUrl, String description, String ownerPhone) {
        this.title = title;
        this.color = color;
        this.location = location;
        this.ownerID = ownerID;
        this.isLost = isLost;
        this.photoUrl = photoUrl;
        this.description = description;
        this.ownerPhone = ownerPhone;
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
    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}
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

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
}
