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
    private ArrayList<Child> childListLeft, childListRight;

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
                    isHeader = false;
                    question = new Question();
                    question.setQuestionNum(col[1].trim());
                    question.setQuestionText(col[2].trim());
                }
                else{
                    question.addFeatureGroup(col[0].trim());
                    question.addFeatureNum(Integer.parseInt(col[1].trim()));
                    question.addFeatureText(col[2].trim());
                }

            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void prepareDataset(String fileCSV, ArrayList<Child> childList){
        
        InputStream inStream = null;
        File file = new File(fileCSV);
        String line = "";
        boolean isHeader = true;
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null){
                if(isHeader){
                    isHeader = false;
                }
                else{
                    String[] col = line.split(",");
                    Child child = new Child();
                    child.setChildID(col[0].trim());
                    
                    ArrayList<String> childResponses = new ArrayList<>();
                    for(int i = 1; i < col.length; i++){
                        childResponses.add(col[i].trim());
                    }
                    child.setChildResponses(childResponses);
                    childList.add(child);
                }
            }   
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void trimPopulation(){
        
    }
}
