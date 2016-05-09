package com.jiaying.mediatablet.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cylinder.www.facedetect.FdAuthActivity;
import com.jiaying.mediatablet.R;

import com.jiaying.mediatablet.entity.DevEntity;
import com.jiaying.mediatablet.entity.DonorEntity;
import com.jiaying.mediatablet.entity.PlasmaWeightEntity;
import com.jiaying.mediatablet.entity.VideoPathEntity;
import com.jiaying.mediatablet.fragment.ServerSettingFragment;
import com.jiaying.mediatablet.fragment.authentication.AuthFragment;
import com.jiaying.mediatablet.fragment.authentication.AuthPreviewFragment;

import com.jiaying.mediatablet.fragment.BlankFragment;
import com.jiaying.mediatablet.fragment.collection.CollectionPreviewFragment;
import com.jiaying.mediatablet.fragment.check.CheckFragment;
import com.jiaying.mediatablet.fragment.collection.VideoListFragment;
import com.jiaying.mediatablet.fragment.end.EndFragment;
import com.jiaying.mediatablet.fragment.authentication.WaitingForDonorFragment;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalUIHandler;
import com.jiaying.mediatablet.net.serveraddress.AbstractServer;
import com.jiaying.mediatablet.net.serveraddress.LogServer;
import com.jiaying.mediatablet.net.serveraddress.LogServerCreator;
import com.jiaying.mediatablet.net.serveraddress.SignalServer;
import com.jiaying.mediatablet.net.serveraddress.SignalServerCreator;
import com.jiaying.mediatablet.net.serveraddress.VideoServer;
import com.jiaying.mediatablet.net.serveraddress.VideoServerCreator;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.StateIndex;
import com.jiaying.mediatablet.net.state.stateswitch.AbstractState;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForAuthState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForCheckOverState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForStartState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.thread.AniThread;
import com.jiaying.mediatablet.fragment.AdviceFragment;
import com.jiaying.mediatablet.fragment.AppointmentFragment;
import com.jiaying.mediatablet.fragment.collection.CollectionFragment;
import com.jiaying.mediatablet.fragment.collection.PlayVideoFragment;
import com.jiaying.mediatablet.fragment.pression.PressingFragment;
import com.jiaying.mediatablet.fragment.collection.SurfInternetFragment;
import com.jiaying.mediatablet.fragment.authentication.WelcomeFragment;
import com.jiaying.mediatablet.utils.AppInfoUtils;
import com.jiaying.mediatablet.utils.MyLog;
import com.jiaying.mediatablet.utils.WifiAdmin;
import com.jiaying.mediatablet.widget.HorizontalProgressBar;
import com.jiaying.mediatablet.widget.VerticalProgressBar;

import java.lang.ref.SoftReference;


