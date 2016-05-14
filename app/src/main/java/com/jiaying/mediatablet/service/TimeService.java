package com.jiaying.mediatablet.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jiaying.mediatablet.constants.Constants;
import com.jiaying.mediatablet.constants.IntentAction;
import com.jiaying.mediatablet.constants.IntentExtra;
import com.jiaying.mediatablet.utils.MyLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：lenovo on 2016/5/13 10:13
 * 邮箱：353510746@qq.com
 * 功能：时间计时
 */
public class TimeService extends Service {
    private static final String TAG = "TimeService";
    //服务器获取到的正确时间
    private long currentTime = 946659661;
    //定时刷新时间任务
    private Timer mTimer = null;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.e(TAG, "timer service onCreate");

        sharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE);
        currentTime = sharedPreferences.getLong("time", System.currentTimeMillis());
        setTimerTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.e(TAG, "timer service onStartCommand" + this.toString());
        currentTime = intent.getLongExtra("currenttime", 0);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.e(TAG, "timer service destroy");
        sharedPreferences.edit().putLong("time", currentTime).commit();
        Intent localIntent = new Intent();
        localIntent.setClass(this, TimeService.class);  //销毁时重新启动Service
        startService(localIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setTimerTask() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent it = new Intent();
                it.setAction(IntentAction.ACTION_UPDATE_TIME);
                it.putExtra(IntentExtra.EXTRA_TIME, currentTime);
                sendBroadcast(it);
                currentTime += 1000;
//                MyLog.e(TAG,"timer service currentTime:" + currentTime);

            }
        }, 0, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }
}
