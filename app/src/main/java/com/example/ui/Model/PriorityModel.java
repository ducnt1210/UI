package com.example.ui.Model;

public class PriorityModel {
    private String uid, priority, userName, date, filePath;
    boolean isVerified;

    public PriorityModel() {

    }
    public PriorityModel(String uid, String priority, String userName, String date, String filePath, boolean isVerified) {
        this.uid = uid;
        this.priority = priority;
        this.userName = userName;
        this.date = date;
        this.filePath = filePath;
        this.isVerified = isVerified;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
