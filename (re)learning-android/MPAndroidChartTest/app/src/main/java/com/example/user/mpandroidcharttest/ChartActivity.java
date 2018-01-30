package com.example.user.mpandroidcharttest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.user.mpandroidcharttest.Model.Child;
import com.example.user.mpandroidcharttest.Model.ValueCounter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

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
        mSpin2.setAdapter(adapter);

        createData();



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

    public void updateChartButton(View view){
        //TODO create switch statements for mFilter
        //TODO check mPieChart, I think that's why it's not working. not using the actual pie chart in the layout
        PieChart pieChart = createPieChart();
        Log.d("test", mFilter1);


        //turn this into a function?
        switch(mFilter1){
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
                int[] bmiCount = bmiCounter.getValBMI();

                //create PieChart data

                String[] test = {"test1", "test2", "test3", "test4"};
                PieData data = populatePie(pieChart, bmiCount, test); //or do this in populatePie?
                pieChart.setData(data);
                Log.d("test2", "did it go here?");
                break;
        }
        pieChart.invalidate();

    }

    private PieChart createPieChart(){

        PieChart pieChart = new PieChart(this);

        // configure pie chart
        pieChart.setUsePercentValues(true);

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(100);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        // set a chart value selected listener
        //pieChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());

        // customize legends
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setWordWrapEnabled(true);
        //l.setTextSize(R.dimen.context_text_size);
        l.setTextColor(Color.WHITE);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        return pieChart;



    }

    private PieData populatePie(PieChart pieChart, int[] valueCount, String[] valueLabel){

        List<PieEntry> entries = new ArrayList<>();


        //compute values for PieChart first
        ArrayList<Float> pieValues = computeValue(valueCount);

        for(int i = 0; i < pieValues.size(); i++){
            entries.add(new PieEntry(pieValues.get(i)));
            //entries.add(new PieEntry(valueCount[i]))
        }
        PieDataSet set = new PieDataSet(entries, "test");
        PieData data = new PieData(set);
        return data;


    }

    private ArrayList computeValue(int[] valueCount){

        int totalCount = 0;
        int sliceCount = valueCount.length;
        ArrayList<Float> computedValues = new ArrayList<>();
        //more efficient way of doing this?
        for(int i = 0; i < valueCount.length; i++){

            totalCount += valueCount[i];
        }

        for(int i = 0; i < valueCount.length; i++){
            computedValues.add((float)valueCount[i] / totalCount * 10);
        }

        return computedValues;


    }
}
