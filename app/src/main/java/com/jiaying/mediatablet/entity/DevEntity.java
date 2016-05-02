package com.jiaying.mediatablet.entity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hipil on 2016/4/25.
 */
public class DevEntity {
    private static DevEntity ourInstance = new DevEntity();
    private SharedPreferences settings;
    private SharedPreferences.Editor localEditor;
    private Activity activity;

    public static DevEntity getInstance() {
        return ourInstance;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        settings = activity.getPreferences(Context.MODE_PRIVATE);
        localEditor = this.settings.edit();
    }

    public String getDeviceId() {
        return settings.getString("devid", "wrongid");
    }

    public void setDeviceId(String deviceId) {
        localEditor.putString("devid", deviceId);
        localEditor.commit();
    }

    private DevEntity() {

    }
}
