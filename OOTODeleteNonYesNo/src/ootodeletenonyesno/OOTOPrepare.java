/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ootodeletenonyesno;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class OOTOPrepare {

    private ArrayList<Question> questionList;
    private Question question;

    public OOTOPrepare(){

        initialize();
    }

    public void initialize(){

        prepareFeature();

    }

    public void prepareFeature(){

        System.out.println("test");
        InputStream inStream = null;
        File file = new File("InitialVarDescTest.csv");
        String line = "";
        boolean isHeader = true;
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));

            while((line = br.readLine()) != null){

                String col[] = line.split(",");
                if(col[0].trim().equals("^")){
                    if(!isHeader){
                        questionList.add(question);
                    }
                }

            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
