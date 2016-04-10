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

import com.cylinder.www.facedetect.FdActivity;
import com.jiaying.mediatablet.R;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.fragment.AppointmentFragment;
import com.jiaying.mediatablet.fragment.AuthFragment;
import com.jiaying.mediatablet.fragment.AuthenticationFragment;

import com.jiaying.mediatablet.fragment.BlankFragment;
import com.jiaying.mediatablet.fragment.EvaluationInputFragment;
import com.jiaying.mediatablet.fragment.HintFragment;
import com.jiaying.mediatablet.fragment.SuggestionInputFragment;
import com.jiaying.mediatablet.fragment.WaitingPlasmFragment;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalRecordAndFilter;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalUIHandler;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.utils.FilterSignal;
import com.jiaying.mediatablet.net.utils.RecordState;
import com.jiaying.mediatablet.thread.AniThread;
import com.jiaying.mediatablet.fragment.AdviceFragment;
import com.jiaying.mediatablet.fragment.AppointmentInputFragment;
import com.jiaying.mediatablet.fragment.CollectionFragment;
import com.jiaying.mediatablet.fragment.FunctionSettingFragment;
import com.jiaying.mediatablet.fragment.InitializeFragment;
import com.jiaying.mediatablet.fragment.OverFragment;
import com.jiaying.mediatablet.fragment.PlayVideoFragment;
import com.jiaying.mediatablet.fragment.PressingFragment;
import com.jiaying.mediatablet.fragment.PunctureFragment;
import com.jiaying.mediatablet.fragment.ServerSettingFragment;
import com.jiaying.mediatablet.fragment.SurfInternetFragment;
import com.jiaying.mediatablet.fragment.VideoFragment;
import com.jiaying.mediatablet.fragment.WelcomePlasmFragment;
import com.jiaying.mediatablet.widget.VerticalProgressBar;

import java.lang.ref.SoftReference;


/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, PunctureFragment.PunctureFragmentInteractionListener,
        CollectionFragment.CollectionFragmentInteractionListener, PlayVideoFragment.PlayVideoFragmentInteractionListener

        , AdviceFragment.AdviceFragmentInteractionListener, AppointmentFragment.OnAppointFragmentListener, AuthenticationFragment.OnAuthFragmentInteractionListener {


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
    private ImageView ivLogoAndBack;
    private TextView fun_txt;//功能设置
    private TextView server_txt;//服务器设置
    private TextView net_state_txt;//网络链接状态
    private TextView wifi_not_txt;
    private TextView title_txt;//标题
    private VerticalProgressBar battery_pb;//剩余电量
    private View right_hint_view;//采浆过程状态显示
    private View call_view;//呼叫
    private TextView battery_not_connect_txt;//电源未连接提示
    private ProgressDialog mDialog = null;
    private TextView time_txt;//当前时间
    private VerticalProgressBar collect_pb;//采集进度
    private View dlg_call_service_view;//电话服务view

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
    public void initView() {
        setContentView(R.layout.activity_main);
        initTitleBar();
        initGroup();
        initMain();
    }

    //标题栏初始化
    private void initTitleBar() {
        title_bar_view = findViewById(R.id.title_bar_view);
        ivLogoAndBack = (ImageView) findViewById(R.id.logo_or_back);
        mParentView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        mPopView = getLayoutInflater().inflate(R.layout.popupwin_main, null);
        right_hint_view = findViewById(R.id.left_view);
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
    private void initGroup() {
        mGroup = (RadioGroup) findViewById(R.id.group);
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_video:
                        //视频列表
                        title_txt.setText(R.string.watch_film);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new VideoFragment()).commit();
                        break;
                    case R.id.btn_surfinternet:
                        //上网
                        title_txt.setText(R.string.surf_internet);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new SurfInternetFragment()).commit();
                        break;
                    case R.id.btn_sug_eval:
                        //意见
                        title_txt.setText(R.string.advice);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdviceFragment()).commit();
                        break;
                    case R.id.btn_appointment:
                        //预约
                        title_txt.setText(R.string.appointment);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AppointmentFragment()).commit();
                        break;
                }
            }
        });
    }

    //中间部分的ui初始化
    private void initMain() {
        fragmentManager = getFragmentManager();



        dlg_call_service_view = findViewById(R.id.dlg_call_service_view);
        View dlg_call_service_cancle_view = findViewById(R.id.dlg_call_service_cancle_view);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
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

    public void dealWaiting() {
        right_hint_view.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_wait_plasm_title);
        mGroup.setVisibility(View.GONE);
        WaitingPlasmFragment waitingPlasmFragment = WaitingPlasmFragment.newInstance(getString(R.string.general_welcome), "");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, waitingPlasmFragment).commit();
    }

    public void dealConfirm() {

        // hide

        right_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);

        //
        title_txt.setText(R.string.auth);

        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AuthFragment()).commit();
