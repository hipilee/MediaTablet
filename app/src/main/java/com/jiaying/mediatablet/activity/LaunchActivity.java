package com.jiaying.mediatablet.activity;

import android.content.Intent;
import android.os.Bundle;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.constants.IntentExtra;
import com.jiaying.mediatablet.net.thread.ConnectWifiThread;
import com.jiaying.mediatablet.utils.ToastUtils;

//wifi自动连接
public class LaunchActivity extends BaseActivity implements ConnectWifiThread.OnConnSuccessListener {
    public static String TAG = "LaunchActivity";

    private ConnectWifiThread connectWifiThread = null;

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

    @Override
    protected void onPause() {
        super.onPause();
        this.connectWifiThread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //自动连接wifi
    private void autoWifiConnect() {
        connectWifiThread = new ConnectWifiThread("JiaYing_ZXDC", "jyzxdcarm", 3, this);

        //设置联网成功后的回调函数；
        connectWifiThread.setOnConnSuccessListener(this);

        try {
//            Thread.State connectWifiThreadState = connectWifiThread.getState();

            //对connectWifiThread线程的state做判断，然后决定是否可以start该线程；
            connectWifiThread.start();
        } catch (IllegalThreadStateException e) {
            throw new Error("The thread connectWifiThread is already open.");
            /*     Object
                 Throwable
            Error       Exception
                     ?         RuntimeException
           Error和RuntimeException不需要捕获，出现这种错误就是因为代码严谨性不够，需要在编写代码
           多加小心；除RuntimeException的Exception，都是属于比较有用的异常，可以作为代码逻辑来使用。
           */
        } finally {
            ToastUtils.showToast(LaunchActivity.this, "connectWifiThread 已经启动！");
        }
    }

    private void jumpToMainActivity() {
        Intent jumpIntent = new Intent(LaunchActivity.this, MainActivity.class);

        /* 该标志是告知MainActivity此次启动是关闭平板电源然后开启的,
        进入MainActivity后会根据这个来标识是否清空前一次的状态，如果为真
        则会清空上次的状态，重置状态到等待时间状态。*/
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