package com.example.user.mpandroidcharttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;

public class ChartActivity extends AppCompatActivity {

    private PieChart mPie1;
    private PieChart mPie2;
    private Spinner mSpin1;
    private Spinner mSpin2;
    private String mString1;
    private String mString2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO SQLite database or use the DatabaseHelper from GeeBee, whichever works better
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mPie1=(PieChart) findViewById(R.id.piechart1);
        mPie2=(PieChart) findViewById(R.id.piechart2);

        mSpin1=(Spinner) findViewById(R.id.chart1_spinner);
        mSpin2=(Spinner) findViewById(R.id.chart2_spinner);

        mString1 = "";
        mString2 = "";

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mString1 = mSpin1.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mString2 = mSpin1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.test_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpin1.setAdapter(adapter);


    }
}
