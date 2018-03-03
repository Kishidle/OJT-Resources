package com.example.user.mpandroidcharttest.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.mpandroidcharttest.Model.Child;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramon on 2/11/2018.
 */

public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    //database name
    private static final String DATABASE_NAME = "Synthetic Child Data";

    //table name
    private static final String TABLE_CHILD = "Child_Data";

    //column headers
    private static final String COL_ID = "Child_ID";
    private static final String COL_REGION = "Region";
    private static final String COL_PROVINCE = "Province";
    private static final String COL_MUNICIPAL = "Municipality";
    private static final String COL_BARANGAY = "Barangay";
    private static final String COL_GENDER = "Gender";
    private static final String COL_AGE = "Age";
    private static final String COL_WEIGHT = "Weight(kg)";
    private static final String COL_HEIGHT = "Height(cm)";
    private static final String COL_BMI = "BMI";
    private static final String COL_VACCPOLO = "Vaccination(Polio)";
    private static final String COL_VACCTETA = "Vaccination(Tetanus)";
    private static final String COL_EYES = "Eyes";
    private static final String COL_COLOR = "Color";
    private static final String COL_HEARING = "Hearing";
    private static final String COL_FINEMOTOR = "Fine Motor";
    private static final String COL_GROSSMOTOR = "Gross Motor";
    private static final String COL_MENTAL1 = "Mental_1";
    private static final String COL_MENTAL2 = "Mental_2";
    private static final String COL_MENTAL3 = "Mental_3";
    private static final String COL_MENTAL4 = "Mental_4";
    private static final String COL_MENTAL5 = "Mental_5";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHILD_TABLE = "CREATE TABLE " + TABLE_CHILD + " (" + COL_ID + " TEXT PRIMARY KEY NOT NULL," + COL_REGION + " TEXT," + COL_PROVINCE + " TEXT,"
                + COL_MUNICIPAL + " TEXT," + COL_BARANGAY + " TEXT," + COL_GENDER + " TEXT," + COL_AGE + " TEXT," + COL_WEIGHT + " TEXT,"
                + COL_HEIGHT + " TEXT," + COL_BMI + " TEXT," + COL_VACCPOLO + " TEXT," + COL_VACCTETA + " TEXT," + COL_EYES + " TEXT,"
                + COL_COLOR + " TEXT," + COL_HEARING + " TEXT," + COL_FINEMOTOR + " TEXT," + COL_GROSSMOTOR + " TEXT," + COL_MENTAL1 + " TEXT,"
                + COL_MENTAL2 + " TEXT, " + COL_MENTAL3 + " TEXT," + COL_MENTAL4 + " TEXT," + COL_MENTAL5 + " TEXT" + ")";
        db.execSQL(CREATE_CHILD_TABLE);
    }
    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //drop table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);

        //create table again
        onCreate(db);
    }

    public void addChild(Child child){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //adding child data to values
        values.put(COL_ID, child.getChildID());
        values.put(COL_REGION, child.getRegionNum());
        values.put(COL_PROVINCE, child.getProvinceNum());
        values.put(COL_MUNICIPAL, child.getMunicipalNum());
        values.put(COL_BARANGAY, child.getBarangayNum());
        values.put(COL_GENDER, child.getcGender());
        values.put(COL_AGE, child.getcAge());
        values.put(COL_WEIGHT, child.getcWeight());
        values.put(COL_HEIGHT, child.getcHeight());
        values.put(COL_BMI, child.getcBMI());
        values.put(COL_VACCPOLO, child.getcVaccPol());
        values.put(COL_VACCTETA, child.getcVaccTeta());
        values.put(COL_EYES, child.getcEyes());
        values.put(COL_COLOR, child.getcEyeColorTest());
        values.put(COL_HEARING, child.getcHearing());
        values.put(COL_FINEMOTOR, child.getcFineMotor());
        values.put(COL_GROSSMOTOR, child.getcGrossMotor());
        values.put(COL_MENTAL1, child.getcMental1());
        values.put(COL_MENTAL2, child.getcMental2());
        values.put(COL_MENTAL3, child.getcMental3());
        values.put(COL_MENTAL4, child.getcMental4());
        values.put(COL_MENTAL5, child.getcMental5());

        db.insert(TABLE_CHILD, null, values);
        db.close();

    }

    public Child getChild(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHILD, new String[] { COL_ID, COL_REGION, COL_PROVINCE, COL_MUNICIPAL, COL_BARANGAY, COL_GENDER, COL_AGE,
                                                            COL_WEIGHT, COL_HEIGHT, COL_BMI, COL_VACCPOLO, COL_VACCTETA, COL_EYES, COL_COLOR, COL_HEARING,
                                                            COL_FINEMOTOR, COL_GROSSMOTOR, COL_MENTAL1, COL_MENTAL2, COL_MENTAL3, COL_MENTAL4, COL_MENTAL5}, COL_ID + "=?",
                                                            new String[] { String.valueOf(COL_ID) }, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();

        Child child = new Child();

        child.setChildID(cursor.getString(0));
        child.setRegionNum(cursor.getInt(1));
        child.setProvinceNum(cursor.getInt(2));
        child.setMunicipalNum(cursor.getInt(3));
        child.setBarangayNum(cursor.getInt(4));
        child.setcGender(cursor.getInt(5));
        child.setcAge(cursor.getInt(6));
        child.setcWeight(cursor.getFloat(7));
        child.setcHeight(cursor.getFloat(8));
        child.setcBMI(cursor.getFloat(9));
        child.setcVaccPol(cursor.getInt(10));
        child.setcVaccTeta(cursor.getInt(11));
        child.setcEyes(cursor.getInt(12));
        child.setcEyeColorTest(cursor.getInt(13));

        return child;

    }


    public List<Child> getAllChild(){

        List<Child> childList = new ArrayList<>();

        //select all
        String selectQuery = "SELECT * FROM " + TABLE_CHILD;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){

            do{
                Child child = new Child();
                child.setChildID(cursor.getString(0));
                child.setRegionNum(cursor.getInt(1));
                child.setProvinceNum(cursor.getInt(2));
                child.setMunicipalNum(cursor.getInt(3));
                child.setBarangayNum(cursor.getInt(4));
                child.setcGender(cursor.getInt(5));
                child.setcAge(cursor.getInt(6));
                child.setcWeight(cursor.getFloat(7));
                child.setcHeight(cursor.getFloat(8));
                child.setcBMI(cursor.getFloat(9));
                child.setcVaccPol(cursor.getInt(10));
                child.setcVaccTeta(cursor.getInt(11));
                child.setcEyes(cursor.getInt(12));
                child.setcEyeColorTest(cursor.getInt(13));
                childList.add(child);
            } while(cursor.moveToNext());
        }

        return childList;
    }


    public int getChildCount(){

        String countQuery = "SELECT * FROM " + TABLE_CHILD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();

    }


    public int updateChild(Child child){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID, child.getChildID());
        values.put(COL_REGION, child.getRegionNum());
        values.put(COL_PROVINCE, child.getProvinceNum());
        values.put(COL_MUNICIPAL, child.getMunicipalNum());
        values.put(COL_BARANGAY, child.getBarangayNum());
        values.put(COL_GENDER, child.getcGender());
        values.put(COL_AGE, child.getcAge());
        values.put(COL_WEIGHT, child.getcWeight());
        values.put(COL_HEIGHT, child.getcHeight());
        values.put(COL_BMI, child.getcBMI());
        values.put(COL_VACCPOLO, child.getcVaccPol());
        values.put(COL_VACCTETA, child.getcVaccTeta());
        values.put(COL_EYES, child.getcEyes());
        values.put(COL_COLOR, child.getcEyeColorTest());
        values.put(COL_HEARING, child.getcHearing());
        values.put(COL_FINEMOTOR, child.getcFineMotor());
        values.put(COL_GROSSMOTOR, child.getcGrossMotor());
        values.put(COL_MENTAL1, child.getcMental1());
        values.put(COL_MENTAL2, child.getcMental2());
        values.put(COL_MENTAL3, child.getcMental3());
        values.put(COL_MENTAL4, child.getcMental4());
        values.put(COL_MENTAL5, child.getcMental5());

        return db.update(TABLE_CHILD, values, COL_ID + " = ?",
                new String[]{ String.valueOf(child.getChildID())});

    }
    public void deleteChild(Child child){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHILD, COL_ID + " = ?",
                new String[] { String.valueOf(child.getChildID())});
        db.close();

    }

    public SQLiteDatabase getReadableDatabase(){
        return this.getReadableDatabase();
    }


}
