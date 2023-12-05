package com.example.ui.Model;

import com.google.firebase.Timestamp;

public class TransactionModel {
    String ID;
    Timestamp timestamp;
    Timestamp usedTimestamp;
    long numberOfTickets;
    String token;
    long amount;
    boolean used;
    String UserID;

    public TransactionModel() {
    }

    public TransactionModel(String ID, Timestamp timestamp, Timestamp usedTimestamp, long numberOfTickets, String token, long amount, boolean used, String userID) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.usedTimestamp = usedTimestamp;
        this.numberOfTickets = numberOfTickets;
        this.token = token;
        this.amount = amount;
        this.used = used;
        UserID = userID;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Timestamp getUsedTimestamp() {
        return usedTimestamp;
    }

    public void setUsedTimestamp(Timestamp usedTimestamp) {
        this.usedTimestamp = usedTimestamp;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(long numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
