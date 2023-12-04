package com.example.ui.Model;

import java.util.ArrayList;
import java.util.List;

public class QuizModel {
    private String id;
    private String question;
    private List<String> answer;
    private String true_answer;
    private String detailed_answer;

    public QuizModel() {
        id = "";
        question = "";
        answer = new ArrayList<>();
        true_answer = "";
        detailed_answer = "";
    }

    public QuizModel(String id, String question, List<String> answer, String true_answer, String detailed_answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.true_answer = true_answer;
        this.detailed_answer = detailed_answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public String getTrue_answer() {
        return true_answer;
    }

    public void setTrue_answer(String true_answer) {
        this.true_answer = true_answer;
    }

    public String getDetailed_answer() {
        return detailed_answer;
    }

    public void setDetailed_answer(String detailed_answer) {
        this.detailed_answer = detailed_answer;
    }
}
