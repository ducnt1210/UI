package com.example.ui.Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Timer;

public class NewsModel {
    private String id;
    private String title;
    private ArrayList<String> image_path;
    private Timestamp time;
    private String description;

    public NewsModel(String id, String title, ArrayList<String> image_path, Timestamp time, String description) {
        this.id = id;
        this.title = title;
        this.image_path = image_path;
        this.time = time;
        this.description = description;
    }

    public NewsModel(String title, ArrayList<String> image_path, Timestamp time, String description) {
        this.title = title;
        this.image_path = image_path;
        this.time = time;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getImage_path() {
        return image_path;
    }

    public void setImage_path(ArrayList<String> image_path) {
        this.image_path = image_path;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
