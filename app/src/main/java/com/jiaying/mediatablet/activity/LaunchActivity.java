package com.jiaying.mediatablet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.constants.IntentExtra;
import com.jiaying.mediatablet.net.thread.ConnectWifiThread;
import com.jiaying.mediatablet.utils.ToastUtils;

//wifi自动连接
public class LaunchActivity extends BaseActivity implements ConnectWifiThread.OnConnSuccessListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoWifiConnect();
    }

    //自动连接wifi
    private void autoWifiConnect() {
        ConnectWifiThread connectWifiThread = new ConnectWifiThread("JiaYing_ZXDC", "jyzxdcarm", 3, this);

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
            ToastUtils.showToast(LaunchActivity.this, "connectWifiThread 已经启动！");
        }
    }

    private void jumpToMainActivity() {
        Intent jumpIntent = new Intent(LaunchActivity.this, MainActivity.class);

        // 该标志是告知MainActivity此次启动是关闭平板电源然后开启的,
        // 进入MainActivity后会根据这个来标识是否清空前一次的状态，如果为真
        // 则会清空上次的状态，重置状态到等待时间状态。
        boolean isBoot = true;

        jumpIntent.putExtra(IntentExtra.EXTRA_BOOT, isBoot);
        startActivity(jumpIntent);

        //关闭LaunchActivity
        this.finish();
    }

    //联网成功后，执行跳转到MainActivity的工作
    @Override
    public void onConnSuccess() {
        jumpToMainActivity();
    }
}