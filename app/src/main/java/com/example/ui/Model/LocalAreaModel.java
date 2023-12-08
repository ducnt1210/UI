package com.example.ui.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class LocalAreaModel implements Serializable {
    private String id;

    private String name;
    private ArrayList<String> exhibits;
    private ArrayList<String> description;

    public LocalAreaModel() {

    }

    public LocalAreaModel(String id, String name, ArrayList<String> exhibits, ArrayList<String> description) {
        this.id = id;
        this.name = name;
        this.exhibits = exhibits;
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


    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }
}