//        fragmentManager.beginTransaction().replace(R.id.fragment_hint_container, HintFragment.newInstance("", "")).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_hint_container, new AuthenticationFragment()).commit();
    }

    public void dealCompression() {
        right_hint_view.setVisibility(View.VISIBLE);
        mGroup.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_pressing_title);
        PressingFragment pressingFragment = new PressingFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, pressingFragment).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_hint_container, HintFragment.newInstance("", "")).commit();

    }

    public void dealPuncture() {
        right_hint_view.setVisibility(View.VISIBLE);
        mGroup.setVisibility(View.GONE);
        title_txt.setText(R.string.fragment_puncture_title);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new PunctureFragment()).commit();
    }

    public void dealStartPunctureVideo(String path) {

        mGroup.setVisibility(View.GONE);

        title_txt.setText(R.string.fragment_puncture_video);

        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(path, "PunctureVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    public void dealStart() {

        title_txt.setText(R.string.fragment_collect_title);
        mGroup.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectionFragment()).commit();
    }

    public void dealSignalStartCollcetionVideo(String path) {

        right_hint_view.setVisibility(View.VISIBLE);
        mGroup.setVisibility(View.GONE);


        title_txt.setText(R.string.play_video);

        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroup.setVisibility(View.VISIBLE);
                ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
                title_txt.setText(R.string.watch_film);
                VideoFragment videoFragment = new VideoFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, videoFragment).commit();
                ivLogoAndBack.setEnabled(false);
            }
        });
        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(path, "StartCollcetionVideo");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, playVideoFragment).commit();
    }

    public void dealStartFist() {


        if (startFist != null) {

            startFist.finishAni();
        }

        ivStartFistHint = (ImageView) findViewById(R.id.iv_start_fist);
        ivStartFistHint.setVisibility(View.VISIBLE);

        startFist = new AniThread(this, ivStartFistHint, "startfist.gif", 150);
        startFist.startAni();
    }

    public void dealStopFist() {
        if (startFist != null) {
            startFist.finishAni();
        }
        ivStartFistHint.setVisibility(View.INVISIBLE);

    }

    public void dealEnd() {
        title_bar_view.setVisibility(View.GONE);
        right_hint_view.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.fragment_hint_container, new BlankFragment()).commit();
    }


//
//        //等待献浆元
//        Button btn1 = (Button) findViewById(R.id.btn1);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                right_hint_view.setVisibility(View.GONE);
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
//                right_hint_view.setVisibility(View.GONE);
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
                right_hint_view.setVisibility(View.GONE);
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
                right_hint_view.setVisibility(View.GONE);
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
                dlg_call_service_view.setVisibility(View.VISIBLE);
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
    public void onCollectionFragmentInteraction(RecSignal recSignal) {
        observableZXDCSignalListenerThread.recMsg(recSignal);

    }

    @Override
    public void onPlayVideoFragmentInteraction(RecSignal recSignal) {

    }

    @Override
    public void onPunctureFragmentInteraction(RecSignal recSignal) {
        observableZXDCSignalListenerThread.recMsg(recSignal);
    }

    @Override
    public void onAdviceFragmentInteraction(RecSignal recSignal) {
        switch (recSignal) {
            case INPUTSUGGESTION:

                right_hint_view.setVisibility(View.VISIBLE);

                title_txt.setText(R.string.suggestion);
                mGroup.setVisibility(View.GONE);
                title_bar_view.setVisibility(View.VISIBLE);
                ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);
                ivLogoAndBack.setEnabled(true);
                ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                break;

            case INPUTEVALUATION:



                // 这里需要调整为把信号交个listener去处理。

                right_hint_view.setVisibility(View.VISIBLE);
                title_bar_view.setVisibility(View.VISIBLE);

                mGroup.setVisibility(View.GONE);

                ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);
                title_txt.setText(R.string.evalution);

                ivLogoAndBack.setEnabled(true);
                ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                break;
        }
    }


    @Override
    public void onAppointFragmentInteraction(RecSignal recSignal) {
        title_txt.setText(R.string.appoint);
        mGroup.setVisibility(View.GONE);
        title_bar_view.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.jiantou_press);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    public void onAuthFragmentInteraction(RecSignal recSignal) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title_txt.setText(R.string.fragment_welcome_plasm_title);
            }
        });

        Donor donor = Donor.getInstance();
        String name = donor.getUserName();
        String sloganone = MainActivity.this.getString(R.string.sloganoneabove);
        String slogantwo = MainActivity.this.getString(R.string.sloganonebelow);
        WelcomePlasmFragment welcomeFragment = WelcomePlasmFragment.newInstance(sloganone, name + ", " + slogantwo);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, welcomeFragment).commit();

        fragmentManager.beginTransaction().replace(R.id.fragment_hint_container, new BlankFragment()).commit();


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
