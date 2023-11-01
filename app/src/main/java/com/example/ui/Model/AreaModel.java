package com.example.ui.Model;

import java.util.ArrayList;

public class AreaModel {
    private String id;

    private  String name;
    private ArrayList<ExhibitModel> exhibits;

    private ArrayList<String> image_path;
    private ArrayList<String> video_path;
    private String description;

    public AreaModel() {

    }

    public AreaModel(String id, String name, ArrayList<ExhibitModel> exhibits, ArrayList<String> image_path, ArrayList<String> video_path, String description) {
        this.id = id;
        this.name = name;
        this.exhibits = exhibits;
        this.image_path = image_path;
        this.video_path = video_path;
        this.description = description;
    }

    public AreaModel(String name, ArrayList<ExhibitModel> exhibits, ArrayList<String> image_path, ArrayList<String> video_path, String description) {
        this.name = name;
        this.exhibits = exhibits;
        this.image_path = image_path;
        this.video_path = video_path;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ExhibitModel> getExhibits() {
        return exhibits;
    }

    public ArrayList<String> getImage_path() {
        return image_path;
    }

    public ArrayList<String> getVideo_path() {
        return video_path;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExhibits(ArrayList<ExhibitModel> exhibits) {
        this.exhibits = exhibits;
    }

    public void setImage_path(ArrayList<String> image_path) {
        this.image_path = image_path;
    }

    public void setVideo_path(ArrayList<String> video_path) {
        this.video_path = video_path;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
