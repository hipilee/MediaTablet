package com.jiaying.mediatablet.thread;

import android.util.Log;

import java.net.InetAddress;

/**
 * Created by hipil on 2016/11/18.
 */
public class CheckSerReachable extends Thread {
    private String TAG = "CheckSerReachable";

    private int timeout;
    private String ip;
    private OnUnreachableCallback onUnreachableCallback;

    public CheckSerReachable(int timeout, String ip) {
        this.timeout = timeout;
        this.ip = ip;

    }

    public void setOnUnreachableCallback(OnUnreachableCallback onUnreachableCallback) {
        this.onUnreachableCallback = onUnreachableCallback;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            Log.e(TAG, "执行 ping"+System.currentTimeMillis());
            try {
                if (ping(ip, timeout)) {
                    Log.e(TAG, "ping "+ ip +" 通畅");
                } else {
                    Log.e(TAG, "ping "+ ip +" 不通畅");
                    onUnreachableCallback.onUnreachable();
                }

                sleep(1000 * 30 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    true：可用
//    false：不可用
    public static boolean ping(String ipAddress, int timeout) throws Exception {

        boolean status = InetAddress.getByName(ipAddress).isReachable(timeout);
        return status;
    }

    public interface OnUnreachableCallback {
        void onUnreachable();
    }
}
