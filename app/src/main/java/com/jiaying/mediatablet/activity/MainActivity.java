package com.jiaying.mediatablet.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cylinder.www.facedetect.FdActivity;
import com.cylinder.www.hardware.RecorderManager;
import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.businessobject.Donor;
import com.jiaying.mediatablet.fragment.HintFragment;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalRecordAndFilter;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalUIHandler;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.utils.FilterSignal;
import com.jiaying.mediatablet.net.utils.RecordState;
import com.jiaying.mediatablet.thread.AniThread;
import com.jiaying.mediatablet.fragment.AdviceFragment;
import com.jiaying.mediatablet.fragment.AppointmentFragment;
import com.jiaying.mediatablet.fragment.CollectionFragment;
import com.jiaying.mediatablet.fragment.FistFragment;
import com.jiaying.mediatablet.fragment.FunctionSettingFragment;
import com.jiaying.mediatablet.fragment.InitializeFragment;
import com.jiaying.mediatablet.fragment.OverFragment;
import com.jiaying.mediatablet.fragment.OverServiceEvaluateFragment;
import com.jiaying.mediatablet.fragment.PlayVideoFragment;
import com.jiaying.mediatablet.fragment.PressingFragment;
import com.jiaying.mediatablet.fragment.PunctureEvaluateFragment;
import com.jiaying.mediatablet.fragment.PunctureFragment;
import com.jiaying.mediatablet.fragment.ServerSettingFragment;
import com.jiaying.mediatablet.fragment.SurfInternetFragment;
import com.jiaying.mediatablet.fragment.VideoFragment;
import com.jiaying.mediatablet.fragment.WaitingPlasmFragment;
import com.jiaying.mediatablet.fragment.WelcomePlasmFragment;
import com.jiaying.mediatablet.utils.AppInfoUtils;
import com.jiaying.mediatablet.widget.VerticalProgressBar;

import java.lang.ref.SoftReference;


