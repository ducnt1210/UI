package com.example.ui.Model;

import java.util.ArrayList;

public class ExhibitModel {
    private String id;
    private AreaModel area;
    private String name;
    private ArrayList<String> image_path;
    private ArrayList<String> video_path;
    private String description;

    public ExhibitModel(String id, AreaModel area, String name, ArrayList<String> image_path, ArrayList<String> video_path, String description) {
        this.id = id;
        this.area = area;
        this.name = name;
        this.image_path = image_path;
        this.video_path = video_path;
        this.description = description;
    }

    public ExhibitModel(AreaModel area, String name, ArrayList<String> image_path, ArrayList<String> video_path, String description) {
        this.area = area;
        this.name = name;
        this.image_path = image_path;
        this.video_path = video_path;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AreaModel getArea() {
        return area;
    }

    public void setArea(AreaModel area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getImage_path() {
        return image_path;
    }

    public void setImage_path(ArrayList<String> image_path) {
        this.image_path = image_path;
    }

    public ArrayList<String> getVideo_path() {
        return video_path;
    }

    public void setVideo_path(ArrayList<String> video_path) {
        this.video_path = video_path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
