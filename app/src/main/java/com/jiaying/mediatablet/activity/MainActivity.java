package com.jiaying.mediatablet.activity;

import android.app.FragmentManager;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cylinder.www.facedetect.FdAuthActivity;
import com.jiaying.mediatablet.R;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.entity.VideoPathEntity;
import com.jiaying.mediatablet.fragment.AuthFragment;
import com.jiaying.mediatablet.fragment.AuthPreviewFragment;

import com.jiaying.mediatablet.fragment.BlankFragment;
import com.jiaying.mediatablet.fragment.CollectionPreviewFragment;
import com.jiaying.mediatablet.fragment.EndFragment;
import com.jiaying.mediatablet.fragment.WaitingForDonorFragment;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalRecord;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalUIHandler;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForCheckState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.thread.AniThread;
import com.jiaying.mediatablet.fragment.AdviceFragment;
import com.jiaying.mediatablet.fragment.AppointmentFragment;
import com.jiaying.mediatablet.fragment.CollectionFragment;
import com.jiaying.mediatablet.fragment.FunctionSettingFragment;
import com.jiaying.mediatablet.fragment.PlayVideoFragment;
import com.jiaying.mediatablet.fragment.PressingFragment;
import com.jiaying.mediatablet.fragment.ServerSettingFragment;
import com.jiaying.mediatablet.fragment.SurfInternetFragment;
import com.jiaying.mediatablet.fragment.VideoFragment;
import com.jiaying.mediatablet.fragment.WelcomePlasmFragment;
import com.jiaying.mediatablet.widget.VerticalProgressBar;

import java.lang.ref.SoftReference;


/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {


    private RecordState recordState;
    private FragmentManager fragmentManager;
    private AniThread startFist;
    private FdAuthActivity fdAuthActivity;
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
    private ImageView ivLogoAndBack;
    private TextView fun_txt;//功能设置
    private TextView server_txt;//服务器设置
    private TextView net_state_txt;//网络链接状态
    private TextView wifi_not_txt;
    private TextView title_txt;//标题
    private VerticalProgressBar battery_pb;//剩余电量
    private View left_hint_view;//采浆过程状态显示
    private View call_view;//呼叫
    private TextView battery_not_connect_txt;//电源未连接提示
    private ProgressDialog mDialog = null;
    private TextView time_txt;//当前时间
    private VerticalProgressBar collect_pb;//采集进度
    private View dlg_call_service_view;//电话服务view
    private CollectionPreviewFragment collectionPreviewFragment;

    private PowerManager.WakeLock mWakelock;
    private KeyguardManager km;
    private PowerManager pm;

    private ObserverZXDCSignalRecord observerZXDCSignalRecordAndFilter;
    private ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler;
    private ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread = null;

    public ObservableZXDCSignalListenerThread getObservableZXDCSignalListenerThread() {
        return observableZXDCSignalListenerThread;
    }

    public RecordState getRecordState() {
        return recordState;
    }

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


    //这个处理时间也是需要解决的

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
        forbidLockScreen();

    }

    private void forbidLockScreen() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");

        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();  //解锁
        mWakelock.acquire();//点亮
    }

    @Override
    protected void initVariables() {
        Log.e("ERROR", "开始执行MainActivity中的onCreate()函数");
        recordState = RecordState.getInstance(this);
        TabletStateContext.getInstance().setCurrentState(WaitingForCheckState.getInstance());

        fragmentManager = getFragmentManager();
        // Observer Pattern: ObservableZXDCSignalListenerThread(Observer),ObserverZXDCSignalUIHandler(Observer),
        // ObservableZXDCSignalListenerThread(Observable)


        observableZXDCSignalListenerThread = new ObservableZXDCSignalListenerThread(recordState);
        observerZXDCSignalRecordAndFilter = new ObserverZXDCSignalRecord(recordState);
        observerZXDCSignalUIHandler = new ObserverZXDCSignalUIHandler(new SoftReference<>(this), this);

        // Add the observers into the observable object.
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalUIHandler);
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalRecordAndFilter);
        observableZXDCSignalListenerThread.start();
    }

    @Override
    protected void initView() {
        Log.e("ERROR", "initView");
        setContentView(R.layout.activity_main);
        initTitleBar();
        initTabGroup();
        initMainUI();
    }

    private void initLeftView() {

    }

    //标题栏初始化
    private void initTitleBar() {
        title_bar_view = findViewById(R.id.title_bar_view);
        ivLogoAndBack = (ImageView) findViewById(R.id.logo_or_back);
        mParentView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        mPopView = getLayoutInflater().inflate(R.layout.popupwin_main, null);
        left_hint_view = findViewById(R.id.left_view);
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

        title_txt = (TextView) findViewById(R.id.title_txt);
    }

    //初始化tab选择
    private void initTabGroup() {
        mGroup = (RadioGroup) findViewById(R.id.group);
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_video:
                        //视频列表
                        TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEO);
                        break;

                    case R.id.btn_surfinternet:
                        //上网
                        TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOSURF);
                        break;

                    case R.id.btn_sug_eval:
                        //意见
                        TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOSUGGEST);
                        break;

                    case R.id.btn_appointment:
                        //预约
                        TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOAPPOINT);
                        break;
                }
            }
        });
    }

    //中间部分的ui初始化
    private void initMainUI() {

        dlg_call_service_view = findViewById(R.id.dlg_call_service_view);
        View dlg_call_service_cancle_view = findViewById(R.id.dlg_call_service_view);
        dlg_call_service_cancle_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);
            }
        });