/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, PunctureFragment.PunctureFragmentInteractionListener,CollectionFragment.CollectionFragmentInteractionListener {

    private RecordState recordState;
    private FilterSignal filterSignal;
    private FragmentManager fragmentManager;
    private AniThread startFist, stopFist;
    private FdActivity fdActivity;
    private View title_bar_view;//标题栏1
    private View title_bar_back_view;//带返回的标题栏
    private ImageView title_bar_back_img;//返回按钮
    private TextView title_bar_back_txt;//带返回标题栏的标题
    private RadioGroup mGroup;
    private ImageView overflow_image;//弹出功能
    private PopupWindow mPopupWindow;
    private View mPopView;
    private View mParentView;
    private ImageView ivStartFistHint, ivStopFistHint;
    private TextView fun_txt;//功能设置
    private TextView server_txt;//服务器设置
    private View wait_bg;//待机背景
    private TextView net_state_txt;//网络链接状态
    private TextView wifi_not_txt;
    private TextView title_txt;//标题
    private VerticalProgressBar battery_pb;//剩余电量
    private View right_view;//采浆过程状态显示
    private View call_view;//呼叫
    private TextView battery_not_connect_txt;//电源未连接提示
    private ProgressDialog mDialog = null;
    private TextView time_txt;//当前时间
    private VerticalProgressBar collect_pb;//采集进度
    ObserverZXDCSignalRecordAndFilter observerZXDCSignalRecordAndFilter;
    ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler;
    ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            //判断电量
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                //获取最大电量，如未获取到具体数值，则默认为100
                int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                //显示电量
                battery_pb.setMax(batteryScale);
                battery_pb.setProgress(batteryLevel);

                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
//                 正在充电
//                    battery_not_connect_txt.setVisibility(View.GONE);
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    {
//                        battery_not_connect_txt.setVisibility(View.VISIBLE);
                    }

                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
//                    net_state_txt.setVisibility(View.VISIBLE);
//                    wifi_not_txt.setVisibility(View.VISIBLE);
//                } else {
                net_state_txt.setVisibility(View.GONE);
                wifi_not_txt.setVisibility(View.GONE);
//                }
            }
        }
    };

    private static final int WHAT_UPDATE_TIME = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_UPDATE_TIME:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
                    time_txt.setText(sysTimeStr); //更新时间
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);
        new Thread(new TimeRunnable()).start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        initTitleBar();
        initGroup();
        initMain();
    }


    //中间部分的ui初始化
    private void initMain() {
        fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.fragment_container, new InitializeFragment()).commit();

        battery_not_connect_txt = (TextView) findViewById(R.id.battery_not_connect_txt);

        time_txt = (TextView) findViewById(R.id.time_txt);
        collect_pb = (VerticalProgressBar) findViewById(R.id.collect_pb);
        collect_pb.setProgress(80);

        ivStartFistHint = (ImageView) this.findViewById(R.id.iv_start_fist);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //初始化tab选择
    private void initGroup() {
        mGroup = (RadioGroup) findViewById(R.id.group);
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_1:
                        //视频列表
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new VideoFragment()).commit();
                        break;
                    case R.id.btn_2:
                        //上网
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new SurfInternetFragment()).commit();
                        break;
                    case R.id.btn_3:
                        //意见
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdviceFragment()).commit();
                        break;
                    case R.id.btn_4:
                        //预约
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AppointmentFragment()).commit();
                        break;

                }
            }
        });
    }


    //标题栏初始化
    private void initTitleBar() {
        title_bar_view = findViewById(R.id.title_bar_view);
        mParentView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        mPopView = getLayoutInflater().inflate(R.layout.popupwin_main, null);
        right_view = findViewById(R.id.right_view);
        call_view = findViewById(R.id.call_view);
        call_view.setOnClickListener(this);
        battery_pb = (VerticalProgressBar) findViewById(R.id.battery_pb);
        wifi_not_txt = (TextView) findViewById(R.id.wifi_not_txt);
        net_state_txt = (TextView) findViewById(R.id.net_state_txt);
        net_state_txt.setOnClickListener(this);

        //选择功能设置，服务器地址设置以及重启
        overflow_image = (ImageView) findViewById(R.id.overflow_image);
        overflow_image.setOnClickListener(this);
        fun_txt = (TextView) mPopView.findViewById(R.id.fun_txt);
        fun_txt.setOnClickListener(this);
        server_txt = (TextView) mPopView.findViewById(R.id.server_txt);
        server_txt.setOnClickListener(this);
        wait_bg = findViewById(R.id.wait_bg);
        title_txt = (TextView) findViewById(R.id.title_txt);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void initVariables() {
        recordState = new RecordState(this);
        filterSignal = new FilterSignal();

        // Observer Pattern: ObservableZXDCSignalListenerThread(Observer),ObserverZXDCSignalUIHandler(Observer),O
        // bservableZXDCSignalListenerThread(Observable)
        observerZXDCSignalRecordAndFilter = new ObserverZXDCSignalRecordAndFilter(recordState, filterSignal);
        observerZXDCSignalUIHandler = new ObserverZXDCSignalUIHandler(new SoftReference<MainActivity>(this));
        observableZXDCSignalListenerThread = new ObservableZXDCSignalListenerThread(recordState, filterSignal);

        // Add the observers into the observable object.
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalUIHandler);
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalRecordAndFilter);
        observableZXDCSignalListenerThread.start();
    }

    public void dealConfirm() {
        Donor donor = Donor.getInstance();
        right_view.setVisibility(View.GONE);
        wait_bg.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_welcome_plasm_title);
        mGroup.setVisibility(View.GONE);

        WelcomePlasmFragment welcomeFragment = WelcomePlasmFragment.newInstance(MainActivity.this.getString(R.string.sloganoneabove), donor.getUserName() + ", " + MainActivity.this.getString(R.string.sloganonebelow));

        fragmentManager.beginTransaction().replace(R.id.fragment_container, welcomeFragment).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_hint_container, HintFragment.newInstance("", "")).commit();
    }

    public void dealCompression() {
        right_view.setVisibility(View.GONE);
        wait_bg.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_pressing_title);
        mGroup.setVisibility(View.GONE);

        PressingFragment pressingFragment = new PressingFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, pressingFragment).commit();
    }

    public void dealPuncture() {
        right_view.setVisibility(View.GONE);
        wait_bg.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_puncture_title);
        mGroup.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new PunctureFragment()).commit();
    }

    public void dealStartPunctureVideo() {
        right_view.setVisibility(View.GONE);
        wait_bg.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_puncture_video);
        mGroup.setVisibility(View.GONE);

        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance("/sdcard/kindness.mp4","PunctureVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    public void dealStart() {
        right_view.setVisibility(View.GONE);
        wait_bg.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_collect_title);
        mGroup.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectionFragment()).commit();
    }

    public void dealSignalStartCollcetionVideo(){

        right_view.setVisibility(View.VISIBLE);
        wait_bg.setVisibility(View.GONE);
        title_txt.setText(R.string.play_video);
        mGroup.setVisibility(View.GONE);
        title_bar_view.setVisibility(View.GONE);
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance("/sdcard/donation.mp4","StartCollcetionVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    public void dealStartFist() {

        ivStartFistHint = (ImageView) findViewById(R.id.iv_start_fist);
        ivStartFistHint.setVisibility(View.VISIBLE);
        startFist = new AniThread(this, ivStartFistHint, "startfist.gif", 150);
        startFist.startAni();
    }

    public void dealStopFist() {
        startFist.finishAni();
        ivStartFistHint.setVisibility(View.INVISIBLE);
    }

    public void dealEnd() {
        right_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverFragment()).commit();
    }

