package com.jiaying.mediatablet.net.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/3.
 */

// It's used when the app is closed and then restart.
public class RecordState {
    private SharedPreferences settings;
    private SharedPreferences.Editor localEditor;
    private HashMap<String, Boolean> stateMap;
    private HashMap<String, String> informationMap;
    private Activity activity;

    public RecordState(Activity activity) {

        // Initialize the variables.
        this.activity = activity;
        settings = this.activity.getPreferences(Context.MODE_PRIVATE);
        localEditor = this.settings.edit();

        // true 该状态发生过
        // false 该状态未发生过
        stateMap = new HashMap<String, Boolean>();
        informationMap = new HashMap<String, String>();

        //load the state info .
        stateMap.put("confirm", getConfirm());
        stateMap.put("compression", getCompression());
        stateMap.put("puncture", getPuncture());
        stateMap.put("start", getStart());
        stateMap.put("end", getEnd());
        // load donor info.
        informationMap.put("donorname", getDonorName());
        informationMap.put("gender", getGender());
        informationMap.put("donorid", getDonorID());
    }


    // commit the state info and donor info into preference.
    public void commit() {
        localEditor.putBoolean("confirm", stateMap.get("confirm"));
        localEditor.putString("donorname", informationMap.get("donorname"));
        localEditor.putString("donorid", informationMap.get("donorid"));
        localEditor.putString("gender", informationMap.get("gender"));

        localEditor.putBoolean("confirm", stateMap.get("confirm"));
        localEditor.putBoolean("compression",stateMap.get("compression"));
        localEditor.putBoolean("puncture", stateMap.get("puncture"));
        localEditor.putBoolean("start", stateMap.get("start"));
        localEditor.putBoolean("end", stateMap.get("end"));
        localEditor.commit();
    }

    public void retrieve() {
//         donor info
        informationMap.put("donorname", settings.getString("donorname", null));
        informationMap.put("donorid", settings.getString("donorid", null));
        informationMap.put("gender", settings.getString("gender", null));

//     state info
        stateMap.put("confirm", settings.getBoolean("confirm", false));
        stateMap.put("compression", settings.getBoolean("compression", false));
        stateMap.put("puncture", settings.getBoolean("puncture", false));
        stateMap.put("start", settings.getBoolean("start", false));
        stateMap.put("end", settings.getBoolean("end", false));
    }

    public void reset() {
        stateMap.put("confirm", false);
        stateMap.put("compression", false);
        stateMap.put("puncture", false);
        stateMap.put("start", false);
        stateMap.put("end", false);
    }
// **********record the state info***********
    public Boolean getConfirm() {
        return stateMap.get("confirm");
    }

    public void recConfirm() {
        stateMap.put("confirm", true);
    }

    public Boolean getCompression() {
        return stateMap.get("compression");
    }

    public void recCompression() {
        stateMap.put("compression", true);
    }

    public Boolean getPuncture() {
        return stateMap.get("puncture");
    }

    public void recPuncture() {
        stateMap.put("compression", true);
        stateMap.put("puncture", true);
    }

    public Boolean getStart() {
        return stateMap.get("start");
    }

    public void recStart() {
        stateMap.put("compression", true);
        stateMap.put("puncture", true);
        stateMap.put("start", true);
    }


    public Boolean getEnd() {
        return stateMap.get("end");
    }

    public void recEnd() {
        stateMap.put("confirm",true);
        stateMap.put("compression", true);
        stateMap.put("puncture", true);
        stateMap.put("start", true);
        stateMap.put("end", true);
    }


// **********record the donor info***********

    public String getDonorName() {
        return informationMap.get("donorname");
    }

    public void setDonorName(String donorName) {
        informationMap.put("donorname", donorName);
    }

    public String getDonorID() {
        return informationMap.get("donorid");
    }

    public void setDonorID(String donorID) {
        informationMap.put("donorid", donorID);
    }

    public String getGender() {
        return informationMap.get("gender");
    }

    public void setGender(String gender){
        informationMap.put("gender",gender);
    }
}
