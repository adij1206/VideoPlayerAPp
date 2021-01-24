package com.aditya.videoplayer;

public class Video {
    private String title;
    private String id;

    public Video(){}

    public Video(String title, String id) {
        this.title = title;

        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getId() {
        return id;
    }

    public void setId(String videoId) {
        this.id = id;
    }
}