//    private void Test() {
//
//        //显示
//        Button btn00 = (Button) findViewById(R.id.btnShow);
//        btn00.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View view = findViewById(R.id.ll_test);
//                view.setVisibility(View.VISIBLE);
//            }
//        });
//        //隐藏
//        Button btn01 = (Button) findViewById(R.id.btnHide);
//        btn01.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View view = findViewById(R.id.ll_test);
//                view.setVisibility(View.GONE);
//            }
//        });
//        //初始化
//        Button btn0 = (Button) findViewById(R.id.btn0);
//        btn0.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.app_name);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new InitializeFragment()).commit();
//            }
//        });
//        //休眠界面
//        Button btn1_1 = (Button) findViewById(R.id.btn1_1);
//        btn1_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.VISIBLE);
//                title_txt.setText(R.string.app_name);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new WaitingPlasmFragment()).commit();
//            }
//        });
//
//        //等待献浆元
//        Button btn1 = (Button) findViewById(R.id.btn1);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_wait_plasm_title);
//                mGroup.setVisibility(View.GONE);
//                WaitingPlasmFragment waitingPlasmFragment = WaitingPlasmFragment.newInstance(getString(R.string.general_welcome), "");
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, waitingPlasmFragment).commit();
//            }
//        });
//        //欢迎献浆
//        Button btn2 = (Button) findViewById(R.id.btn2);
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Donor donor = Donor.getInstance();
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_welcome_plasm_title);
//                mGroup.setVisibility(View.GONE);
//
//                WelcomePlasmFragment welcomeFragment = WelcomePlasmFragment.newInstance(MainActivity.this.getString(R.string.sloganoneabove), donor.getUserName() + ", " + MainActivity.this.getString(R.string.sloganonebelow));
//
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, welcomeFragment).commit();
//            }
//        });
//
//
//        //加压提示
//        Button btn3 = (Button) findViewById(R.id.btn3);
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_pressing_title);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PressingFragment()).commit();
//            }
//        });
//
//        //穿刺提示
//        Button btn4 = (Button) findViewById(R.id.btn4);
//        btn4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_puncture_title);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PunctureFragment()).commit();
//            }
//        });
//
//        //穿刺视频播放
//        Button btn5 = (Button) findViewById(R.id.btn5);
//        btn5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_puncture_video);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PlayVideoFragment()).commit();
//            }
//        });
//
//        //穿刺评价
//        Button btn6 = (Button) findViewById(R.id.btn6);
//        btn6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_puncture_evaluate);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PunctureEvaluateFragment()).commit();
//            }
//        });
//
//        //采集提示
//        Button btn7 = (Button) findViewById(R.id.btn7);
//        btn7.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_collect_title);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectionFragment()).commit();
//            }
//        });
//
//        //采集播放视频，并且显示采集的进度等信息
//        Button btn8 = (Button) findViewById(R.id.btn8);
//        btn8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.VISIBLE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.play_video);
//                mGroup.setVisibility(View.VISIBLE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PlayVideoFragment()).commit();
//            }
//        });
//        //握拳提示
//        Button btn9 = (Button) findViewById(R.id.btn9);
//        btn9.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startFist.startAni();
//                wait_bg.setVisibility(View.GONE);
//                mGroup.setVisibility(View.VISIBLE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new FistFragment()).commit();
//            }
//        });
//
//        //结束服务评价
//        Button btn10 = (Button) findViewById(R.id.btn10);
//        btn10.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverServiceEvaluateFragment()).commit();
//            }
//        });
//
//        //结束欢送
//        Button btn11 = (Button) findViewById(R.id.btn11);
//        btn11.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                right_view.setVisibility(View.GONE);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverFragment()).commit();
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        Intent it = null;
        switch (v.getId()) {
            case R.id.fun_txt:
                //功能设置
//                it = new Intent(MainActivity.this, FunctionSettingActivity.class);
                title_bar_view.setVisibility(View.GONE);
                title_bar_back_view.setVisibility(View.VISIBLE);
                title_bar_back_txt.setText(R.string.func_setting);
                right_view.setVisibility(View.GONE);
                mGroup.setVisibility(View.GONE);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new FunctionSettingFragment()).addToBackStack(null).commit();
                mPopupWindow.dismiss();
                break;
            case R.id.server_txt:
                //服务器设置