//        fragmentManager.beginTransaction().replace(R.id.fragment_container, new InitializeFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new WaitingForDonorFragment()).commit();

        battery_not_connect_txt = (TextView) findViewById(R.id.battery_not_connect_txt);

        time_txt = (TextView) findViewById(R.id.time_txt);
        collect_pb = (VerticalProgressBar) findViewById(R.id.collect_pb);
        collect_pb.setProgress(80);

        ivStartFistHint = (ImageView) this.findViewById(R.id.iv_start_fist);

    }

    @Override
    public void loadData() {
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("ERROR", "开始执行MainActivity中的onRestart()函数");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ERROR", "开始执行MainActivity中的onStart()函数");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ERROR", "开始执行MainActivity中的onResume()函数");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ERROR", "开始执行MainActivity中的onPause()函数");
        long start = System.currentTimeMillis();
        TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.POWEROFF);
        StartMainActivityAgain();
        long end = System.currentTimeMillis();
        Log.e("ERROR", "结束执行MainActivity中的onPause()函数，耗时：" + (end - start) / 1000.0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ERROR", "开始执行MainActivity中的onStop()函数");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ERROR", "onDestroy");
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public void dealTime() {
        long sysTime = System.currentTimeMillis();
        CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
        time_txt.setText(sysTimeStr); //更新时间
    }


    //登录成功后等待推送浆员信息
    public void dealWaiting() {
        Log.e("ERROR", "开始--处理等待信号" + fragmentManager.toString());

        setUi(false, true, false);
        title_txt.setText(R.string.fragment_wait_plasm_title);
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

    //切换
        fragmentManager = getFragmentManager();
        WaitingForDonorFragment waitingForDonorFragment = WaitingForDonorFragment.newInstance(getString(R.string.general_welcome), "");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, waitingForDonorFragment).commit();

        Log.e("ERROR", "结束--处理等待信号");
    }

    //收到浆员信息后，认证浆员信息
    public void dealConfirm() {
        Log.e("ERROR", "开始--处理确认信号" + fragmentManager.toString());

        fragmentManager = getFragmentManager();
        AuthFragment authFragment = new AuthFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, authFragment).commit();

        setUi(false, true, false);
        title_txt.setText(R.string.auth);
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        //switch
        AuthPreviewFragment authPreviewFragment = new AuthPreviewFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_auth_container, authPreviewFragment).commit();
        Log.e("ERROR", "结束--处理确认信号");
    }

    //认证通过后，进入等待加压状态
    public void dealAuthPass() {
        Log.e("ERROR", "开始--处理认证通过信号" + fragmentManager.toString());

        fragmentManager.beginTransaction().replace(R.id.fragment_auth_container, new BlankFragment()).commit();

        setUi(false, true, false);
        title_txt.setText(R.string.fragment_welcome_plasm_title);
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        Donor donor = Donor.getInstance();
        String name = donor.getUserName();
        String sloganone = MainActivity.this.getString(R.string.sloganoneabove);
        String slogantwo = MainActivity.this.getString(R.string.sloganonebelow);
        WelcomePlasmFragment welcomeFragment = WelcomePlasmFragment.newInstance(name, sloganone);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, welcomeFragment).commit();
