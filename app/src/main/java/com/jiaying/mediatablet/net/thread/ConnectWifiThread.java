package com.jiaying.mediatablet.net.thread;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
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

    private OnConnSuccessListener onConnSuccessListener = null;

    public ConnectWifiThread(String SSID, String PWD, int TYPE, Context context) {
        this.SSID = SSID;
        this.PWD = PWD;
        this.TYPE = TYPE;
        wifiAdmin = new WifiAdmin(context);
    }

    @Override
    public void run() {
        super.run();
        //无论wifi是否关闭，都先关闭wifi，因为会出现wifi自己掉线的情况，在这种掉线的情况通常需要先关闭wifi
        //在平板
        Log.e("error", "关闭wifi");
        wifiAdmin.closeWifi();
        while (true) {
            //判断wifi是否已经打开
            if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {//wifi已经打开
                  /*连接网络,此处的addNetwork是异步操作，不能确保其可以立即添加网络成功，
                    所以以3秒为间隔来反复轮询网络添加结果*/
                wifiIsOk = wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(SSID, PWD, TYPE));
                Log.e("error", "连接wifi");
                //判断wifi是否已经连接上
                if (wifiIsOk) {
                    //界面跳转
                    if (this.onConnSuccessListener == null)
                        throw new RuntimeException("onConnSuccessListener is null");
                    this.onConnSuccessListener.onConnSuccess();
                    break;
                }
            } else {//wifi没有打开
                wifiAdmin.openWifi();
                Log.e("error", "打开wifi");
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

    public void setOnConnSuccessListener(OnConnSuccessListener onConnSuccessListener) {
        this.onConnSuccessListener = onConnSuccessListener;
    }

    public interface OnConnSuccessListener {
        public void onConnSuccess();
    }
}

