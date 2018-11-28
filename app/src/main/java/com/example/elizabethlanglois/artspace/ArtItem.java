package com.example.elizabethlanglois.artspace;

import android.graphics.Bitmap;

public class ArtItem {

    public String title, location, description, type;
    public String date, time, contact;
    public String drawing;

    public ArtItem() {
        // No arg constructor for firebase
    }

    public ArtItem(String title, String location, String description) {
        this.title = title;
        this.location = location;
        this.description = description;
    }

    public void setDate(String date) {
        this.date=date;
    }

    public void setTime(String time) {
        this.time=time;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setType(String type) {
        this.type = type;
    }

}
