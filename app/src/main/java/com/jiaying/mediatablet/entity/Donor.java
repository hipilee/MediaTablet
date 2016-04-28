package com.jiaying.mediatablet.entity;

import android.graphics.Bitmap;

/**
 * Created by hipilee on 2014/11/19.
 */
public class Donor {

    private String donorID = "";

    private String idName = "", documentName = "";

    private String gender = "", sex = "";

    private String address = "", dz = "";

    private Bitmap faceBitmap = null;
    private Bitmap documentFaceBitmap = null;

    private String nation = "";

    private String age = "";

    private String year = "", month = "", day = "";


    private static final Donor ourInstance = new Donor();

    public static Donor getInstance() {
        return ourInstance;
    }

    private Donor() {
    }

    public String getAge() {
        return age;
    }

    public String getDz() {
        return dz;
    }

    public String getSex() {
        return sex;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setAge(String age) {
        this.age = age;

    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setDocumentFaceBitmap(Bitmap documentFaceBitmap) {
        this.documentFaceBitmap = documentFaceBitmap;
    }

    public Bitmap getDocumentFaceBitmap() {
        return documentFaceBitmap;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public Bitmap getFaceBitmap() {
        return faceBitmap;
    }

    public void setFaceBitmap(Bitmap faceBitmap) {
        this.faceBitmap = faceBitmap;
    }

    public String getDonorID() {
        return donorID;
    }

    public void setDonorID(String donorID) {
        this.donorID = donorID;
    }
}
