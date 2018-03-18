/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ootodeletenonyesno;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class Question {
    
    private String questionText;
    private String questionNum;
    private ArrayList<String> featureGroup;
    private ArrayList<Integer> featureNum;
    private ArrayList<String> featureText;

    public Question(){
        featureGroup = new ArrayList<String>();
        featureNum = new ArrayList<Integer>();
        featureText = new ArrayList<String>();
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

    public void addFeatureGroup(String feature){
        featureGroup.add(feature);
    }
    public void addFeatureNum(int featureNum){
        this.featureNum.add(featureNum);
    }
    public void addFeatureText(String featureText){
        this.featureText.add(featureText);
    }

    public ArrayList<String> getFeatureGroup() {
        return featureGroup;
    }

    public ArrayList<Integer> getFeatureNum() {
        return featureNum;
    }

    public ArrayList<String> getFeatureText() {
        return featureText;
    }
}
