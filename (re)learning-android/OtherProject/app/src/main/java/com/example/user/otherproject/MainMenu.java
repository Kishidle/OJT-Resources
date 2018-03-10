package com.example.user.otherproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    //TODO: get database and store, make UI a little bit better/presentable

    public void launchChartActivity(View view){
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }
}
