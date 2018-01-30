package com.example.user.mpandroidcharttest.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ramon on 1/30/2018.
 */

public class ValueCounter {

    private int[] valAge = {0, 0}; // <=12 & >12
    private int[] valBMI = {0, 0, 0, 0}; //Underweight, Normal, Overweight, Obese
    private ArrayList<Child> childList;

    public ValueCounter(ArrayList<Child> childList){
        this.childList = childList;

        //setValBMI();
        //setVacc();

    }

    public void setValBMI(){
        String result;
        for(int i = 0; i < childList.size(); i++){
            result = calculateBMI(childList.get(i).getcBMI());
            switch(result){
                case "Underweight": valBMI[0]++;
                    break;
                case "Normal": valBMI[1]++;
                    break;
                case "Overweight": valBMI[2]++;
                    break;
                case "Obese": valBMI[3]++;
                    break;
            }
        }
    }

    public int[] getValBMI(){
        return valBMI;
    }

    public String calculateBMI(float cBMI){

        if(cBMI < 18.5){
            return "Underweight";
        }
        else if(cBMI >= 18.5 && cBMI <= 24.9){
            return "Normal";
        }
        else if(cBMI >= 25 && cBMI<=29.9){
            return "Overweight";
        }

        else if(cBMI > 29.9){
            return "Obese";
        }

        return "N/A";


    }
}
