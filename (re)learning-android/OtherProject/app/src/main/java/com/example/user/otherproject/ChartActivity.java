package com.example.user.otherproject;

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
import android.widget.TextView;

import com.example.user.otherproject.Controller.DBHelper;
import com.example.user.otherproject.Model.Child;
import com.example.user.otherproject.Model.ValueCounter;
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
    private int leftFilterNum, rightFilterNum, questionNum;
    private TextView questionText;
    private View colorBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //TODO edit Child class to completely include the synthetic dataset
        //TODO next and previous buttons semi-done need to refresh/change the charts and stuff
        //TODO colored square box thing done
        //TODO back button to main menu done
        //TODO get database for sample and the features
        //TODO computations not started yet

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //mPie1=(PieChart) findViewById(R.id.piechart1);
        //mPie2=(PieChart) findViewById(R.id.piechart2);

        questionNum = 1;
        //preliminary set question text
        questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText("Question " + Integer.toString(questionNum));

        colorBar = (View) findViewById(R.id.colored_bar);

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
                //prepareChart();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mFilter2 = mSpin2.getSelectedItem().toString();
                //prepareChart();
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
                //prepareChart();

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
                //prepareChart();

            }

            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });



        //TODO MOST UPDATED TEST SQLITEDATABASE AND DO FILTERS
        //TODO add function to choose which type of chart to use. default = pie chart. also add filtering by region, etc.


        //importCSV();


        mFilter1 = "BMI";
        mFilter2 = "BMI";
        Log.d("mfiltertest", mFilter1);
        Log.d("hitest", "testing it started here");
        //createCharts();
        //prepareChartData();

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

                //prepareChart();

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

                //prepareChart();
            }

        });



    }



    public void createCharts(){
        mPieLeft = createPieChart();
        mPieRight = createPieChart();
    }


    

    /*private ArrayList<Child> filterChild(String position){
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


    }*/





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

    private void preparePieChartData(PieChart pieChart, int[] valueCount, String mFilter){

        List<PieEntry> entries = new ArrayList<>();


        //compute values for PieChart first
        ArrayList<Float> pieValues = computeValue(valueCount);
        String[] bmiString = {"underweight", "Normal", "Overweight", "Obese"};
        String[] vaccString = {"No Vaccination", "With Vaccination"};


        Log.d("valuesize", Integer.toString(pieValues.size()));
        Log.d("valuetag", mFilter);
        for(int i = 0; i < pieValues.size(); i++){
            Log.d("Pie Values", pieValues.get(i).toString());

            if(mFilter.equals("BMI")){
                entries.add(new PieEntry(pieValues.get(i), bmiString[i]));
            }
            else{
                entries.add(new PieEntry(pieValues.get(i), vaccString[i]));
            }
            //entries.add(new PieEntry(pieValues.get(i), test[i]));
            //entries.add(new PieEntry(valueCount[i]))
        }
        String label = "BMI distribution";
        if(mFilter.equals("Vaccination Polio")) label = "Vaccination Polio";
        else if (mFilter.equals("Vaccination Tetanus")) label = "Vaccination Tetanus";
        PieDataSet set = new PieDataSet(entries, label);


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
        //pieChart.animateY(1000);



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
    private void readFileName(){
        //for getting the filenames of the dataset to be displayed in the program
    }
    private void computeChiStat(){
        //compute Chi statistic
        //display green/red box

    }
    public void setQuestionText(int questionNum){
        //set questiontext on launch, and use on prevView() and nextView()

    }

    public void prevView(View view) {
        colorBar.setBackgroundColor(Color.parseColor("#00FF00")); //green color



    }

    public void nextView(View view){
        questionNum++;
        questionText.setText("Question " + Integer.toString(questionNum));
        colorBar.setBackgroundColor(Color.parseColor("#FF0000"));


    }
}
