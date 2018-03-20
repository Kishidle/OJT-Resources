package com.example.user.otherproject.Model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ramon on 1/30/2018.
 */

public class ValueCounter {

    private int[] valResponse = {0, 0};
    private ArrayList<Child> childList;
    private int questionNum;

    public ValueCounter(ArrayList<Child> childList, int questionNum){
        this.childList = childList;
        this.questionNum = questionNum;
    }

    public void setResponse(){
        for(int i = 0; i < childList.size(); i++){
            Log.d("valuecounterchart", childList.get(i).getChildResponses().get(questionNum));
            if(childList.get(i).getChildResponses().get(questionNum).equals("a")){

                valResponse[0]++;
            }
            else if(childList.get(i).getChildResponses().get(questionNum).equals("b")){
                valResponse[1]++;
            }
        }
    }
    public int[] getResponse(){
        return valResponse;
    }
}
