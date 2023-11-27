package com.example.ui.Model;


import com.example.ui.Utils;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

public class NotificationModel {
    private String id;
    private String image_path;
    private List<String> description;
    private String user_id;
    private boolean seen;
    private boolean sentNotification;
    private Timestamp time;

    public NotificationModel() {
        this.id = "";
        this.image_path = "";
        this.description = new ArrayList<>();
        this.user_id = "";
        this.seen = true;
        this.sentNotification = true;
        this.time = Timestamp.now();
    }

    public NotificationModel(String id, String image_path, List<String> description,
                             String user_id, boolean seen, boolean sentNotification, Timestamp time) {
        this.id = id;
        this.image_path = image_path;
        this.description = description;
        this.user_id = user_id;
        this.seen = seen;
        this.time = time;
        this.sentNotification = sentNotification;
    }

    public NotificationModel(String image_path, List<String> description,
                             String user_id, boolean seen, boolean sentNotification, Timestamp time) {
        this.image_path = image_path;
        this.description = description;
        this.user_id = user_id;
        this.seen = seen;
        this.time = time;
        this.sentNotification = sentNotification;
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

    public List<String> getDescription() {
        return this.description;
    }

    public void setDescription(List<String> description) {
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

    public boolean getSentNotification() {
        return this.sentNotification;
    }

    public void setSentNotification(boolean sentNotification) {
        this.sentNotification = sentNotification;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String formatDate() {
        return Utils.formatDate(this.time);
    }

    public boolean isSameday(Date otherDay) {
        return Utils.isSameDay(this.time.toDate(), otherDay);
    }

    public String heading() {
        String result = "";
        if (this.description.size() > 0) {
            String des = this.description.get(0);
            if (des.startsWith("$heading$")) {
                result = result + des.substring("$heading$".length());
            } else if (des.startsWith("$note$")) {
                result = result + des.substring("$note$".length());
            } else if (des.startsWith("$imgs$") == false) {
                result = result + des;
            }
        }
        return result;
    }

    public String fullDescription() {
        String result = "";
        if (this.description.size() > 1) {
            for (int i = 1; i < this.description.size(); ++i) {
                String des = this.description.get(i);
                if (des.startsWith("$heading$")) {
                    result = result + des.substring("$heading$".length()) + "\n";
                } else if (des.startsWith("$note$")) {
                    result = result + des.substring("$note$".length()) + "\n";
                } else if (des.startsWith("$imgs$") == false) {
                    result = result + des + "\n";
                }
            }
        }
        return result;
    }
}