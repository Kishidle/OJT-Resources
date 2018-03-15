package com.example.user.otherproject.Model;

import java.util.ArrayList;

/**
 * Created by Ramon on 1/30/2018.
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
