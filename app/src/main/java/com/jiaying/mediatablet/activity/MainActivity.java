package com.jiaying.mediatablet.activity;

import android.app.FragmentManager;
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
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cylinder.www.facedetect.FdAuthActivity;
import com.jiaying.mediatablet.R;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.entity.VideoPathEntity;
import com.jiaying.mediatablet.fragment.AppointmentFragment;
import com.jiaying.mediatablet.fragment.AuthFragment;
import com.jiaying.mediatablet.fragment.AuthenticationFragment;

import com.jiaying.mediatablet.fragment.BlankFragment;
import com.jiaying.mediatablet.fragment.EvaluationInputFragment;
import com.jiaying.mediatablet.fragment.HintFragment;
import com.jiaying.mediatablet.fragment.SuggestionInputFragment;
import com.jiaying.mediatablet.fragment.WaitingPlasmFragment;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalRecord;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalUIHandler;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForDonorState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.thread.AniThread;
import com.jiaying.mediatablet.fragment.AdviceFragment;
import com.jiaying.mediatablet.fragment.AppointmentInputFragment;
import com.jiaying.mediatablet.fragment.CollectionFragment;
import com.jiaying.mediatablet.fragment.FunctionSettingFragment;
import com.jiaying.mediatablet.fragment.OverFragment;
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
    private HintFragment hintFragment;

    private ObserverZXDCSignalRecord observerZXDCSignalRecordAndFilter;
    private ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler;
    private ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread;

    public ObservableZXDCSignalListenerThread getObservableZXDCSignalListenerThread() {
        return observableZXDCSignalListenerThread;
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
    }

    @Override
    protected void initVariables() {
        recordState = new RecordState(this);

        TabletStateContext.getInstance().setCurrentState(WaitingForDonorState.getInstance());

        // Observer Pattern: ObservableZXDCSignalListenerThread(Observer),ObserverZXDCSignalUIHandler(Observer),
        // ObservableZXDCSignalListenerThread(Observable)
        observerZXDCSignalRecordAndFilter = new ObserverZXDCSignalRecord(recordState);
        observerZXDCSignalUIHandler = new ObserverZXDCSignalUIHandler(new SoftReference<MainActivity>(this));
        observableZXDCSignalListenerThread = new ObservableZXDCSignalListenerThread(recordState);

        // Add the observers into the observable object.
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalUIHandler);
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalRecordAndFilter);
        observableZXDCSignalListenerThread.start();


    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        initTitleBar();
        initTabGroup();
        initMainUI();
    }

    private void initLeftView(){

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
                        TabletStateContext.getInstance().handleMessge(observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEO);
                        break;

                    case R.id.btn_surfinternet:
                        //上网
                        TabletStateContext.getInstance().handleMessge(observableZXDCSignalListenerThread, null, null, RecSignal.TOSURF);
                        break;

                    case R.id.btn_sug_eval:
                        //意见
                        TabletStateContext.getInstance().handleMessge(observableZXDCSignalListenerThread, null, null, RecSignal.TOSUGGEST);
                        break;

                    case R.id.btn_appointment:
                        //预约
                        TabletStateContext.getInstance().handleMessge(observableZXDCSignalListenerThread, null, null, RecSignal.TOAPPOINT);
                        break;
                }
            }
        });
    }

    //中间部分的ui初始化
    private void initMainUI() {
        fragmentManager = getFragmentManager();


        dlg_call_service_view = findViewById(R.id.dlg_call_service_view);
        View dlg_call_service_cancle_view = findViewById(R.id.dlg_call_service_view);
        dlg_call_service_cancle_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);
            }
        });

//        fragmentManager.beginTransaction().replace(R.id.fragment_container, new InitializeFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new WaitingPlasmFragment()).commit();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public void dealTime(){
        long sysTime = System.currentTimeMillis();
        CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
        time_txt.setText(sysTimeStr); //更新时间
    }


    //             登录成功后等待推送浆员信息
    public void dealWaiting() {

//        hide
        left_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);

