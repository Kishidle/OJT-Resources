package com.example.user.mpandroidcharttest;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.mpandroidcharttest.Controller.DBHelper;
import com.example.user.mpandroidcharttest.Model.Child;
import com.example.user.mpandroidcharttest.Model.ValueCounter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChartActivity extends AppCompatActivity {

    private PieChart mPieLeft;
    private PieChart mPieRight;
    private LineChart lineChart;
    private BarChart barChart;
    private Spinner mSpin1, mSpin2, mChartSpin, mLeftFilterSpin, mRightFilterSpin;
    private String mFilter1, mFilter2, mChartSelected, mLeftFilter, mRightFilter;

    private ArrayList<Child> childList;
    private ArrayList<Child> filteredList;
    private Child childData1, childData2, childData3, childData4, childData5;
    private Button mUpdateBtn;
    private ArrayList<PieEntry> pieEntries1;
    private ArrayList<PieEntry> pieEntries2;
    private RelativeLayout graphLayoutLeft, graphLayoutRight;
    private String xData, xDataRight;
    private int[] yDataLeft, yDataRight;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private int leftFilterNum, rightFilterNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO SQLite database or use the DatabaseHelper from GeeBee, whichever works better
        //TODO relative layout instead of static charts in activity, so that charts can be added. check geebee source code on how -- done
        //TODO replace spinner values with column headers from the synthetic dataset
        //TODO edit Child class to completely include the synthetic dataset

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //mPie1=(PieChart) findViewById(R.id.piechart1);
        //mPie2=(PieChart) findViewById(R.id.piechart2);

        graphLayoutLeft = (RelativeLayout) findViewById(R.id.graph_container_left);
        graphLayoutRight = (RelativeLayout) findViewById(R.id.graph_container_right);

        mSpin1=(Spinner) findViewById(R.id.chart1_spinner);
        mSpin2=(Spinner) findViewById(R.id.chart2_spinner);
        mChartSpin=(Spinner) findViewById(R.id.chartListSpinner);
        mLeftFilterSpin = (Spinner) findViewById(R.id.leftfilter_spinner);
        mRightFilterSpin = (Spinner) findViewById(R.id.rightfilter_spinner);


        pieEntries1 = new ArrayList<>();
        pieEntries2 = new ArrayList<>();
        childList = new ArrayList<>();
        filteredList = new ArrayList<>();

        mFilter1 = "";
        mFilter2 = "";
        mChartSelected = "";
        mLeftFilter = "All";
        mRightFilter = "All";

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.test_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpin1.setAdapter(adapter);
        mSpin2.setAdapter(adapter);

        ArrayAdapter<CharSequence> chartAdapter = ArrayAdapter.createFromResource(this, R.array.chart_array, android.R.layout.simple_spinner_item);
        mChartSpin.setAdapter(chartAdapter);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this, R.array.filter_array, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLeftFilterSpin.setAdapter(filterAdapter);
        mRightFilterSpin.setAdapter(filterAdapter);

        //mDBHelper = new DBHelper(this);

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

        mLeftFilterSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                Log.d("leftfilter", "it went here lol");
                mLeftFilter = mLeftFilterSpin.getSelectedItem().toString();
                if(!mLeftFilter.equals("All")){
                    /*
                    Pattern p = Pattern.compile("\"-?\\\\d+");
                    Matcher m = p.matcher(mLeftFilter);
                    Log.d("leftfiltertest", mLeftFilter);
                    while(m.find()){
                        leftFilterNum = Integer.parseInt(m.group());
                    }*/
                    leftFilterNum = i;
                    Log.d("leftfilternum", Integer.toString(leftFilterNum));

                }
                prepareChart();

            }

            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });

        mRightFilterSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

                mRightFilter = mRightFilterSpin.getSelectedItem().toString();
                if(!mRightFilterSpin.getSelectedItem().toString().equals("All")){
                    /*
                    Pattern p = Pattern.compile("\"-?\\\\d+");
                    Matcher m = p.matcher(mRightFilterSpin.getSelectedItem().toString());
                    while(m.find()){
                        rightFilterNum = Integer.parseInt(m.group());
                    }
                    */
                    rightFilterNum = i;

                }
                prepareChart();

            }

            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });



        //TODO MOST UPDATED TEST SQLITEDATABASE AND DO FILTERS
        //TODO add function to choose which type of chart to use. default = pie chart. also add filtering by region, etc.


        importCSV();


        mFilter1 = "BMI";
        Log.d("mfiltertest", mFilter1);
        Log.d("hitest", "testing it started here");
        createCharts();
        prepareChartData();

        mChartSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                //adding charts to the two RelativeLayouts
                mChartSelected = mChartSpin.getSelectedItem().toString();
                ViewGroup.LayoutParams paramsLeft, paramsRight;
                graphLayoutLeft.removeAllViews();
                graphLayoutRight.removeAllViews();

                if(mChartSelected.equals("Pie Chart")){
                    graphLayoutLeft.addView(mPieLeft);
                    graphLayoutRight.addView(mPieRight);

                    //adjust the size
                    Log.d("charttag", "did it go here?");
                    paramsLeft = mPieLeft.getLayoutParams();
                    paramsRight = mPieRight.getLayoutParams();

                    paramsRight.height = 400;
                    paramsRight.width = 400;
                }
                else{
                    graphLayoutLeft.addView(barChart);
                    paramsLeft = barChart.getLayoutParams();
                }

                if(!mChartSelected.equals("Pie Chart")){
                    graphLayoutRight.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 0f));
                }
                else{
                    //graphLayoutRight.setLayoutParams(new LinearLayout.LayoutParams(400, 400, 1f));
                }

                paramsLeft.width = 400;
                paramsLeft.height = 400;

                prepareChart();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

                mChartSelected = "Pie Chart";
                graphLayoutLeft.addView(mPieLeft);
                graphLayoutRight.addView(mPieRight);

                ViewGroup.LayoutParams params = mPieLeft.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;

                ViewGroup.LayoutParams paramsRight = mPieRight.getLayoutParams();
                paramsRight.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;

                Log.d("onnothing", "test");

                prepareChart();
            }

        });



    }

    public void createData(){
        //hardcoded data for child class. ONLY FOR TESTING
        childList = new ArrayList<>();
        childData1 = new Child();
        childData2 = new Child();
        childData3 = new Child();
        childData4 = new Child();
        childData5 = new Child();

        //BMI is placeholder for now, TODO set calculated BMI values
        childData1.setParams("cabedg", 4, 4, 4, 4, 0, 15, 53, 164, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0 , 0, 0, 0);
        //childData2.setParams("abcedg", 3, 2, 1, 2, 1, 10, 43, 161, 22);
        //childData3.setParams("zxcvbg", 10, 9, 8, 7, 1, 8, 38, 146, 23);
        //childData4.setParams("asdasd", 5, 4, 2, 1, 0, 10, 38, 150, 17);
        //childData5.setParams("zxczcx", 2, 3, 2, 3, 1, 11, 12, 130, 50);

        childList.add(childData1);
        //childList.add(childData2);
        //childList.add(childData3);
        //childList.add(childData4);
        //childList.add(childData5);

    }

    public void importCSV(){
        //TODO add import csv
        String mCSVfile = "syndata.csv";
        AssetManager manager = this.getAssets();
        InputStream inStream = null;
        try{
            inStream = manager.open(mCSVfile);
        } catch(IOException e){
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));

        String line = "";
        int i = 0;
        try{
            while((line = buffer.readLine()) != null){

                if(i == 0){
                    i++;
                }
                else{
                    String[] col = line.split(",");
                    Log.d("columntest", line);
                    Child child = new Child();

                    child.setChildID(col[0].trim());
                    child.setRegionNum(Integer.parseInt(col[1].trim()));
                    child.setProvinceNum(Integer.parseInt(col[2].trim()));
                    child.setMunicipalNum(Integer.parseInt(col[3].trim()));
                    child.setBarangayNum(Integer.parseInt(col[4].trim()));
                    child.setcGender(Integer.parseInt(col[5].trim()));
                    child.setcAge(Integer.parseInt(col[6].trim()));
                    child.setcWeight(Float.parseFloat(col[7].trim()));
                    child.setcHeight(Float.parseFloat(col[8].trim()));
                    child.setcBMI(Float.parseFloat(col[9].trim()));
                    child.setcVaccPol(Integer.parseInt(col[10].trim()));
                    child.setcVaccTeta(Integer.parseInt(col[11].trim()));
                    child.setcEyes(Integer.parseInt(col[12].trim()));
                    child.setcEyeColorTest(Integer.parseInt(col[13].trim()));
                    child.setcHearing(Integer.parseInt(col[14].trim()));
                    child.setcFineMotor(Integer.parseInt(col[15].trim()));
                    child.setcGrossMotor(Integer.parseInt(col[16].trim()));
                    child.setcMental1(Integer.parseInt(col[17].trim()));
                    child.setcMental2(Integer.parseInt(col[18].trim()));
                    child.setcMental3(Integer.parseInt(col[19].trim()));
                    child.setcMental4(Integer.parseInt(col[20].trim()));
                    child.setcMental5(Integer.parseInt(col[21].trim()));
                    //mDBHelper.addChild(child);
                    childList.add(child);

                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    public void createCharts(){
        mPieLeft = createPieChart();
        mPieRight = createPieChart();
        barChart = createBarChart();
        lineChart = createLineChart();
    }

    public void updateChartButton(View view){
        //TODO create switch statements for mFilter
        //TODO check mPieChart, I think that's why it's not working. not using the actual pie chart in the layout
        //TODO either remove button so that it updates when you change the spinner, or make it so that the invalidates work here and the computations are elsewhere
        //mPie1 = createPieChart(mPie1);
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
                preparePieChartData(mPieLeft, bmiCount); //or do this in populatePie?

                Log.d("test2", "did it go here?");
                break;
        }

        mPieLeft.animateY(1000);
        Description description = new Description();
        description.setText("BMI distribution");
        mPieLeft.setDescription(description);
        mPieLeft.invalidate();


        mPieRight.invalidate();
        mPieRight.animateY(1000);
        //mPie1.animateY(3000, Easing.EasingOption.EaseInOutElastic);


    }

    private void prepareChartData(){

        ValueCounter vCounterLeft = new ValueCounter(childList);
        vCounterLeft.setValBMI();
        ValueCounter vCounterRight = new ValueCounter(childList);
        vCounterRight.setValBMI();

        //basically use xData, yDataLeft, and yDataRight and get the values from the filters
        switch(mFilter1){

            case "BMI":

                //xData = vCounterLeft.getLabelBMI();
                yDataLeft = vCounterLeft.getValBMI();
                yDataRight = vCounterRight.getValBMI();



                break;
            case "Vaccination": break;
            case "test": break;
        }


    }

    private void prepareChart(){
        if(mChartSelected.equals("Pie Chart")){
            //Log.d("preparecharttest", Integer.toString(childList.get(1).getcAge()));
            if(mLeftFilter.equals("All")){
                preparePieChartData(mPieLeft, yDataLeft);
            }
            else{
                filteredList = filterChild("left");
                Log.d("preparecharttest", "did it go here");
                ValueCounter vc = new ValueCounter(filteredList);
                vc.setValBMI();
                int[] count = vc.getValBMI();
                preparePieChartData(mPieLeft, count);

            }
            if(mRightFilter.equals("All")){
                preparePieChartData(mPieRight, yDataRight);
            }
            else{
                filteredList = filterChild("right");
                ValueCounter vc = new ValueCounter(filteredList);
                vc.setValBMI();
                int[] count = vc.getValBMI();
                preparePieChartData(mPieRight, count);
            }
            //preparePieChart(mPieRight);
        }
        else if(mChartSelected.equals("Bar Chart")){
            prepareBarChartData(barChart, yDataLeft);
        }
        else if(mChartSelected.equals("Line Chart")){
            prepareLineChartData(lineChart, yDataLeft);
        }

    }

    private ArrayList<Child> filterChild(String position){
        filteredList = new ArrayList<>();
        int regionNum;
        if(position.equals("left")){
            regionNum = leftFilterNum;
        }
        else regionNum = rightFilterNum;

        for(int i = 0; i < childList.size(); i++){
            if(regionNum == childList.get(i).getRegionNum()){
                filteredList.add(childList.get(i));
            }
        }

        return filteredList;


    }

    private LineChart createLineChart(){

        lineChart = new LineChart(this);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(false);

        return lineChart;

    }

    private void prepareLineChartData(LineChart lineChart, int[] valueCount){

        List<Entry> lineEntries = new ArrayList<>();
        ArrayList<Float> lineValues = computeValue(valueCount);

        for(int i = 0; i < lineValues.size(); i++){
            Log.d("linevalue", lineValues.get(i).toString());
            lineEntries.add(new Entry(lineValues.get(i), i));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "test");
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

    }

    private PieChart createPieChart(){

        PieChart pieChart = new PieChart(this);
        // configure pie chart
        pieChart.setUsePercentValues(true);

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.BLUE);
        pieChart.setHoleRadius(45);
        //pieChart.setTransparentCircleRadius(100);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        pieChart.setTransparentCircleRadius(50f);

        // set a chart value selected listener
        //pieChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());

        // customize legends
        Legend l = pieChart.getLegend();

        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        l.setWordWrapEnabled(true);
        //l.setTextSize(R.dimen.context_text_size);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        return pieChart;
    }



    private BarChart createBarChart(){

        barChart = new BarChart(this);
        barChart.setDrawValueAboveBar(true);

        return barChart;
    }

    private void prepareBarChartData(BarChart barChart, int[] valueCount){

        List<BarEntry> entries = new ArrayList<>();

        ArrayList<Float> barValues = computeValue(valueCount);

        Log.d("piecharttest", "test1");

        for(int i = 0; i < barValues.size(); i++){

            Log.d("Bar Values", barValues.get(i).toString());
            entries.add(new BarEntry(barValues.get(i), i));
        }
        BarDataSet barDataSet = new BarDataSet(entries, "BMI distribution");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.invalidate();

    }

    private void preparePieChartData(PieChart pieChart, int[] valueCount){

        List<PieEntry> entries = new ArrayList<>();


        //compute values for PieChart first
        ArrayList<Float> pieValues = computeValue(valueCount);

        String[] test = {"Underweight", "Normal", "Overweight", "Obese"};

        for(int i = 0; i < pieValues.size(); i++){
            Log.d("Pie Values", pieValues.get(i).toString());
            entries.add(new PieEntry(pieValues.get(i), test[i]));
            //entries.add(new PieEntry(valueCount[i]))
        }
        PieDataSet set = new PieDataSet(entries, "BMI distribution");


        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setSliceSpace(4f);

        //value line test
        //set.setValueLinePart1OffsetPercentage(80.f);
        //set.setValueLinePart1Length(0.4f);
        //set.setValueLinePart2Length(0.4f);
        //set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();



    }

    private ArrayList computeValue(int[] valueCount){

        int totalCount = 0;

        //int sliceCount = valueCount.length;
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
