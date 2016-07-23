package com.jiaying.mediatablet.net.thread;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.jiaying.mediatablet.utils.WifiAdmin;

/**
 * Created by hipil on 2016/7/23.
 */
public class ConnectWifiThread extends Thread {
    private boolean wifiIsOk = false;
    private String SSID = null;
    private String PWD = null;
    private int TYPE = 0;
    private WifiAdmin wifiAdmin = null;
    private OnConnSuccessListener onConnSuccessListener=null;
    public ConnectWifiThread(String SSID, String PWD, int TYPE, Context context) {
        this.SSID = SSID;
        this.PWD = PWD;
        this.TYPE = TYPE;
        wifiAdmin = new WifiAdmin(context);

    }

    public void setOnConnSuccessListener(OnConnSuccessListener onConnSuccessListener){
        this.onConnSuccessListener = onConnSuccessListener;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            //判断wifi是否已经打开
            if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {//wifi已经打开
                  /*连接网络,此处的addNetwork是异步操作，不能确保其可以立即添加网络成功，
                    所以以3秒为间隔来反复轮询网络添加结果*/
                wifiIsOk = wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(SSID, PWD, TYPE));
                //判断wifi是否已经连接上
                if (wifiIsOk) {
                    //界面跳转
                    if(this.onConnSuccessListener == null)
                        throw new RuntimeException("onConnSuccessListener is null");
                    this.onConnSuccessListener.onConnSuccess();
                    break;
                }
            } else {//wifi没有打开
                wifiAdmin.openWifi();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                /*
                Thrown when a waiting thread is activated
                before the condition it was waiting for has been satisfied
                比如：在sleep期间，调用了Interrupt()函数会抛出该异常。
                */
                e.printStackTrace();
            }
        }
    }

    public interface OnConnSuccessListener{

        public void onConnSuccess();
    }
}

