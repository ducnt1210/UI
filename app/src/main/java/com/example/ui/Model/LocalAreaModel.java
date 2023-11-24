package com.example.ui.Model;

import java.util.ArrayList;

public class LocalAreaModel {
    private String id;

    private String name;
    private ArrayList<String> exhibits;
    private String parentId;
    private ArrayList<String> description;

    public LocalAreaModel() {

    }

    public LocalAreaModel(String id, String name, ArrayList<String> exhibits, String parentId, ArrayList<String> description) {
        this.id = id;
        this.name = name;
        this.exhibits = exhibits;
        this.parentId = parentId;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getExhibits() {
        return exhibits;
    }

    public void setExhibits(ArrayList<String> exhibits) {
        this.exhibits = exhibits;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }
}