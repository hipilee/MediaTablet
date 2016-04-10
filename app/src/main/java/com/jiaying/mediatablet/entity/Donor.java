package com.jiaying.mediatablet.entity;
import android.graphics.Bitmap;

/**
 * Created by hipilee on 2014/11/19.
 */
public class Donor {

	private String userName;

	private String year,month,day;

	private String address;

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

	private String gender;

	private String nation;

	private Bitmap faceBitmap;

	private String donorID = "";

	private static final Donor ourInstance = new Donor();

	public static Donor getInstance() {
		return ourInstance;
	}

	private Donor() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
