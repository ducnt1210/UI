package com.example.ui.Model;

import com.google.firebase.Timestamp;

public class TransactionModel {
    String ID;
    Timestamp timestamp;
    long numberOfTickets;
    String token;
    long amount;
    String UserID;

    public TransactionModel() {
    }

    public TransactionModel(String ID, Timestamp timestamp, long numberOfTickets, String token, long amount, String userID) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.numberOfTickets = numberOfTickets;
        this.token = token;
        this.amount = amount;
        UserID = userID;
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
