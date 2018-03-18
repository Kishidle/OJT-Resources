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
public class Child {
    
    private String childID;
    private ArrayList<String> childResponses;


    public Child(){


    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public ArrayList<String> getChildResponses() {
        return childResponses;
    }

    public void setChildResponses(ArrayList<String> childResponses) {
        this.childResponses = childResponses;
    }
}
