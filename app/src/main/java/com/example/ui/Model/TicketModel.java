package com.example.ui.Model;

import com.google.firebase.Timestamp;

public class TicketModel {
    String ID;
    Timestamp buyTimestamp;
    Timestamp usedTimestamp;
    long price;
    String UserID;
    String transactionID;

    int amount;

    public TicketModel() {
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public TicketModel(String ID, Timestamp buyTimestamp, Timestamp usedTimestamp, long price, String userID, String transactionID, int amount) {
        this.ID = ID;
        this.buyTimestamp = buyTimestamp;
        this.usedTimestamp = usedTimestamp;
        this.price = price;
        UserID = userID;
        this.transactionID = transactionID;
        this.amount = amount;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Timestamp getBuyTimestamp() {
        return buyTimestamp;
    }

    public void setBuyTimestamp(Timestamp buyTimestamp) {
        this.buyTimestamp = buyTimestamp;
    }

    public Timestamp getUsedTimestamp() {
        return usedTimestamp;
    }

    public void setUsedTimestamp(Timestamp usedTimestamp) {
        this.usedTimestamp = usedTimestamp;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