//        fragmentManager.beginTransaction().replace(R.id.fragment_container, new EndFragment()).commit();
        Log.e("ERROR", "结束--处理认证通过信号");

    }

    //收到加压信号，进入等待穿刺状态
    public void dealCompression() {
        Log.e("ERROR", "开始--处理加压信号" );

        setUi(true, true, false);
        title_txt.setText(R.string.fragment_pressing_title);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        left_hint_view.setVisibility(View.VISIBLE);

        //switch
        PressingFragment pressingFragment = new PressingFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, pressingFragment).commit();
        collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
        fragmentManager.beginTransaction().replace(R.id.fragment_record_container, collectionPreviewFragment).commit();

        Log.e("ERROR", "结束--处理加压信号");

    }

    //             处理穿刺
    public void dealPuncture() {

        Log.e("ERROR", "dealPuncture");
        setUi(true, true, false);
        title_txt.setText(R.string.fragment_puncture_video);

        //switch
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance("/sdcard/donation.mp4", "PunctureVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
        if (collectionPreviewFragment == null) {
            collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
            fragmentManager.beginTransaction().replace(R.id.fragment_record_container, collectionPreviewFragment).commit();
        }
    }


    //处理开始采集信号
    public void dealStart() {

        Log.e("ERROR", "dealStart");
        setUi(true, true, false);
        title_txt.setText(R.string.fragment_collect_title);

        //switch
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectionFragment()).commit();
        if (collectionPreviewFragment == null) {
            collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
            fragmentManager.beginTransaction().replace(R.id.fragment_record_container, collectionPreviewFragment).commit();
        }
    }

    //处理开始采集后自动播放视频
    public void dealStartCollcetionVideo(String path) {

        Log.e("ERROR", "dealStartCollcetionVideo");

        setUi(true, true, false);
        title_txt.setText(R.string.play_video);

        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);

        //这里需要修改为信号发送
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BACKTOVIDEOLIST);
            }
        });

        //switch
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(path, "StartCollcetionVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    public void dealStartVideo() {

        Log.e("ERROR", "dealStartVideo");


        setUi(true,true,false);
        title_txt.setText(R.string.play_video);

        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);

        //这里需要修改为信号发送
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BACKTOVIDEOLIST);
            }
        });

        //switch
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(VideoPathEntity.videoPath, "selectVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }


    //观看影片
    public void dealToVideo() {

        title_txt.setText(R.string.watch_film);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new VideoFragment()).commit();
    }

    //上网娱乐
    public void dealToSurf() {

        title_txt.setText(R.string.surf_internet);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new SurfInternetFragment()).commit();
    }

    //建议评价
    public void dealToAdvice() {

        title_txt.setText(R.string.advice);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdviceFragment()).commit();
    }

    //预约服务
    public void dealToAppointment() {

        title_txt.setText(R.string.appointment);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AppointmentFragment()).commit();
    }

    //握拳
    public void dealStartFist() {

        if (startFist != null) {

            startFist.finishAni();
        }

        ivStartFistHint = (ImageView) findViewById(R.id.iv_start_fist);
        ivStartFistHint.setVisibility(View.VISIBLE);

        startFist = new AniThread(this, ivStartFistHint, "startfist.gif", 150);
        startFist.startAni();
    }

    //停止握拳
    public void dealStopFist() {
        if (startFist != null) {
            startFist.finishAni();
        }
        ivStartFistHint.setVisibility(View.INVISIBLE);
    }

    //从视频播放界面返回视频列表
    public void dealBackToVideoList() {
        setUi(true, true, true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.watch_film);
        ivLogoAndBack.setEnabled(false);

        //switch
        VideoFragment videoFragment = new VideoFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, videoFragment).commit();
    }

    //处理采浆结束信号
    public void dealEnd() {

        Log.e("ERROR", "开始处理结束信号");
        setUi(false, false, false);

        //hide
        title_bar_view.setVisibility(View.GONE);
        left_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);
        ivStartFistHint.setVisibility(View.GONE);

        //switch
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new EndFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_record_container, new BlankFragment()).commit();
        Log.e("ERROR", "结束处理结束信号");
    }

    public void dealCheck(){
        setUi(false,false,false);
    }

    private void setUi(boolean leftHint, boolean titleBar, boolean tabGroup) {
        if (leftHint) {
            left_hint_view.setVisibility(View.VISIBLE);
        } else {
            left_hint_view.setVisibility(View.GONE);
        }

        if (titleBar) {
            title_bar_view.setVisibility(View.VISIBLE);
        } else {
            title_bar_view.setVisibility(View.GONE);
        }

        if (tabGroup) {
            mGroup.setVisibility(View.VISIBLE);
        } else {
            mGroup.setVisibility(View.GONE);
        }
    }


//
//        //结束服务评价
//        Button btn10 = (Button) findViewById(R.id.btn10);
//        btn10.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                left_hint_view.setVisibility(View.GONE);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverServiceEvaluateFragment()).commit();
//            }
//        });
//


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
                left_hint_view.setVisibility(View.GONE);
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
                left_hint_view.setVisibility(View.GONE);
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

                break;
            case R.id.call_view:
                //呼叫护士提供服务
                dlg_call_service_view.setVisibility(View.VISIBLE);
                break;
//            case R.id.back_img:
//                //返回
//                title_bar_back_view.setVisibility(View.GONE);
//                title_bar_view.setVisibility(View.VISIBLE);
//                fragmentManager.popBackStack();
//                break;
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

    //禁止返回按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            // The donor can't use the BACK button to close the APP.
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
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

    // 重启MainActivity
    private void StartMainActivityAgain() {

        Intent intentToNewMainActivity = new Intent(MainActivity.this, MainActivity.class);
        intentToNewMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intentToNewMainActivity);
        MainActivity.this.finish();

    }
}
