package com.example.ui.Model;

import com.example.ui.Utils;
import com.google.firebase.Timestamp;

import java.util.Date;

public class ExchangedGiftModel {
    private String id;
    private String user_id;
    private String name;
    private int price;
    private String image_path;
    private String status;
    private Timestamp time;

    public ExchangedGiftModel() {
        id = "";
        user_id = "";
        name = "";
        price = 0;
        image_path = "";
        status = "";
        time = new Timestamp(new Date());
    }

    public ExchangedGiftModel(String id, String user_id, String name,
                              int price, String image_path, String status, Timestamp time) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.price = price;
        this.image_path = image_path;
        this.status = status;
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String formatDate() {
        return Utils.formatDate(time);
    }
}