/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static AbstractServer logServer;
    public static AbstractServer signalServer;
    public static AbstractServer videoServer;

    private RecordState recordState;
    private FragmentManager fragmentManager;
    private AniThread startFist;
    private FdAuthActivity fdAuthActivity;
    private View title_bar_view;//标题栏
    private View title_bar_back_view;//带返回的标题栏
    private ImageView title_bar_back_img;//返回按钮
    private TextView title_bar_back_txt;//带返回标题栏的标题
    private RadioGroup mGroup;
    private ImageView overflow_image;//弹出功能
    private PopupWindow mPopupWindow;
    private View mPopView;
    private View mParentView;
    private ImageView ivStartFistHint;
    private ImageView ivLogoAndBack;
    private TextView fun_txt;//功能设置
    private TextView server_txt;//参数设置
    private TextView restart_txt;//软件重启
    private TextView net_state_txt;//网络链接状态
    private TextView wifi_not_txt;
    private TextView title_txt;//标题
    private VerticalProgressBar battery_pb;//剩余电量
    private View left_hint_view;//采浆过程状态显示
    private View call_view;//呼叫
    private TextView battery_not_connect_txt;//电源未连接提示
    private ProgressDialog mDialog = null;
    private TextView time_txt;//当前时间
    private HorizontalProgressBar collect_pb;//采集进度
    private View dlg_call_service_view;//电话服务view
    private CollectionPreviewFragment collectionPreviewFragment;
    private LinearLayout ll_cl;

    private PowerManager.WakeLock mWakelock;
    private KeyguardManager km;
    private PowerManager pm;

    //wifi自动连接begin
    private WifiAdmin wifiAdmin = null;
    private static final String SSID = "test";
    private static final String PWD = "123456libo";
    private static final int TYPE = 3;
    //wifi自动连接end

    //告警电量值
    private static final int WARNING_BATTERY_VALUE = 40;
    private DevEntity devEntity;

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
//               正在充电

                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    {
                        //如果没有充电的状态下，判断电量是否充足
                        String state = recordState.getState();
                        boolean isCheckBattery = false;
                        if (TextUtils.isEmpty(state)) {
                            isCheckBattery = true;
                        } else {
                            if (state.equals(StateIndex.WAITINGFORDONOR) || state.equals(StateIndex.WAITINGFORGETRES)) {
                                isCheckBattery = true;
                            }
                        }
                        MyLog.e("ERROR", "recordState " + state + ",isCheckBattery " + isCheckBattery);
                        if (isCheckBattery && batteryLevel <= WARNING_BATTERY_VALUE) {
                            battery_not_connect_txt.setVisibility(View.VISIBLE);
                            battery_not_connect_txt.setText(getString(R.string.battery_low));
                        } else {
                            battery_not_connect_txt.setVisibility(View.GONE);
                        }
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


    @Override
    protected void initVariables() {
        Log.e("ERROR", "开始执行MainActivity中的onCreate()函数");
        wifiAdmin = new WifiAdmin(this);
        fragmentManager = getFragmentManager();

        //记录现场
        recordState = RecordState.getInstance(this);

        //设备号
        devEntity = DevEntity.getInstance();

        //初始化网络
        logServer = new LogServerCreator().creator(this);
        videoServer = new VideoServerCreator().creator(this);
        signalServer = new SignalServerCreator().creator(this);


        initDevEntity();

        recoverDonor(this);


        //开机后处于检查状态（需要完成的工作有：电量是否充足，然后联网）
        TabletStateContext.getInstance().setCurrentState(WaitingForCheckOverState.getInstance());

        //观察者模式
        // Observer Pattern: ObservableZXDCSignalListenerThread(Observer),ObserverZXDCSignalUIHandler(Observer),
        // ObservableZXDCSignalListenerThread(Observable)
        observableZXDCSignalListenerThread = new ObservableZXDCSignalListenerThread(recordState);
        observerZXDCSignalUIHandler = new ObserverZXDCSignalUIHandler(new SoftReference<>(this), this);

        // Add the observers into the observable object.
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalUIHandler);
    }

    private void initDevEntity() {

        SharedPreferences settings;

        settings = this.getPreferences(Context.MODE_PRIVATE);


        DevEntity.getInstance().setAp(settings.getString("ap", "wrong"));
        DevEntity.getInstance().setOrg(settings.getString("org", "*"));
        DevEntity.getInstance().setPassword(settings.getString("password", "123456"));
        DevEntity.getInstance().setServerAp(settings.getString("serverap", "JzDataCenter"));
        DevEntity.getInstance().setServerOrg(settings.getString("serverorg", "*"));
    }

    private void recoverDonor(Activity activity) {
        SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
        DonorEntity.getInstance().setIdName(settings.getString("name", "先生/女士"));
        DonorEntity.getInstance().setDonorID(settings.getString("id", "000000"));
    }


    @Override
    protected void initView() {
        Log.e("ERROR", "initView");
        setContentView(R.layout.activity_main);

        //左侧提示栏
        initLeftView();

        //顶部标题栏
        initTitleBar();

        //选择按钮
        initTabGroup();

        //主内容区
        initMainUI();
    }

    private void initLeftView() {

        //屏幕左侧的提示栏
        left_hint_view = findViewById(R.id.left_view_container);

        //握拳提示图片
//        ivStartFistHint = (ImageView) findViewById(R.id.iv_start_fist);
        ivStartFistHint = (ImageView) this.findViewById(R.id.iv_start_fist);

        //服务请求
        call_view = findViewById(R.id.call_view);
        call_view.setOnClickListener(this);

    }

    //标题栏初始化
    private void initTitleBar() {

        //屏幕顶端的标题栏
        title_bar_view = findViewById(R.id.title_bar_view);

        //标题栏内部左侧的标题栏名字
        title_txt = (TextView) findViewById(R.id.title_txt);
        title_txt.setText(R.string.fragment_wait_check_title);

        //标题栏内部左侧的图标
        ivLogoAndBack = (ImageView) findViewById(R.id.logo_or_back);
        ivLogoAndBack.setEnabled(false);
        mParentView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        mPopView = getLayoutInflater().inflate(R.layout.popupwin_main, null);

        //标题栏内部中间的采集进度
        ll_cl = (LinearLayout) findViewById(R.id.ll_cl);
        collect_pb = (HorizontalProgressBar) findViewById(R.id.collect_pb);
        collect_pb.setProgress(0);

        //标题栏内部右侧的北京时间
        time_txt = (TextView) findViewById(R.id.time_txt);

        //标题栏内部右侧的电量、网络信号。
        battery_pb = (VerticalProgressBar) findViewById(R.id.battery_pb);
        wifi_not_txt = (TextView) findViewById(R.id.wifi_not_txt);
        net_state_txt = (TextView) findViewById(R.id.net_state_txt);
        net_state_txt.setOnClickListener(this);

        //标题栏内部右侧的选择功能设置，服务器地址设置以及重启
        // TODO: 2016/4/29 参数配置及应用重启的功能还未能实现。
        overflow_image = (ImageView) findViewById(R.id.overflow_image);
        overflow_image.setOnClickListener(this);
//        fun_txt = (TextView) mPopView.findViewById(R.id.fun_txt);
//        fun_txt.setOnClickListener(this);

        restart_txt = (TextView) mPopView.findViewById(R.id.restart_txt);
        restart_txt.setOnClickListener(this);
    }

    //初始化tab选择
    private void initTabGroup() {
        mGroup = (RadioGroup) findViewById(R.id.tab_group);
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

        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CheckFragment()).commit();

        battery_not_connect_txt = (TextView) findViewById(R.id.battery_not_connect_txt);

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

        test();
        //启动联网
        observableZXDCSignalListenerThread.start();
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

        setComponentUI(false, true, false, false);

        title_txt.setText(R.string.fragment_wait_plasm_title);
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        //切换
        WaitingForDonorFragment waitingForDonorFragment = WaitingForDonorFragment.newInstance(getString(R.string.general_welcome), "");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, waitingForDonorFragment).commit();

        Log.e("ERROR", "结束--处理等待信号");
    }

    //收到浆员信息后，认证浆员信息
    public void dealConfirm() {
        Log.e("ERROR", "开始--处理确认信号" + fragmentManager.toString());

        //调整出身份证信息和档案信息
        AuthFragment authFragment = new AuthFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, authFragment).commit();

        //调整出认证预览界面
        AuthPreviewFragment authPreviewFragment = new AuthPreviewFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_auth_container, authPreviewFragment).commit();

        //设置显示状态
        setComponentUI(false, true, false, false);

        title_txt.setText(R.string.auth);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        ivLogoAndBack.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.AUTHPASS);
                return false;
            }
        });

        Log.e("ERROR", "结束--处理确认信号");
    }

    //认证通过后，进入等待加压状态
    public void dealAuthPass() {
        Log.e("ERROR", "开始--处理认证通过信号" + fragmentManager.toString());

        //隐藏认证预览界面
        BlankFragment blankFragment = new BlankFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_auth_container, blankFragment).commit();

        //显示欢迎献浆员语句
        String name = DonorEntity.getInstance().getIdName();
        String sloganone = MainActivity.this.getString(R.string.sloganoneabove);
        WelcomeFragment welcomeFragment = WelcomeFragment.newInstance(name, sloganone);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, welcomeFragment).commit();

        //设置显示状态
        setComponentUI(false, true, false, false);
        title_txt.setText(R.string.fragment_welcome_plasm_title);
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        Log.e("ERROR", "结束--处理认证通过信号");
    }

    //收到加压信号，进入等待穿刺状态
    public void dealCompression() {
        Log.e("ERROR", "开始--处理加压信号");


        setComponentUI(true, true, false, true);
        title_txt.setText(R.string.fragment_pressing_title);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);
        collect_pb.setProgress(0);

        //播报加压提示
        PressingFragment pressingFragment = new PressingFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, pressingFragment).commit();

        //调出采集过程中预览画面
        collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
        fragmentManager.beginTransaction().replace(R.id.fragment_record_container, collectionPreviewFragment).commit();

        Log.e("ERROR", "结束--处理加压信号");

    }

    //             处理穿刺
    public void dealPuncture() {

        Log.e("ERROR", "开始---处理穿刺信号");
        setComponentUI(true, true, false, true);
        title_txt.setText(R.string.fragment_puncture_video);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        //开始播放采集视频
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance("/sdcard/donation.mp4", "PunctureVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();

        //如果加压信号跳过了，需要调出采集中预览画面
        if (collectionPreviewFragment == null) {
            collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
            fragmentManager.beginTransaction().replace(R.id.fragment_record_container, collectionPreviewFragment).commit();
        }

        Log.e("ERROR", "结束---处理穿刺信号");
    }


    //处理开始采集信号
    public void dealStart() {

        Log.e("ERROR", "dealStart");
        setComponentUI(true, true, false, true);
        title_txt.setText(R.string.fragment_collect_title);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        //播放采集提示
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectionFragment()).commit();

        //如果加压信号跳过了，需要调出采集中预览画面
        if (collectionPreviewFragment == null) {
            collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
            fragmentManager.beginTransaction().replace(R.id.fragment_record_container, collectionPreviewFragment).commit();
        }
    }

    //处理开始采集后自动播放视频
    public void dealStartCollcetionVideo(String path) {

        Log.e("ERROR", "dealStartCollcetionVideo");

        setComponentUI(true, true, false, true);
        title_txt.setText(R.string.play_video);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);

        //给返回按钮设置回掉函数
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEO);
            }
        });

        //播放默认的采集视频
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(path, "StartCollcetionVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    public void dealStartVideo() {

        Log.e("ERROR", "dealStartVideo");
        setComponentUI(true, true, false, true);
        title_txt.setText(R.string.play_video);

        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);

        //这里需要修改为信号发送
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEO);
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEO);
            }
        });

        //开始播放选择的视频
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(VideoPathEntity.videoPath, "selectVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }


    //观看影片
    public void dealToVideo() {

        setComponentUI(true, true, true, true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.watch_film);
        ivLogoAndBack.setEnabled(false);

        fragmentManager.beginTransaction().replace(R.id.fragment_container, new VideoListFragment()).commit();
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

        if (ivStartFistHint.getVisibility() != View.VISIBLE) {
            ivStartFistHint.setVisibility(View.VISIBLE);

            startFist = new AniThread(this, ivStartFistHint, "startfist.gif", 150);
            startFist.startAni();
        }

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
        setComponentUI(true, true, true, true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.watch_film);
        ivLogoAndBack.setEnabled(false);

        //switch
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new VideoListFragment()).commit();
    }

    //处理采浆结束信号
    public void dealEnd() {

//        recoverDonor(this);

        Log.e("ERROR", "开始处理结束信号");
        setComponentUI(false, false, false, false);

        //hide
        title_bar_view.setVisibility(View.GONE);
        left_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);
        ivStartFistHint.setVisibility(View.GONE);

        //switch
        //这里
        fragmentManager.beginTransaction().replace(R.id.fragment_record_container, new BlankFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new EndFragment()).commit();

        Log.e("ERROR", "结束处理结束信号");
    }


    public void dealCheckStart() {

        //隐藏和显示布局
        setComponentUI(false, false, false, false);

        //// TODO: 2016/4/29 启动检查状态，状态检查完毕后，发送CHECKOVER信号

        //switch

        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CheckFragment()).commit();
        autoWifiConnect();
        //判断设备可用性
        Handler checkHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //模拟检查通过信号
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CHECKOVER);
            }
        };
        checkHandler.sendMessageDelayed(new Message().obtain(), 10000);

    }

    private void autoWifiConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
                        boolean addSucess = wifiAdmin.addNetwork(wifiAdmin
                                .CreateWifiInfo(SSID, PWD, TYPE));
                        if (addSucess) {
                            break;
                        }
                    } else {
                        wifiAdmin.openWifi();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void dealCheckOver() {
        Log.e("ERROR", "开始--处理检查完毕信号" + fragmentManager.toString());
        //隐藏和显示布局
        setComponentUI(false, true, false, false);

        title_txt.setText(R.string.fragment_wait_plasm_title);
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        //切换
        fragmentManager = getFragmentManager();

        //switch
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CheckFragment()).commit();

        TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.GETRES);
        Log.e("ERROR", "结束--处理检查完毕信号" + fragmentManager.toString());
    }


    public void dealGetRes() {
        Log.e("ERROR", "开始--处理收到应答信号" + fragmentManager.toString());

        //隐藏和显示布局
        setComponentUI(false, true, false, false);

        title_txt.setText(R.string.fragment_wait_plasm_title);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        ivLogoAndBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.SETTINGS);
                return false;
            }
        });

        //切换
        fragmentManager = getFragmentManager();
        WaitingForDonorFragment waitingForDonorFragment = WaitingForDonorFragment.newInstance(getString(R.string.general_welcome), "");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, waitingForDonorFragment).commit();

        Log.e("ERROR", "结束--处理收到应答信号" + fragmentManager.toString());
    }

    public void dealSettings() {

        //隐藏和显示布局
        setComponentUI(false, true, false, false);
        title_txt.setText(R.string.fragment_wait_plasm_title);
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        //切换
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new ServerSettingFragment()).addToBackStack(null).commit();
    }

    public void dealReStart() {

        TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.POWEROFF);
        recordState.recCheckStart();
        recordState.commit();
        StartMainActivityAgain();
    }

    public void dealPlasmaWeight() {
        Log.e("ERROR", "dealStart");
        setComponentUI(true, true, false, true);
        title_txt.setText(R.string.fragment_collect_title);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        collect_pb.setProgress(PlasmaWeightEntity.getInstance().getCurWeight());
        collect_pb.setMax(PlasmaWeightEntity.getInstance().getSettingWeight());
    }

    private void setComponentUI(boolean leftHint, boolean titleBar, boolean tabGroup, boolean collection) {
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
        if (collection) {
            ll_cl.setVisibility(View.VISIBLE);
        } else {
            ll_cl.setVisibility(View.INVISIBLE);
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
//            case R.id.fun_txt:
            //功能设置
//                it = new Intent(MainActivity.this, FunctionSettingActivity.class);
//                title_bar_view.setVisibility(View.GONE);
//                title_bar_back_view.setVisibility(View.VISIBLE);
//                title_bar_back_txt.setText(R.string.func_setting);
//                left_hint_view.setVisibility(View.GONE);
//                mGroup.setVisibility(View.GONE);
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new FunctionSettingFragment()).addToBackStack(null).commit();
//                mPopupWindow.dismiss();
//                break;
//            case R.id.server_txt:
//                //服务器设置
////                it = new Intent(MainActivity.this, ServerSettingActivity.class);
////                title_bar_view.setVisibility(View.GONE);
////                title_bar_back_view.setVisibility(View.VISIBLE);
////                title_bar_back_txt.setText(R.string.server_setting);
////                left_hint_view.setVisibility(View.GONE);
////                mGroup.setVisibility(View.GONE);
//
//                mPopupWindow.dismiss();
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, new ServerSettingFragment()).addToBackStack(null).commit();
//
//                break;
            case R.id.restart_txt:
                //重启
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.RESTART);
                break;
            case R.id.overflow_image:
                showPopWindow();
                break;
            case R.id.net_state_txt:
                //检测网络和检查服务器配置
