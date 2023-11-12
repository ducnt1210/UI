package com.example.ui.Model;


import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Formatter;

public class NotificationModel {
    private String id;
    private String image_path;
    private String description;
    private String user_id;
    private boolean seen;
    private Timestamp time;

    public NotificationModel() {

    }

    public NotificationModel(String id, String image_path, String description,
                             String user_id, boolean seen, Timestamp time) {
        this.id = id;
        this.image_path = image_path;
        this.description = description;
        this.user_id = user_id;
        this.seen = seen;
        this.time = time;
    }

    public NotificationModel(String image_path, String description,
                             String user_id, boolean seen, Timestamp time) {
        this.image_path = image_path;
        this.description = description;
        this.user_id = user_id;
        this.seen = seen;
        this.time = time;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_path() {
        return this.image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean getSeen() {
        return this.seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String formatDate(Timestamp timestamp) {
        Date date = timestamp.toDate();
        String pattern = "hh:mm dd/MM/yyyy";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
//        SimpleDateFormat sdf =
        DateFormat formatter = new SimpleDateFormat(pattern);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    public boolean isSameday(Date date1, Date date2) {
        return date1.getDate() == date2.getDate()
                && date1.getMonth() == date2.getMonth()
                && date1.getYear() == date2.getYear();
    }
}