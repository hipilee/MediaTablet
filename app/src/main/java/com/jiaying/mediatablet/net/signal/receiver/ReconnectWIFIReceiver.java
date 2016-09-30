package com.jiaying.mediatablet.net.signal.receiver;

import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.fragment.collection.CollectionPreviewFragment;
import com.jiaying.mediatablet.net.thread.ConnectWifiThread;
import com.jiaying.mediatablet.utils.ToastUtils;

/**
 * Created by hipil on 2016/9/20.
 */
public class ReconnectWIFIReceiver extends Receiver implements ConnectWifiThread.OnConnSuccessListener {
    private MainActivity mainActivity;

    public ReconnectWIFIReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void work() {
        autoWifiConnect();
    }

    private void autoWifiConnect() {
        ConnectWifiThread connectWifiThread = new ConnectWifiThread("JiaYing_ZXDC", "jyzxdcarm", 3, this.mainActivity);

        connectWifiThread.setOnConnSuccessListener(this);

        try {
            connectWifiThread.start();
        } catch (IllegalThreadStateException e) {
            throw new Error("The thread connectWifiThread is already open.");
            /*     Object
                 Throwable
            Error       Exception
                     ?         RuntimeException
           */

            // TODO: 2016/7/23 向数据库写入该异常，并记录线程当时的状态。
        } finally {
            ToastUtils.showToast(this.mainActivity, "connectWifiThread 已经启动！");
        }
    }

    @Override
    public void onConnSuccess() {

    }
}
