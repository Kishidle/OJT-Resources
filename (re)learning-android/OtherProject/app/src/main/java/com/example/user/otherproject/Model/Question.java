package com.example.user.otherproject.Model;

/**
 * Created by Ramon on 3/12/2018.
 */

public class Question {

    private String questionText;
    private String questionNum;
    private String featureGroup;
    private int featureNum;
    private String featureText;

    public Question(){

    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(String questionNum) {
        this.questionNum = questionNum;
    }

    public String getFeatureGroup() {
        return featureGroup;
    }

    public void setFeatureGroup(String featureGroup) {
        this.featureGroup = featureGroup;
    }

    public int getFeatureNum() {
        return featureNum;
    }

    public void setFeatureNum(int featureNum) {
        this.featureNum = featureNum;
    }

    public String getFeatureText() {
        return featureText;
    }

    public void setFeatureText(String featureText) {
        this.featureText = featureText;
    }
}