//                it = new Intent(MainActivity.this, ServerSettingActivity.class);
                title_bar_view.setVisibility(View.GONE);
                title_bar_back_view.setVisibility(View.VISIBLE);
                title_bar_back_txt.setText(R.string.server_setting);
                right_view.setVisibility(View.GONE);
                mGroup.setVisibility(View.GONE);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new ServerSettingFragment()).addToBackStack(null).commit();
                mPopupWindow.dismiss();
                break;
            case R.id.restar_txt:
                //重启
                mPopupWindow.dismiss();
                break;
            case R.id.overflow_image:
//                showPopWindow();
                break;
            case R.id.net_state_txt:
                //检测网络和检查服务器配置
                it = new Intent(MainActivity.this, ServerSettingActivity.class);
                break;
            case R.id.call_view:
                //呼叫护士提供服务
                showCallDialog();
                break;
            case R.id.back_img:
                //返回
                title_bar_back_view.setVisibility(View.GONE);
                title_bar_view.setVisibility(View.VISIBLE);
                fragmentManager.popBackStack();
                break;
        }
        if (it != null) {
            startActivity(it);
        }
    }


//    private void showPopWindow() {
//        View view = findViewById(R.id.ll_test);
//        view.setVisibility(View.VISIBLE);
//        if (mPopupWindow == null) {
//            mPopupWindow = new PopupWindow(mPopView,
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
//                    false);
//            mPopupWindow.setHeight(AppInfoUtils.dip2px(MainActivity.this, 195));
//            mPopupWindow.setWidth(AppInfoUtils.dip2px(MainActivity.this, 210));
//            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//            mPopupWindow.setOutsideTouchable(true);
//            mPopupWindow.setFocusable(true);
//
//        }
//        mPopupWindow
//                .setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//                    @Override
//                    public void onDismiss() {
//                    }
//                });
//        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
//        mPopupWindow.showAtLocation(mParentView, Gravity.RIGHT
//                        | Gravity.TOP, AppInfoUtils.dip2px(MainActivity.this, 2),
//                AppInfoUtils.dip2px(MainActivity.this, 76));
//    }

    private void showCallDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
        }
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        mDialog.setContentView(R.layout.dlg_call_service);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            // The donor can't use the BACK button to close the APP.
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFragmentInteraction(RecSignal recSignal) {
        observableZXDCSignalListenerThread.recMsg(recSignal);
    }

    private class TimeRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Message message = new Message();
                    message.what = WHAT_UPDATE_TIME;
                    mHandler.sendMessage(message);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
