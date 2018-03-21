package com.example.user.otherproject;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.user.otherproject.Model.Child;
import com.example.user.otherproject.Model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 1;
    private Uri uri;
    private String src;
    private ArrayList<Question> questionList;
    private ArrayList<Child> childListLeft, childListRight;
    private Question question;
    private String fileCSVLeft, fileCSVRight, featureCSV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        questionList = new ArrayList<>();
    }

    //TODO: get database and store, make UI a little bit better/presentable
    //TODO: get features from InitialVarDesc

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICKFILE_RESULT_CODE){
            if(resultCode == RESULT_OK){
                //do stuff
                //this is to get the filename of the datasets. not trimmed yet
                uri = data.getData();
                src = getPath(uri);
                Log.d("File URI: ", uri.toString());
                Log.d("filename", src);
            }
        }
    }
    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    public void launchChartActivity(View view){
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("listLeft", fileCSVLeft);
        intent.putExtra("listRight", fileCSVRight);
        intent.putExtra("featureList", featureCSV);
        //intent.putExtra("listLeft", childListLeft);
        //ntent.putExtra("listRight", childListRight);
        //intent.putExtra("questionList", questionList);
        startActivity(intent);
    }


    public void loadFileLeft(View view){
        /*
        Load hard-coded file from the left dataset button.
         */

        /*Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);*/
        fileCSVLeft = "female_p_test_num.csv";
        childListLeft = new ArrayList<>();
        prepareData(fileCSVLeft, childListLeft);

    }
    public void loadFileRight(View view){
        /*
        Loads hard-coded file from the right dataset button
         */
        fileCSVRight = "male_p_test_num.csv";
        childListRight = new ArrayList<>();
        prepareData(fileCSVRight, childListRight);
    }



    public void prepareData(String fileCSV, ArrayList<Child> childList){
        /*
        Prepares data coming from the left and right dataset buttons, and adds them to the
        corresponding arraylist.
         */

        AssetManager manager = this.getAssets();
        InputStream inStream = null;
        try{
            inStream = manager.open(fileCSV);
        } catch(IOException e){
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        boolean isHeader = true;
        try{
            while((line = buffer.readLine()) != null){
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

        Toast.makeText(MainMenu.this, "Added dataset!", Toast.LENGTH_LONG).show();

    }

    public void loadFileFeature(View view){
        /*
        Loads hard-coded file from the feature button. This function already prepares the Question
        arraylist.
         */
        //Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        //chooseFile.setType("*/*");
        //chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        //startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
        //TODO load files from cloud
        featureCSV = "InitialVarDescTempTemp.csv";
        AssetManager manager = this.getAssets();
        InputStream inStream = null;
        try{
            inStream = manager.open(featureCSV);
        } catch(IOException e){
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";

        boolean isFirst = true;
        try{
            while((line = buffer.readLine()) != null){
                Log.d("lineread", line);
                String[] col = line.split(",");
                Log.d("numberofcolumns", Integer.toString(col.length));

                if(col[0].trim().equals("^")){
                    // new class
                    if(!isFirst){
                        //add to arraylist
                        questionList.add(question);

                    }
                    isFirst = false;
                    question = new Question();
                    question.setQuestionLabel(col[1].trim());
                    question.setQuestionText(col[2].trim());
                    Log.d("questionNum", col[1].trim());

                }
                else{
                    //new features
                    //question.setFeatureGroup(col[0].trim());
                    //question.setFeatureNum(Integer.parseInt(col[1].trim()));
                    //question.setFeatureText(col[2].trim());
                }

            }
        } catch(IOException e){
            e.printStackTrace();
        }

        Toast.makeText(MainMenu.this, "Added feature dataset!", Toast.LENGTH_LONG).show();

    }

}
