package com.example.discovermada.model;

import com.example.discovermada.utils.Constant;

public class TouristSpots {

    private String _id;
    private String name;
    private String description;
    private Double [] location;
    private String[] images;
    private String[] videos;
    private String htmlContent;

    public TouristSpots() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }

    public String[] getImages()
    {
        String[] newUrl = new String[images.length];
        for (int i = 0 ; i<images.length ;i++){
            newUrl[i] = Constant.PATH_IMG_FB+"/"+images[i];
        }
        return newUrl;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getVideos() {
        return videos;
    }

    public void setVideos(String[] videos) {
        this.videos = videos;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
