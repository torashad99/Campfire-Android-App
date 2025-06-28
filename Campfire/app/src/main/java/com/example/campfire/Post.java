package com.example.campfire;

public class Post {
    String title; // optional, but recommended
    String content; // require

    String locationId;

//    PostType story_type; // optional
//    Image picture; // optional

    /* This is something to possibly implement.
        User can specify the kind of story / comment their telling.
        Story is a traditional remark / experience with that.
        Question is a question about the location.
        Fact is a verifiable piece of supplementary info
        Rumor is a non-verifiable piece of info
     */
//     enum PostType {
//        STORY,
//        QUESTION,
//        FACT,
//        RUMOR
//    }
    public Post() {}
    public Post(String title_, String content_, String location_id_) {
        if (content_.isEmpty()) throw new IllegalArgumentException("Post content is required");

        title = title_;
        content = content_;
        locationId = location_id_;
    }

    public Post(String title_, String content_) {
        if (content_.isEmpty()) throw new IllegalArgumentException("Post content is required");

        title = title_;
        content = content_;
        locationId = "";
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLocationId() { return locationId; }
}
