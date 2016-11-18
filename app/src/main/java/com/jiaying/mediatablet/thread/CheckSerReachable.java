package com.jiaying.mediatablet.thread;

import java.net.InetAddress;

/**
 * Created by hipil on 2016/11/18.
 */
public class CheckSerReachable extends Thread {
    private String TAG = "CheckSerReachable";

    private int timeout;
    private String ip;
    private OnUnreachableCallback onUnreachableCallback;

    public CheckSerReachable(int timeout, String ip, OnUnreachableCallback onUnreachableCallback) {
        this.timeout = timeout;
        this.ip = ip;
        this.onUnreachableCallback = onUnreachableCallback;
    }

    @Override
    public void run() {
        super.run();
        while (isInterrupted()) {
            try {
                sleep(1000 * 60 * 2);
                if (ping(ip, timeout)) {

                } else {
                    onUnreachableCallback.onUnreachable();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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
