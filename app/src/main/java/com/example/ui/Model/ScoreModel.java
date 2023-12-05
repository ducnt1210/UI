package com.example.ui.Model;

public class ScoreModel {
    String id; //user_id = id
    private int score;

    public ScoreModel() {
        this.id = "";
        this.score = 0;
    }

    public ScoreModel(String id, int score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
