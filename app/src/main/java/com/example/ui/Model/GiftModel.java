package com.example.ui.Model;

public class GiftModel {
    private String id;
    private String name;
    private int price;
    private String image_path;

    public GiftModel() {
        name = "";
        price = 0;
        image_path = "";
        id = "";
    }

    public GiftModel(String id, String name, int price, String image_path) {
        this.name = name;
        this.price = price;
        this.image_path = image_path;
        this.id = id;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
