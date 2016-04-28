package com.jiaying.mediatablet.entity;

/**
 * Created by hipil on 2016/4/25.
 */
public class DevEntity {
    private static DevEntity ourInstance = new DevEntity();

    public static DevEntity getInstance() {
        return ourInstance;
    }

    public String getDeviceId() {
        return deviceId;
    }

    private String deviceId;

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private DevEntity() {
    }
}
