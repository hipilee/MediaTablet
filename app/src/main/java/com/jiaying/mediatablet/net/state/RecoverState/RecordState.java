package com.jiaying.mediatablet.net.state.RecoverState;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.jiaying.mediatablet.net.signal.RecSignal;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/3.
 */

// It's used when the app is closed and then restart.
public class RecordState {
    private SharedPreferences settings;
    private SharedPreferences.Editor localEditor;
    private String state;
    private Activity activity;
    private static RecordState recordState=null;

    private RecordState(Activity activity) {

        // Initialize the variables.
        this.activity = activity;
        settings = this.activity.getPreferences(Context.MODE_PRIVATE);
        localEditor = this.settings.edit();
    }

    public static RecordState getInstance(Activity activity){
        if(recordState==null){
            recordState = new RecordState(activity);
        }
        return recordState;
    }


    // commit the state info and donor info into preference.
    public synchronized void commit() {
        localEditor.putString("state", state);
        localEditor.commit();
    }

    public synchronized void retrieve() {
        state = settings.getString("state", null);

    }

    public synchronized String getState(){
        return state;
    }

    public synchronized void reset() {

    }
// **********record the state info***********


    public synchronized void recConfirm() {
        state = StateIndex.WAITINGFORAUTH;
    }

    public synchronized void recAuth() {
        state = StateIndex.WAITINGFORCOMPRESSION;
    }

    public void recCompression() {
        state = StateIndex.WAITINGFORPUNCTURE;
    }


    public synchronized void recPuncture() {
        state = StateIndex.WAITINGFORSTART;
    }


    public synchronized void recStart() {
        state = StateIndex.WAITINGFOREND;
    }


    public synchronized void recEnd() {
        state = StateIndex.WAITINGFORCHECK;
    }

    public synchronized void recReady() {
        state = StateIndex.WAITINGFORDONOR;
    }


}