//        show
        title_bar_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.fragment_wait_plasm_title);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

//        switch
        WaitingPlasmFragment waitingPlasmFragment = WaitingPlasmFragment.newInstance(getString(R.string.general_welcome), "");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, waitingPlasmFragment).commit();
    }

    //             收到浆员信息后，人证浆员信息
    public void dealConfirm() {

        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AuthFragment()).commit();

        // hide
        left_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);

        //show
        title_bar_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.auth);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        //switch

        fragmentManager.beginTransaction().replace(R.id.fragment_auth_container, new AuthenticationFragment()).commit();
    }

    //             人证通过后，进入等待加压状态
    public void dealAuthPass() {

        fragmentManager.beginTransaction().replace(R.id.fragment_auth_container, new BlankFragment()).commit();

        // hide
        left_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);

        //show
        title_bar_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.fragment_welcome_plasm_title);

        Donor donor = Donor.getInstance();
        String name = donor.getUserName();
        String sloganone = MainActivity.this.getString(R.string.sloganoneabove);
        String slogantwo = MainActivity.this.getString(R.string.sloganonebelow);
        WelcomePlasmFragment welcomeFragment = WelcomePlasmFragment.newInstance(sloganone, name + ", " + slogantwo);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, welcomeFragment).commit();


    }

    //             收到加压信号，进入等待穿刺状态
    public void dealCompression() {

        // hide
        mGroup.setVisibility(View.GONE);

        //show
        title_bar_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.fragment_pressing_title);

        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        left_hint_view.setVisibility(View.VISIBLE);

        //switch
        PressingFragment pressingFragment = new PressingFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, pressingFragment).commit();
        hintFragment = HintFragment.newInstance("", "");
        fragmentManager.beginTransaction().replace(R.id.fragment_record_container, hintFragment).commit();

    }

    //             处理穿刺
    public void dealPuncture() {


        //hide
        mGroup.setVisibility(View.GONE);
        //show
        title_bar_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.fragment_puncture_video);
        left_hint_view.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);
        //switch
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance("/sdcard/donation.mp4", "PunctureVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
        if (hintFragment == null) {
            hintFragment = HintFragment.newInstance("", "");
            fragmentManager.beginTransaction().replace(R.id.fragment_record_container, hintFragment).commit();
        }
    }

    public void dealStartPunctureVideo(String path) {

//        //hide
//        mGroup.setVisibility(View.GONE);
//        //show
//        title_bar_view.setVisibility(View.VISIBLE);
//        title_txt.setText(R.string.fragment_puncture_video);
//        left_hint_view.setVisibility(View.VISIBLE);
//        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
//        ivLogoAndBack.setEnabled(false);
//        //switch
//        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(path, "PunctureVideo");
//        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    //处理开始采集信号
    public void dealStart() {

        //hide
        mGroup.setVisibility(View.GONE);
        //show
        title_bar_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.fragment_collect_title);
        left_hint_view.setVisibility(View.VISIBLE);

        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);
        //switch
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectionFragment()).commit();
        if (hintFragment == null) {
            hintFragment = HintFragment.newInstance("", "");
            fragmentManager.beginTransaction().replace(R.id.fragment_record_container, hintFragment).commit();
        }
    }

    //处理开始采集后自动播放视频
    public void dealStartCollcetionVideo(String path) {

        //hide
        mGroup.setVisibility(View.GONE);

        //show
        title_bar_view.setVisibility(View.VISIBLE);
        left_hint_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.play_video);

        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);

        //这里需要修改为信号发送
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(observableZXDCSignalListenerThread, null, null, RecSignal.BACKTOVIDEOLIST);
            }
        });

        //switch
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(path, "StartCollcetionVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    public void dealStartVideo() {

        //hide
        mGroup.setVisibility(View.GONE);

        //show
        title_bar_view.setVisibility(View.VISIBLE);
        left_hint_view.setVisibility(View.VISIBLE);
        title_txt.setText(R.string.play_video);

        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);

        //这里需要修改为信号发送
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabletStateContext.getInstance().handleMessge(observableZXDCSignalListenerThread, null, null, RecSignal.BACKTOVIDEOLIST);
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

    //             进入预约录入界面
    public void dealAppointClick() {
        title_txt.setText(R.string.appoint);
        mGroup.setVisibility(View.GONE);
        title_bar_view.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //修改为发送信号
                mGroup.setVisibility(View.VISIBLE);
                ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
                title_txt.setText(R.string.appointment);
                AppointmentFragment appointmentFragment = new AppointmentFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, appointmentFragment).commit();
                ivLogoAndBack.setEnabled(false);
            }
        });
        AppointmentInputFragment appointmentInputFragment = new AppointmentInputFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, appointmentInputFragment).commit();
    }

    //             进入建议录入界面
    public void dealSuggestClick() {
        left_hint_view.setVisibility(View.VISIBLE);

        title_txt.setText(R.string.suggestion);
        mGroup.setVisibility(View.GONE);
        title_bar_view.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //修改为发送信号
                mGroup.setVisibility(View.VISIBLE);
                ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
                title_txt.setText(R.string.advice);
                AdviceFragment adviceFragment = new AdviceFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, adviceFragment).commit();
                ivLogoAndBack.setEnabled(false);
            }
        });
        SuggestionInputFragment suggestionInputFragment = new SuggestionInputFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, suggestionInputFragment).commit();
    }

    //             进入评价录入界面
    public void dealEvaluationClick() {

        left_hint_view.setVisibility(View.VISIBLE);
        title_bar_view.setVisibility(View.VISIBLE);

        mGroup.setVisibility(View.GONE);

        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);
        title_txt.setText(R.string.evalution);

        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //修改为发送信号
                mGroup.setVisibility(View.VISIBLE);
                ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
                title_txt.setText(R.string.advice);
                AdviceFragment adviceFragment = new AdviceFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, adviceFragment).commit();
                ivLogoAndBack.setEnabled(false);
            }
        });
        EvaluationInputFragment evaluationInputFragment = new EvaluationInputFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, evaluationInputFragment).commit();
    }


    //处理录入建议页面的保存按钮
    public void dealSaveSuggestion() {
        mGroup.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.advice);
        AdviceFragment adviceFragment = new AdviceFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, adviceFragment).commit();
        ivLogoAndBack.setEnabled(false);
    }

    //处理录入评价页面的保存按钮
    public void dealSaveEvaluation() {
        mGroup.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.advice);
        AdviceFragment adviceFragment = new AdviceFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, adviceFragment).commit();
        ivLogoAndBack.setEnabled(false);
    }

    //处理录入预约页面的保存按钮
    public void dealSaveAppointment() {
        mGroup.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.advice);
        AppointmentFragment appointmentFragment = new AppointmentFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, appointmentFragment).commit();
        ivLogoAndBack.setEnabled(false);
    }

    //从视频播放界面返回视频列表
    public void dealBackToVideoList() {
        mGroup.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.watch_film);
        VideoFragment videoFragment = new VideoFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, videoFragment).commit();
        ivLogoAndBack.setEnabled(false);
    }

    //处理采浆结束信号
    public void dealEnd() {

        //hide
        title_bar_view.setVisibility(View.GONE);
        left_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);
        ivStartFistHint.setVisibility(View.GONE);

        //switch
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_record_container, new BlankFragment()).commit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
//                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new WaitingPlasmFragment()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }).start();

    }


//
//        //等待献浆元
//        Button btn1 = (Button) findViewById(R.id.btn1);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                left_hint_view.setVisibility(View.GONE);
//                wait_bg.setVisibility(View.GONE);
//                title_txt.setText(R.string.fragment_wait_plasm_title);
//                mGroup.setVisibility(View.GONE);
//                WaitingPlasmFragment waitingPlasmFragment = WaitingPlasmFragment.newInstance(getString(R.string.general_welcome), "");
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, waitingPlasmFragment).commit();
//            }
//        });


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
}
