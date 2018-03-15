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
    private Question question;
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
        startActivity(intent);
    }

    public void loadFile(View view){

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);

    }

    public void loadFileFeature(View view){
        //Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        //chooseFile.setType("*/*");
        //chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        //startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
        String featureCSV = "InitialVarDesc.csv";
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
                String[] col = line.split(",");

                if(col[0].trim().equals("^")){
                    // new class
                    if(!isFirst){
                        //add to arraylist
                        questionList.add(question);

                    }
                    isFirst = false;
                    question = new Question();
                    question.setQuestionNum(col[1].trim());
                    question.setQuestionText(col[2].trim());

                }
                else{
                    //new features
                    question.setFeatureGroup(col[0].trim());
                    question.setFeatureNum(Integer.parseInt(col[1].trim()));
                    question.setFeatureText(col[2].trim());
                }

            }
        } catch(IOException e){
            e.printStackTrace();
        }


    }

}
