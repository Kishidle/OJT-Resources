package com.example.user.mpandroidcharttest.Model;

/**
 * Created by Ramon on 1/30/2018.
 */

public class Child {

    private String childID;
    private int regionNum;
    private int provinceNum;
    private int municipalNum;
    private int barangayNum;
    private int cGender;
    private int cAge;
    private int cWeight;
    private int cHeight;
    private float cBMI;

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public int getRegionNum() {
        return regionNum;
    }

    public void setRegionNum(int regionNum) {
        this.regionNum = regionNum;
    }

    public int getProvinceNum() {
        return provinceNum;
    }

    public void setProvinceNum(int provinceNum) {
        this.provinceNum = provinceNum;
    }

    public int getMunicipalNum() {
        return municipalNum;
    }

    public void setMunicipalNum(int municipalNum) {
        this.municipalNum = municipalNum;
    }

    public int getBarangayNum() {
        return barangayNum;
    }

    public void setBarangayNum(int barangayNum) {
        this.barangayNum = barangayNum;
    }

    public int getcGender() {
        return cGender;
    }

    public void setcGender(int cGender) {
        this.cGender = cGender;
    }

    public int getcAge() {
        return cAge;
    }

    public void setcAge(int cAge) {
        this.cAge = cAge;
    }

    public int getcWeight() {
        return cWeight;
    }

    public void setcWeight(int cWeight) {
        this.cWeight = cWeight;
    }

    public int getcHeight() {
        return cHeight;
    }

    public void setcHeight(int cHeight) {
        this.cHeight = cHeight;
    }

    public float getcBMI() {
        return cBMI;
    }

    public void setcBMI(float cBMI) {
        this.cBMI = cBMI;
    }

    public void setParams(String childID, int regionNum, int provinceNum, int municipalNum, int barangayNum, int cGender, int cAge, int cWeight, int cHeight, float cBMI){

        setChildID(childID);
        setRegionNum(regionNum);
        setProvinceNum(provinceNum);
        setMunicipalNum(municipalNum);
        setBarangayNum(barangayNum);
        setcGender(cGender);
        setcAge(cAge);
        setcWeight(cWeight);
        setcHeight(cHeight);
        setcBMI(cBMI);


    }


}