//                recordState
                StartMainActivityAgain();
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


    private void showPopWindow() {
        View view = findViewById(R.id.ll_test);
        view.setVisibility(View.VISIBLE);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mPopView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    false);
            mPopupWindow.setHeight(AppInfoUtils.dip2px(MainActivity.this, 100));
            mPopupWindow.setWidth(AppInfoUtils.dip2px(MainActivity.this, 210));
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);

        }
        mPopupWindow
                .setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                    }
                });
        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        mPopupWindow.showAtLocation(mParentView, Gravity.RIGHT
                        | Gravity.TOP, AppInfoUtils.dip2px(MainActivity.this, 2),
                AppInfoUtils.dip2px(MainActivity.this, 76));
    }

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

    // 重新开启一个MainActivity
    private void StartMainActivityAgain() {

        Intent intentToNewMainActivity = new Intent(MainActivity.this, MainActivity.class);
        intentToNewMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intentToNewMainActivity);
        MainActivity.this.finish();

    }

    //禁止屏幕休眠，但长时间无献浆消息推送过来，屏幕处于低亮度。
    private void forbidLockScreen() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");

        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();  //解锁
        mWakelock.acquire();//点亮
    }

    private void test() {
        //收到浆员信息
        Button btn_send_donor_info = (Button) this.findViewById(R.id.btn_send_donor_info);
        btn_send_donor_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CONFIRM);
            }
        });

        //认证通过
        Button btn_auth_pass = (Button) this.findViewById(R.id.btn_auth_pass);
        btn_auth_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.AUTHPASS);
            }
        });

        //加压
        Button btn_compression = (Button) this.findViewById(R.id.btn_compression);
        btn_compression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.COMPRESSINON);
            }
        });

        //穿刺
        Button btn_puncture = (Button) this.findViewById(R.id.btn_puncture);
        btn_puncture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PUNCTURE);
            }
        });


        //采集开始
        Button btn_collection_start = (Button) this.findViewById(R.id.btn_collection_start);
        btn_collection_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.START);
            }
        });

        //血浆重量
        Button btn_plasma_weight = (Button) this.findViewById(R.id.btn_plasma_weight);
        btn_plasma_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PLASMAWEIGHT);
            }
        });

        //管压过低
        Button btn_pipe_low = (Button) this.findViewById(R.id.btn_pipe_low);
        btn_pipe_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PIPELOW);
            }
        });

        //管压正常
        Button btn_pipe_normal = (Button) this.findViewById(R.id.btn_pipe_normal);
        btn_pipe_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PIPENORMAL);
            }
        });

        //采集结束
        Button btn_collection_end = (Button) this.findViewById(R.id.btn_collection_end);
        btn_collection_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.END);
            }
        });

        //检查通过
        Button btn_check_pass = (Button) this.findViewById(R.id.btn_check_pass);
        btn_check_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CHECKOVER);
            }
        });

    }
}
