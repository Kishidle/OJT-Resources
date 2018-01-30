package com.example.user.mpandroidcharttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.user.mpandroidcharttest.Model.Child;
import com.example.user.mpandroidcharttest.Model.ValueCounter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    private PieChart mPie1;
    private PieChart mPie2;
    private Spinner mSpin1;
    private Spinner mSpin2;
    private String mFilter1;
    private String mFilter2;
    private ArrayList<Child> childList;
    private Child childData1;
    private Child childData2;
    private Child childData3;
    private Button mUpdateBtn;
    private ArrayList<PieEntry> pieEntries1;
    private ArrayList<PieEntry> pieEntries2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO SQLite database or use the DatabaseHelper from GeeBee, whichever works better
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mPie1=(PieChart) findViewById(R.id.piechart1);
        mPie2=(PieChart) findViewById(R.id.piechart2);

        mSpin1=(Spinner) findViewById(R.id.chart1_spinner);
        mSpin2=(Spinner) findViewById(R.id.chart2_spinner);

        pieEntries1 = new ArrayList<>();
        pieEntries2 = new ArrayList<>();

        mFilter1 = "";
        mFilter2 = "";

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mFilter1 = mSpin1.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mFilter2 = mSpin1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.test_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpin1.setAdapter(adapter);




    }

    public void updateChart(){
        //update both charts based on selected item from respectivespinners
    }

    public void createData(){
        //hardcoded data for child class. ONLY FOR TESTING
        childList = new ArrayList<>();
        childData1 = new Child();
        childData2 = new Child();
        childData3 = new Child();

        //BMI is placeholder for now, TODO set calculated BMI values
        childData1.setParams("cabedg", 4, 4, 4, 4, 0, 15, 53, 164, 25);
        childData2.setParams("abcedg", 3, 2, 1, 2, 1, 10, 43, 161, 22);
        childData3.setParams("zxcvbg", 10, 9, 8, 7, 1, 8, 38, 146, 23);

        childList.add(childData1);
        childList.add(childData2);
        childList.add(childData3);




    }

    public void updateCharts(String mFilter, PieChart mPie, PieEntry pieEntry, ArrayList<Child> childList){
        //TODO create switch statements for mFilter

        switch(mFilter){

            case "Child ID":  break;
            case "Region": break;
            case "Province": break;
            case "Municipality": break;
            case "Barangay": break;
            case "Gender": break;
            case "Age":
                ValueCounter valueCounter = new ValueCounter(childList);

                break;
            case "Weight": break;
            case "Height": break;
            case "BMI":
                ValueCounter bmiCounter = new ValueCounter(childList);
                bmiCounter.setValBMI();
                //create piechart data

                break;
        }

    }

}
