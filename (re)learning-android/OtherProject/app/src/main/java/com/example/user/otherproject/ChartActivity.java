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
    private String xData, xDataRight, questionLabel, questionString;
    private int[] yDataLeft, yDataRight;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private int leftFilterNum, rightFilterNum;
    private TextView questionText;
    private View colorBar;
    private Question question;
    private int questionNum = 0;
    private int totalCount = 0;
    private ValueCounter vCountLeft, vCountRight;
    private int yesLeft, yesRight;



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

        questionLabel = questionList.get(0).getQuestionLabel();
        questionString = questionList.get(0).getQuestionText();
        //preliminary set question text
        questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText("Question " + questionLabel + ": " + questionString);

        colorBar = (View) findViewById(R.id.colored_bar);

        graphLayoutLeft = (RelativeLayout) findViewById(R.id.graph_container_left);
        graphLayoutRight = (RelativeLayout) findViewById(R.id.graph_container_right);

        pieEntries1 = new ArrayList<>();
        pieEntries2 = new ArrayList<>();
        //childList = new ArrayList<>();
        filteredList = new ArrayList<>();
        Log.d("hitest", "testing it started here");
        createCharts();

        graphLayoutLeft.addView(mPieLeft);
        graphLayoutRight.addView(mPieRight);

        ViewGroup.LayoutParams params = mPieLeft.getLayoutParams();
        params.height = 400;
        params.width = 400;

        ViewGroup.LayoutParams paramsRight = mPieRight.getLayoutParams();
        paramsRight.height = 400;
        paramsRight.width = 400;

        preparePieChartData2(mPieLeft, childListLeft, "left");
        preparePieChartData2(mPieRight, childListRight, "right");
        computeChiStat();

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
        int tempNum = 0;
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
                        tempNum++;

                    }
                    isFirst = false;
                    question = new Question();
                    question.setQuestionNum(tempNum);
                    question.setQuestionLabel(col[1].trim());
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
            questionList.add(question);
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
        pieChart.setDrawHoleEnabled(false);

        // enable hole and configure
        //pieChart.setDrawHoleEnabled(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setHoleRadius(45);
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

    private void preparePieChartData2(PieChart pieChart, ArrayList<Child> childList, String test){

        List<PieEntry> pieEntries = new ArrayList<>();
        String[] pieLabels = {"No", "Yes"};
        ArrayList<Float> pieValues = computeValue2(childList, test);

        Log.d("valuesize", Integer.toString(pieValues.size()));
        for(int i = 0; i < pieValues.size(); i++){
            Log.d("Pie Values", pieValues.get(i).toString());

            pieEntries.add(new PieEntry(pieValues.get(i), pieLabels[i]));
        }
        String label = questionList.get(questionNum).getQuestionLabel();

        PieDataSet set = new PieDataSet(pieEntries, "");

        set.setSliceSpace(4f);
        set.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(18f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        Description desc = new Description();
        desc.setText(label);
        pieChart.setDescription(desc);
        pieChart.invalidate();


    }
    private ArrayList<Float> computeValue2(ArrayList<Child> childList, String test){
        totalCount = 0;
        ValueCounter valueCounter = new ValueCounter(childList, questionNum);
        valueCounter.setResponse();

        int[] valueCount = valueCounter.getResponse();
        if(test.equals("left"))
            yesLeft = valueCount[1];
        else if(test.equals("right")){
            yesRight = valueCount[1];
        }
        Log.d("z-test2", Integer.toString(valueCount[1]));

        ArrayList<Float> computedValues = new ArrayList<>();
        for(int i = 0; i < valueCount.length; i++){
            Log.d("valuecounttest", Integer.toString(valueCount[i]));
            totalCount += valueCount[i];
        }
        for(int i = 0; i < valueCount.length; i++){
            computedValues.add((float) valueCount[i] / totalCount * 10);
        }

        return computedValues;

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


        Log.d("z-value test", Integer.toString(yesLeft));
        Log.d("z-valuetest2", Integer.toString(yesRight));
        double childLeftSize = (double) childListLeft.size();
        double childRightSize = (double) childListRight.size();
        double leftDouble = (double) yesLeft / childLeftSize;
        double rightDouble = (double) yesRight / childRightSize;

        Log.d("childListsize", "Left: " + Integer.toString(childListLeft.size()) + " and Right: " + Integer.toString(childListRight.size()));
        double pHat = ((childLeftSize * leftDouble) + (childRightSize * rightDouble)) / (childLeftSize + childRightSize);
        double sep = Math.sqrt((pHat * (1 - pHat)) * (1 / childLeftSize + 1 / childRightSize));
        Log.d("phat", Double.toString(pHat));
        Log.d("sep", Double.toString(sep));
        double z = (leftDouble - rightDouble) / sep;
        Log.d("z-value", Double.toString(z));
        double z2 = Math.pow(z, 2);
        Log.d("pow", Double.toString(z2));
        if(z <= 2.58){ // 99% confidence interval
            colorBar.setBackgroundColor(Color.parseColor("#00FF00"));
        }
        else if(z > 2.58){
            colorBar.setBackgroundColor(Color.parseColor("#FF0000"));
        }

        //compute Chi statistic
        //display green/red box

    }
    public void setQuestionText(int questionNum){
        //set questiontext on launch, and use on prevView() and nextView()

    }

    public void prevView(View view) {
        questionNum--;
        if(questionNum < 0){
            questionNum = questionList.size() - 1;
        }
        questionText.setText("Question " + questionList.get(questionNum).getQuestionLabel() + ": " + questionList.get(questionNum).getQuestionText());
         //green color

        preparePieChartData2(mPieLeft, childListLeft, "left");
        preparePieChartData2(mPieRight, childListRight, "right");




    }

    public void nextView(View view){
        questionNum++;
        if(questionNum >= questionList.size()){
            questionNum = 0;
        }
        questionText.setText("Question " + questionList.get(questionNum).getQuestionLabel() + ": " + questionList.get(questionNum).getQuestionText());


        preparePieChartData2(mPieLeft, childListLeft, "left");
        preparePieChartData2(mPieRight, childListRight, "right");


    }
}
