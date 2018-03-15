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
import android.widget.Toast;

import com.example.user.otherproject.Controller.DBHelper;
import com.example.user.otherproject.Model.Child;
import com.example.user.otherproject.Model.Question;
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
    private ArrayList<Child> childListLeft, childListRight;
    private ArrayList<Child> filteredList;
    private ArrayList<PieEntry> pieEntries1;
    private ArrayList<PieEntry> pieEntries2;
    private ArrayList<Question> questionList;
    private RelativeLayout graphLayoutLeft, graphLayoutRight;
    private String xData, xDataRight;
    private int[] yDataLeft, yDataRight;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private int leftFilterNum, rightFilterNum, questionNum;
    private TextView questionText;
    private View colorBar;
    private Question question;


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

        String fileCSVLeft = getIntent().getStringExtra("listLeft");
        String fileCSVRight = getIntent().getStringExtra("listRight");
        String featureCSV = getIntent().getStringExtra("featureList");

        childListLeft = new ArrayList<Child>();
        childListRight = new ArrayList<Child>();
        questionList = new ArrayList<Question>();
        prepareDataset(fileCSVLeft, childListLeft);
        prepareDataset(fileCSVRight, childListRight);
        prepareFeatures(featureCSV, questionList);

        Log.d("intenttest", questionList.get(0).getFeatureText().get(0));

        //mPie1=(PieChart) findViewById(R.id.piechart1);
        //mPie2=(PieChart) findViewById(R.id.piechart2);

        questionNum = 1;
        //preliminary set question text
        questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText("Question " + Integer.toString(questionNum));

        colorBar = (View) findViewById(R.id.colored_bar);

        graphLayoutLeft = (RelativeLayout) findViewById(R.id.graph_container_left);
        graphLayoutRight = (RelativeLayout) findViewById(R.id.graph_container_right);

        pieEntries1 = new ArrayList<>();
        pieEntries2 = new ArrayList<>();
        //childList = new ArrayList<>();
        filteredList = new ArrayList<>();
        Log.d("hitest", "testing it started here");
        //createCharts();
        //prepareChartData();

    }



    public void createCharts(){
        mPieLeft = createPieChart();
        mPieRight = createPieChart();
    }


    public void prepareDataset(String fileCSV, ArrayList<Child> childList){
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

        //Toast.makeText(Mai.this, "Added dataset!", Toast.LENGTH_LONG).show();

    }

    public void prepareFeatures(String featureCSV, ArrayList<Question> questionList){
        /*
        Loads hard-coded file from the feature button. This function already prepares the Question
        arraylist.
         */
        //Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        //chooseFile.setType("*/*");
        //chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        //startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
        //TODO load files from cloud

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
                    question.setQuestionNum(col[1].trim());
                    question.setQuestionText(col[2].trim());
                    Log.d("questionNum", col[1].trim());

                }
                else{
                    //new features
                    question.addFeatureGroup(col[0].trim());
                    question.addFeatureNum(Integer.parseInt(col[1].trim()));
                    question.addFeatureText(col[2].trim());
                }

            }
        } catch(IOException e){
            e.printStackTrace();
        }

        //Toast.makeText(MainMenu.this, "Added feature dataset!", Toast.LENGTH_LONG).show();

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
