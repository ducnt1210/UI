package com.example.ui.Model;

import java.util.ArrayList;

public class ExhibitModel {
    private String id;
    private String parentId;
    private String name;
    private ArrayList<String> description;

    public ExhibitModel(String id, String parentId, String name, ArrayList<String> description) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }
}
