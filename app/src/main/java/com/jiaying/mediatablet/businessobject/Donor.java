package com.jiaying.mediatablet.businessobject;
import android.graphics.Bitmap;

/**
 * Created by hipilee on 2014/11/19.
 */
public class Donor {

	private String userName;

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
