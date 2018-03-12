package com.example.user.otherproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    //TODO: get database and store, make UI a little bit better/presentable

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICKFILE_RESULT_CODE){
            if(resultCode == RESULT_OK){
                //do stuff
                //this is to get the filename of the datasets. not trimmed yet
                Uri uri = data.getData();
                String src = uri.getPath();
                Log.d("filename", src);
            }
        }
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

}
