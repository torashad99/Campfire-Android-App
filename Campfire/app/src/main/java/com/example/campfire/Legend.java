package com.example.campfire;

import com.google.firebase.firestore.GeoPoint;

public class Legend {
    String title;
    GeoPoint location;
    String type;
    String description;

    public Legend() {}

    public Legend(String title_, GeoPoint location_, String type_, String description_)
    {
        if (title_.isEmpty()) throw new IllegalArgumentException("Title is required");
        if (location_ == null) throw new IllegalArgumentException("Location can't be null");

        title = title_;
        location = location_;
        type = type_;
        description = description_;
    }

    public String getTitle() {
        return title;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

}
