package com.jiaying.mediatablet.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jiaying.mediatablet.R;


import com.jiaying.mediatablet.constants.IntentAction;
import com.jiaying.mediatablet.constants.IntentExtra;


import com.jiaying.mediatablet.constants.Status;
import com.jiaying.mediatablet.db.DataPreference;

import com.jiaying.mediatablet.entity.DeviceEntity;
import com.jiaying.mediatablet.entity.DonorEntity;
import com.jiaying.mediatablet.entity.PlasmaWeightEntity;
import com.jiaying.mediatablet.entity.ServerTime;

import com.jiaying.mediatablet.entity.VideoPathEntity;
import com.jiaying.mediatablet.fragment.ServerSettingFragment;
import com.jiaying.mediatablet.fragment.authentication.AuthFragment;
import com.jiaying.mediatablet.fragment.authentication.AuthPreviewFragment;

import com.jiaying.mediatablet.fragment.BlankFragment;
import com.jiaying.mediatablet.fragment.authentication.RecordDonorFragment;
import com.jiaying.mediatablet.fragment.authentication.RecordNurseFragment;
import com.jiaying.mediatablet.fragment.collection.CollectionPreviewFragment;
import com.jiaying.mediatablet.fragment.check.CheckFragment;


import com.jiaying.mediatablet.fragment.collection.VideoCategorizeFragment;

import com.jiaying.mediatablet.fragment.collection.VideoListFragment;
import com.jiaying.mediatablet.fragment.end.EndFragment;
import com.jiaying.mediatablet.fragment.authentication.WaitingForDonorFragment;
import com.jiaying.mediatablet.net.btstate.BTConFailureState;
import com.jiaying.mediatablet.net.btstate.BTConSuccessState;

import com.jiaying.mediatablet.net.btstate.BTclosedState;
import com.jiaying.mediatablet.net.btstate.BluetoothContextState;
import com.jiaying.mediatablet.net.btstate.ConnectBTState;
import com.jiaying.mediatablet.net.btstate.InitialBTState;
import com.jiaying.mediatablet.net.btstate.ScanBTState;
import com.jiaying.mediatablet.net.handler.ObserverZXDCSignalUIHandler;

import com.jiaying.mediatablet.net.serveraddress.LogServer;
import com.jiaying.mediatablet.net.serveraddress.SignalServer;
import com.jiaying.mediatablet.net.serveraddress.VideoServer;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.StateIndex;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;

import com.jiaying.mediatablet.net.state.stateswitch.WaitingForBTConState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForTimestampState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;

import com.jiaying.mediatablet.service.ScanBackupVideoService;

import com.jiaying.mediatablet.service.TimeService;

import com.jiaying.mediatablet.thread.AniThread;
import com.jiaying.mediatablet.fragment.AdviceFragment;
import com.jiaying.mediatablet.fragment.AppointmentFragment;
import com.jiaying.mediatablet.fragment.collection.CollectionFragment;

import com.jiaying.mediatablet.fragment.collection.JCPlayVideoFragment;

import com.jiaying.mediatablet.fragment.pression.PressingFragment;
import com.jiaying.mediatablet.fragment.collection.SurfInternetFragment;
import com.jiaying.mediatablet.fragment.authentication.WelcomeFragment;

import com.jiaying.mediatablet.utils.AppInfoUtils;

import com.jiaying.mediatablet.utils.BrightnessTools;


import com.jiaying.mediatablet.utils.DateTime;

import com.jiaying.mediatablet.utils.MyLog;
import com.jiaying.mediatablet.widget.HorizontalProgressBar;
import com.jiaying.mediatablet.widget.VerticalProgressBar;


import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {


    private RecordState recordState;
    private FragmentManager fragmentManager;
    private AniThread startFist;
    private View title_bar_view;//标题栏
    private RadioGroup mGroup;
    private ImageView overflow_image;//弹出功能
    private PopupWindow mPopupWindow;
    private View mPopView;
    private View mParentView;
    private ImageView ivStartFistHint;
    private ImageView ivLogoAndBack;
    private ImageView ivBack;//解决长按logo和点击logo冲突而增加的返回按钮
    private TextView fun_txt;//功能设置
    private TextView server_txt;//参数设置
    private TextView restart_txt;//软件重启
    private TextView net_state_txt;//网络链接状态
    private TextView wifi_not_txt;
    private TextView title_txt;//标题
    private VerticalProgressBar battery_pb;//剩余电量
    private View left_hint_view;//采浆过程状态显示
    private ImageView iv_call;//呼叫护士
    private TextView battery_not_connect_txt;//电源未连接提示
    private ProgressDialog mDialog = null;
    private TextView time_txt;//当前时间
    private HorizontalProgressBar collect_pb;//采集进度
    private View dlg_call_service_view;//电话服务view
    private CollectionPreviewFragment collectionPreviewFragment;
    private LinearLayout ll_cl;

    //告警电量值

    private static final int WARNING_BATTERY_VALUE = 5;

    //电量检查是否通过
    private boolean batteryIsOk = false;

    private TabletStateContext tabletStateContext;

    private ProgressDialog allocDevDialog = null;
    private AlertDialog failAllocDialog = null;

    private ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler;
    private ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread = null;

    private Integer onSaveInstanceState = new Integer(1);
    private Boolean onSaveInstanceStateBoolean = true;
    //避免fragment的相关操作在onSaveInstanceState之后执行。
    //todo还未在所有需要互斥的函数里添加

    private LinearLayout ll_appoint;
    private TextView tv_date;
    private TextView tv_week;

    /**
     * --服务评价开始--
     **/
    private ImageView iv_eval;

    private FrameLayout fl_service_evalution;
    private ImageView iv_evalution_close;
    private int eval_puncture = Status.STATUS_EVL_DEFAULT;//标志穿刺评价
    private int eval_attitude = Status.STATUS_EVL_DEFAULT;//标志态度评价

    //服务评价之1.穿刺评价
    private Button btn_submit;
    private ImageView iv_good_puncture;
    private ImageView iv_soso_puncture;
    private ImageView iv_terrible_puncture;
    private LinearLayout ll_not_good_puncture;

    //二珍穿刺
    private CheckBox cb_erzhengchuanci;
    //疼痛
    private CheckBox cb_tengtong;
    //动作缓慢
    private CheckBox cb_huanman;

    //服务评价之2.态度评价
    private ImageView iv_good;
    private ImageView iv_soso;
    private ImageView iv_terrible;
    private LinearLayout ll_not_good;


    //不礼貌
    private CheckBox cb_bulimao;
    //辱骂
    private CheckBox cb_ruma;
    /**
     * --服务评价结束--
     **/

    private ImageView iv_rise;
    private ImageView iv_down;

    private TextView tv_service_zhijin;
    private TextView tv_service_tangguo;
    private TextView tv_service_reshui;
    private TextView tv_service_zazhi;
    private TextView tv_service_zixun;
    private TextView tv_service_cancel;


    //====蓝牙操作begin====

    private BluetoothContextState bluetoothContextState;

    private static final String BT_LOG = "bt_log";
    //蓝牙操作的父控件
    private LinearLayout ll_bt_container;
    //蓝牙状态和结果的显示
    private TextView tv_bt_status;
    //蓝牙结果按钮的父控件
    private LinearLayout ll_bt_result_control;
    //蓝牙跳过
    private Button btn_bt_jump;
    //蓝牙重连
    private Button btn_bt_reconnect;
    //蓝牙设置
    private Button btn_bt_set;

    //需要连接的蓝牙名字
    private String bt_name = "";
    private BluetoothAdapter bt_adapter = null;
    private BluetoothDevice currentDevice = null;
    private BluetoothHeadset bt_headset = null;
    private BluetoothA2dp bt_a2dp = null;

    private boolean successHeadset = false;

    private boolean successA2DP = false;


    //蓝牙最多重连次数
    private static final int BT_CONN_COUNT_MAX = 10;
    //当前连接的次数
    private int bt_conn_count = 0;


    private boolean isStartCheckBTFlag = true;

    //====蓝牙操作end=====
    public TabletStateContext getTabletStateContext() {
        return this.tabletStateContext;
    }

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


                //如果没有充电的状态下，判断电量是否充足
                String state = recordState.getState();
                boolean isCheckBattery = false;
                if (TextUtils.isEmpty(state)) {
                    isCheckBattery = true;
                } else {
                    if (state.equals(StateIndex.WAITINGFORDONOR) || state.equals(StateIndex.WAITINGFORCHECKOVER)) {
                        isCheckBattery = true;
                    }
                }
                MyLog.e("error=========", tabletStateContext.getCurrentState().toString());
                MyLog.e("ERROR", "recordState " + state + ",isCheckBattery " + isCheckBattery);
                if (isCheckBattery == true && batteryLevel <= WARNING_BATTERY_VALUE) {
                    MyLog.e("ERROR", "正在检查状态");
                    battery_not_connect_txt.setVisibility(View.VISIBLE);
                    battery_not_connect_txt.setText(getString(R.string.battery_low));
                    batteryIsOk = false;
                    tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.LOWPOWER);
                } else if (isCheckBattery == true && batteryLevel > WARNING_BATTERY_VALUE) {
                    battery_not_connect_txt.setVisibility(View.GONE);
                    batteryIsOk = true;
                } else if (isCheckBattery != true && batteryLevel <= WARNING_BATTERY_VALUE) {
                    batteryIsOk = false;
                } else if (isCheckBattery == true && batteryLevel > WARNING_BATTERY_VALUE) {
                    batteryIsOk = true;
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
            } else if (IntentAction.ACTION_UPDATE_TIME.equals(action)) {


                long sysTime = intent.getLongExtra(IntentExtra.EXTRA_TIME, System.currentTimeMillis());

                //更新顶部栏的时间显示
                time_txt.setText(DateTime.timeStamp2Time(sysTime + ""));
                //更新预约服务上面的日期和星期
                tv_date.setText(DateTime.timeStamp2Date(sysTime + ""));

                tv_week.setText(DateTime.getWeekDay(sysTime + ""));
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {


            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {

                if (bluetoothContextState.getCurrentState() instanceof BTclosedState && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    MyLog.e(BT_LOG, "蓝牙状态值发生改变：" + BluetoothAdapter.getDefaultAdapter().isEnabled());

                    MyLog.e(BT_LOG, "蓝牙打开====设置状态为扫描状态");
                    bluetoothContextState.setCurrentState(new ScanBTState());
                    new startBTDiscoveryThread().start();
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //发现蓝牙设备
                MyLog.e(BT_LOG, "有action_found");
                if (bluetoothContextState.getCurrentState() instanceof ScanBTState) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.e(BT_LOG, "搜索到的蓝牙设备：" + device.getName());
                    // 如果查找到的设备符合要连接的设备，处理
                    if (TextUtils.isEmpty(device.getName())) {
                        Log.e(BT_LOG, "搜索到的蓝牙设名字为null");
                        return;
                    }
                    if (device.getName().equalsIgnoreCase(bt_name)) {
                        Log.e(BT_LOG, "要配对的蓝牙名：" + device.getName());

                        currentDevice = device;
                        // 搜索蓝牙设备的过程占用资源比较多，一旦找到需要连接的设备后需要及时关闭搜索
                        if (bt_adapter != null) {
                            bt_adapter.cancelDiscovery();
                        }
                        // 获取蓝牙设备的连接状态
                        int connectState = device.getBondState();
                        switch (connectState) {
                            // 未配对

                            case BluetoothDevice.BOND_NONE:

                                bluetoothContextState.setCurrentState(new ConnectBTState());
                                // 配对
                                try {

                                    MyLog.e(BT_LOG, "未配对开始配对：开始" + bt_name);
                                    Method createBondMethod = BluetoothDevice.class
                                            .getMethod("createBond");
                                    createBondMethod.invoke(device);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MyLog.e(BT_LOG, "未配对开始配对：出错：" + e.toString());
                                }
                                break;
                            // 已配对
                            case BluetoothDevice.BOND_BONDED:
                                MyLog.e(BT_LOG, "已配对开始连接：开始" + bt_name);
                                btProfileConnect();
                                break;
                        }

                    }

                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // 状态改变的广播
                MyLog.e(BT_LOG, "有ACTION_BOND_STATE_CHANGED");
                MyLog.e(BT_LOG, "状态" + (bluetoothContextState.getCurrentState() instanceof ConnectBTState));

                if (bluetoothContextState.getCurrentState() instanceof ConnectBTState) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName().equalsIgnoreCase(bt_name)) {
                        currentDevice = device;
                        int connectState = device.getBondState();
                        MyLog.e(BT_LOG, "connectState  " + connectState);
                        switch (connectState) {
                            case BluetoothDevice.BOND_NONE:
                                break;
                            case BluetoothDevice.BOND_BONDING:
                                break;
                            case BluetoothDevice.BOND_BONDED:
                                btProfileConnect();
                                break;
                        }
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(IntentAction.ACTION_UPDATE_TIME);

        //bluetooth begin
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

        //bluetooth end
        registerReceiver(receiver, filter);

        //启动北京时间service
        startTimeService();

        //启动扫描backup文件夹视频文件
        startScanBackupVideoService();

        //禁止锁屏
        forbidLockScreen();
    }

    @Override
    protected void initVariables() {
        Log.e("ERROR", "开始执行MainActivity中的onCreate()函数");
        fragmentManager = getFragmentManager();
        tabletStateContext = new TabletStateContext();
        bluetoothContextState = new BluetoothContextState();

        //记录现场
        recordState = RecordState.getInstance(this);

        //如果是断电重启后需要到等待时间信号
        boolean isBoot = getIntent().getBooleanExtra(IntentExtra.EXTRA_BOOT, false);
        if (isBoot) {
            recordState.recTimeStamp();
            recordState.commit();
        }
//        recordState.recConfirm();
//        recordState.commit();

        allocDevDialog = new ProgressDialog(this);


        //总Context实例个数 = Service个数 + Activity个数 + 1（Application对应的Context实例）
        //初始化网络
        LogServer.getInstance().setIdataPreference(new DataPreference(getApplicationContext()));
        SignalServer.getInstance().setIdataPreference(new DataPreference(getApplicationContext()));
        VideoServer.getInstance().setIdataPreference(new DataPreference(getApplicationContext()));

        //初始化设备
        DeviceEntity.getInstance().setDataPreference(new DataPreference(getApplicationContext()));

        //初始化献浆员
        DonorEntity.getInstance().setDataPreference(new DataPreference(getApplicationContext()));

        //开机后处于等待时间信号状态
        tabletStateContext.setCurrentState(WaitingForTimestampState.getInstance());

        // 观察者模式
        // Observer Pattern: ObservableZXDCSignalListenerThread(Observer),ObserverZXDCSignalUIHandler(Observer),
        // ObservableZXDCSignalListenerThread(Observable)
        observableZXDCSignalListenerThread = new ObservableZXDCSignalListenerThread(recordState, tabletStateContext);
        observerZXDCSignalUIHandler = new ObserverZXDCSignalUIHandler(new SoftReference<>(this));

        // Add the observers into the observable object.
        observableZXDCSignalListenerThread.addObserver(observerZXDCSignalUIHandler);
    }


    @Override
    protected void initView() {
        Log.e("ERROR", "initView");

        setContentView(R.layout.activity_main);

//        **************************************************
//        *    *                                           *
//        *    *              顶部标题栏                    *
//        *    *********************************************
//        *    *     观看影片      *       浏览网页         *
//        *    *********************************************
//        *    *                                           *
//        *    *                                           *
//        *    *              主内容区                      *
//        *    *                                           *
//        *    *                                           *
//        **************************************************

        //左侧提示栏
        initLeftView();

        //顶部标题栏
        initTitleBar();

        //选择按钮
        initTabGroup();

        //主内容区
        initMainUI();

        //蓝牙界面
        initBTUI();
    }


    //蓝牙界面
    private void initBTUI() {
        ll_bt_container = (LinearLayout) findViewById(R.id.ll_bt_container);
        tv_bt_status = (TextView) findViewById(R.id.tv_bt_status);
        ll_bt_result_control = (LinearLayout) findViewById(R.id.ll_bt_result_control);
        btn_bt_jump = (Button) findViewById(R.id.btn_bt_jump);
    }

    private void initLeftView() {

        //屏幕左侧的提示栏
        left_hint_view = findViewById(R.id.left_view_container);

        //握拳提示图片
        ivStartFistHint = (ImageView) this.findViewById(R.id.iv_start_fist);

        //预约服务
        ll_appoint = (LinearLayout) findViewById(R.id.ll_appoint);
        ll_appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.e("ERROR", "预约服务 click");
            }
        });
        //预约服务上面的日期和星期
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_week = (TextView) findViewById(R.id.tv_week);


        //服务评价初始化开始--

        fl_service_evalution = (FrameLayout) findViewById(R.id.fl_service_evalution);

        iv_eval = (ImageView) findViewById(R.id.iv_eval);
        iv_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fl_service_evalution.setVisibility(View.VISIBLE);
            }
        });
        ll_not_good_puncture = (LinearLayout) findViewById(R.id.ll_not_good_puncture);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fl_service_evalution.setVisibility(View.GONE);
            }
        });
        iv_evalution_close = (ImageView) findViewById(R.id.iv_evalution_close);
        iv_evalution_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fl_service_evalution.setVisibility(View.GONE);
            }
        });

        iv_good_puncture = (ImageView) findViewById(R.id.iv_good_puncture);
        iv_soso_puncture = (ImageView) findViewById(R.id.iv_soso_puncture);
        iv_terrible_puncture = (ImageView) findViewById(R.id.iv_terrible_puncture);

        iv_good_puncture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eval_puncture = Status.STATUS_EVL_GOOD;
                switchEvalutionButtonVisibility();
                iv_good_puncture.setImageResource(R.mipmap.good_press);
                iv_soso_puncture.setImageResource(R.mipmap.soso);
                iv_terrible_puncture.setImageResource(R.mipmap.terrible);

                ll_not_good_puncture.setVisibility(View.INVISIBLE);


            }
        });
        iv_soso_puncture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eval_puncture = Status.STATUS_EVL_SOSO;
                switchEvalutionButtonVisibility();
                iv_good_puncture.setImageResource(R.mipmap.good);
                iv_soso_puncture.setImageResource(R.mipmap.soso_press);
                iv_terrible_puncture.setImageResource(R.mipmap.terrible);

                ll_not_good_puncture.setVisibility(View.VISIBLE);
            }
        });
        iv_terrible_puncture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eval_puncture = Status.STATUS_EVL_TERRIBLE;
                switchEvalutionButtonVisibility();
                iv_good_puncture.setImageResource(R.mipmap.good);
                iv_soso_puncture.setImageResource(R.mipmap.soso);
                iv_terrible_puncture.setImageResource(R.mipmap.terrible_press);

                ll_not_good_puncture.setVisibility(View.VISIBLE);
            }
        });
        //选择项
        cb_erzhengchuanci = (CheckBox) findViewById(R.id.cb_erzhengchuanci);
        cb_huanman = (CheckBox) findViewById(R.id.cb_huanman);
        cb_tengtong = (CheckBox) findViewById(R.id.cb_tengtong);

        //态度评价
        ll_not_good = (LinearLayout) findViewById(R.id.ll_not_good);
        iv_good = (ImageView) findViewById(R.id.iv_good);
        iv_soso = (ImageView) findViewById(R.id.iv_soso);
        iv_terrible = (ImageView) findViewById(R.id.iv_terrible);

        iv_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eval_attitude = Status.STATUS_EVL_GOOD;
                switchEvalutionButtonVisibility();
                iv_good.setImageResource(R.mipmap.good_press);
                iv_soso.setImageResource(R.mipmap.soso);
                iv_terrible.setImageResource(R.mipmap.terrible);

                ll_not_good.setVisibility(View.INVISIBLE);

            }
        });
        iv_soso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eval_attitude = Status.STATUS_EVL_SOSO;
                switchEvalutionButtonVisibility();
                iv_good.setImageResource(R.mipmap.good);
                iv_soso.setImageResource(R.mipmap.soso_press);
                iv_terrible.setImageResource(R.mipmap.terrible);

                ll_not_good.setVisibility(View.VISIBLE);
            }
        });
        iv_terrible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eval_attitude = Status.STATUS_EVL_TERRIBLE;
                switchEvalutionButtonVisibility();
                iv_good.setImageResource(R.mipmap.good);
                iv_soso.setImageResource(R.mipmap.soso);
                iv_terrible.setImageResource(R.mipmap.terrible_press);
                ll_not_good.setVisibility(View.VISIBLE);
            }
        });

        //选项
        cb_bulimao = (CheckBox) findViewById(R.id.cb_bulimao);
        cb_ruma = (CheckBox) findViewById(R.id.cb_ruma);

        //---服务评价初始化结束---

        //呼叫护士服务请求
        iv_call = (ImageView) findViewById(R.id.iv_call);
        iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.VISIBLE);
            }
        });

        /**** 呼叫护士服务内容  *******/

        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_service_zhijin:
                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TISSUE);
                        break;

                    case R.id.tv_service_reshui:
                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BOILEDWATER);
                        break;

                    case R.id.tv_service_tangguo:
                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CANDY);
                        break;

                    case R.id.tv_service_zazhi:
                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.MAGAZINE);
                        break;

                    case R.id.tv_service_zixun:
                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CONSULTATION);
                        break;
                }

            }
        };


//        1.纸巾
        tv_service_zhijin = (TextView) findViewById(R.id.tv_service_zhijin);
        tv_service_zhijin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);

                MyLog.e("ERROR", "zhijin click");

            }
        });

        //        2.糖果
        tv_service_tangguo = (TextView) findViewById(R.id.tv_service_tangguo);
        tv_service_tangguo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);

                MyLog.e("ERROR", "tangguo click");

            }
        });

        //        3.热水
        tv_service_reshui = (TextView) findViewById(R.id.tv_service_reshui);
        tv_service_reshui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);

                MyLog.e("ERROR", "reshui click");

            }
        });

        //        4.杂志
        tv_service_zazhi = (TextView) findViewById(R.id.tv_service_zazhi);
        tv_service_zazhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);

                MyLog.e("ERROR", "zazhi click");
            }
        });

        //        5咨询
        tv_service_zixun = (TextView) findViewById(R.id.tv_service_zixun);
        tv_service_zixun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);

                MyLog.e("ERROR", "zixun click");
            }
        });

        //        6取消
        tv_service_cancel = (TextView) findViewById(R.id.tv_service_cancel);
        tv_service_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_call_service_view.setVisibility(View.GONE);

                MyLog.e("ERROR", "cancel click");
            }
        });
        /**** 呼叫护士服务内容   *******/
        //调节位置 上
        iv_rise = (ImageView) findViewById(R.id.iv_rise);
        iv_rise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.e("ERROR", "rise");
            }
        });
        //调节位置 下
        iv_down = (ImageView) findViewById(R.id.iv_down);
        iv_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.e("ERROR", "down");
            }
        });
    }


    //服务评价的提交按钮显示判断
    private void switchEvalutionButtonVisibility() {
        if (eval_attitude != Status.STATUS_EVL_DEFAULT && eval_puncture != Status.STATUS_EVL_DEFAULT) {
            btn_submit.setVisibility(View.VISIBLE);
        } else {
            btn_submit.setVisibility(View.INVISIBLE);
        }
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
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.SETTINGS);
                return false;
            }
        });
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
                        //视频分类列表

                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEOCATEGORY);

                        break;

                    case R.id.btn_surfinternet:
                        //上网
                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOSURF);
                        break;
                }
            }
        });
    }

    //中间部分的ui初始化
    private void initMainUI() {

        dlg_call_service_view = findViewById(R.id.dlg_call_service_view);

        switchUiComponent(fragmentManager, R.id.fragment_container, new CheckFragment());

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
        checkStartTimeout();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("ERROR", "开始执行MainActivity中的onPause()函数" + this.toString());
        long start = System.currentTimeMillis();
        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.POWEROFF);
        StartMainActivityAgain();
        long end = System.currentTimeMillis();
        Log.e("ERROR", "结束执行MainActivity中的onPause()函数，耗时：" + (end - start) / 1000.0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ERROR", "开始执行MainActivity中的onStop()函数");
        Log.e("ERROR", "结束执行MainActivity中的onStop()函数");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ERROR", "开始执行MainActivity中的onDestroy()函数");
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        //断开服务
        if (bt_headset != null) {
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.HEADSET, bt_headset);
            bt_headset = null;
        }
        if (bt_a2dp != null) {
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, bt_a2dp);
            bt_a2dp = null;
        }
        Log.e("ERROR", "开始执行MainActivity中的onDestroy()函数");
    }

//    @Override
//    protected synchronized void onSaveInstanceState(Bundle outState) {
//        synchronized (onSaveInstanceState) {
//            onSaveInstanceStateBoolean = false;
//            super.onSaveInstanceState(outState);
//        }
//    }

    public synchronized void dealTime() {

        Log.e("ERROR", "开始--处理时间戳信号" + this.toString());
        startTimeService();
        Log.e("ERROR", "结束--处理时间戳信号");
    }

    public synchronized void dealBTCon() {


//        蓝牙连接成功：发送CHECKSTART
//        蓝牙连接失败：发送BTCONFAILURE

        Log.e(BT_LOG, "开始--处理蓝牙连接" + this.toString());
        bluetoothContextState.setCurrentState(new BTclosedState());
        successA2DP = false;
        successHeadset = false;
        bt_conn_count = 0;
        DataPreference dataPreference = new DataPreference(this);
        bt_name = dataPreference.readStr("bluetooth_name");
        MyLog.e(BT_LOG, "setting中保存的蓝牙名字：" + bt_name);

        new AutoBTConThread().start();

        if (isStartCheckBTFlag) {
            new CheckConnStateThread().start();
            isStartCheckBTFlag = false;
        }

        ll_bt_container.setVisibility(View.VISIBLE);
        ll_bt_result_control.setVisibility(View.GONE);
        tv_bt_status.setText(R.string.bt_connecting);

        Log.e(BT_LOG, "结束--处理蓝牙连接");
    }


    private class AutoBTConThread extends Thread {

        @Override
        public void run() {
            super.run();
            bt_adapter = BluetoothAdapter.getDefaultAdapter();
            if (bt_adapter == null) {
                // 设备不支持蓝牙
                MyLog.e(BT_LOG, "不支持蓝牙");
                return;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bluetoothContextState.setCurrentState(new InitialBTState());

            if (bt_adapter.isEnabled()) {
                MyLog.e(BT_LOG, "蓝牙已经打开");
                if (closeOpendBT(bt_adapter, 5)) {
                    MyLog.e(BT_LOG, "蓝牙关闭成功====再次打开蓝牙");
                } else {
                    MyLog.e(BT_LOG, "蓝牙关闭失败====返回");
                    return;
                }
            }

            MyLog.e(BT_LOG, "设置状态为关闭状态");
            bluetoothContextState.setCurrentState(new BTclosedState());

            if (openClosedBT(bt_adapter, 5)) {
                MyLog.e(BT_LOG, "蓝牙打开成功");
            } else {
                MyLog.e(BT_LOG, "蓝牙打开失败");
                return;
            }
        }
    }


    private class CheckConnStateThread extends Thread {

        private int n = 2;

        @Override
        public void run() {
            super.run();

            do {
                try {
                    sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!(bluetoothContextState.getCurrentState() instanceof BTConSuccessState)) {
                    tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BTCONSTART);
                }
            } while (--n > 0);
            tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BTCONFAILURE);
        }
    }


    private Boolean closeOpendBT(BluetoothAdapter bluetoothAdapter, int n) {
        MyLog.e(BT_LOG, "closeOpendBT");
        //每次尝试打开蓝牙前停顿2S
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        打开5次都不能打开成功就认为是蓝牙坏了

        if (n < 1) {
            return false;
        }

        //打开关闭的蓝牙
        if (bluetoothAdapter.disable()) {
            return true;
        } else {
//            打开失败使用递归方式继续打开
            return closeOpendBT(bluetoothAdapter, n--);
        }
    }

    private Boolean openClosedBT(BluetoothAdapter bluetoothAdapter, int n) {
        MyLog.e(BT_LOG, "openClosedBT");
        //每次尝试打开蓝牙前停顿2S
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        打开5次都不能打开成功就认为是蓝牙坏了

        if (n < 1) {
            return false;
        }

        //打开关闭的蓝牙
        if (bluetoothAdapter.enable()) {
            return true;
        } else {
//            打开失败使用递归方式继续打开
            return openClosedBT(bluetoothAdapter, n--);
        }
    }

    /*
蓝牙扫描
 */
    private class startBTDiscoveryThread extends Thread {
        @Override
        public void run() {
            super.run();
            bt_adapter = BluetoothAdapter.getDefaultAdapter();

            //停顿4S
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //开始扫描
            if (bt_adapter.startDiscovery()) {
                MyLog.e(BT_LOG, "开始扫描成功");
            } else {
                MyLog.e(BT_LOG, "开始扫描失败");
                startDis(bt_adapter, 3);
            }
        }
    }

    private boolean startDis(BluetoothAdapter bluetoothAdapter, int n) {
        //每次尝试开始扫描蓝牙前停顿2S
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//       扫描5次都不能扫描成功就认为是蓝牙坏了
        if (n < 1) {
            return false;
        }

        //开始扫描蓝牙
        if (bluetoothAdapter.startDiscovery()) {
            return true;
        } else {
//            扫描失败使用递归方式继续扫描
            return startDis(bluetoothAdapter, n--);
        }
    }

    private void btProfileConnect() {
        bt_adapter.getProfileProxy(MainActivity.this, new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile,
                                           BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    bt_headset = (BluetoothHeadset) proxy;
                    try {
                        Method m = bt_headset.getClass()
                                .getDeclaredMethod("connect",
                                        BluetoothDevice.class);
                        m.setAccessible(true);
                        // 连接Headset
                        successHeadset = (Boolean) m.invoke(
                                bt_headset, currentDevice);
                        MyLog.e(BT_LOG, "BluetoothHeadset 连接结果：" + successHeadset
                                + ",device:" + currentDevice.getName());
                        checkBTConnState(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MyLog.e(BT_LOG, "BluetoothHeadset连接结果出错" + e.toString());
                    }

                }
            }

            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HEADSET) {
                    bt_headset = null;
                }
            }
        }, BluetoothProfile.HEADSET);

        bt_adapter.getProfileProxy(MainActivity.this, new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    bt_a2dp = (BluetoothA2dp) proxy;
                    try {
                        MyLog.e(BT_LOG, "a2dp开始连接" + currentDevice);
                        Method m = bt_a2dp.getClass().getDeclaredMethod(
                                "connect", BluetoothDevice.class);
                        m.setAccessible(true);
                        // 连接a2dp
                        successA2DP = (Boolean) m.invoke(
                                bt_a2dp, currentDevice);
                        MyLog.e(BT_LOG, "a2dp连接结果：" + successA2DP
                                + ",device:" + currentDevice.getName());
                        checkBTConnState(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MyLog.e(BT_LOG, "a2dp连接结果出错" + e.toString());
                    }

                }
            }

            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.A2DP) {
                    bt_a2dp = null;
                }
            }
        }, BluetoothProfile.A2DP);

    }

    //判断蓝牙是否连接成功，标准1.headset成功2.a2dp成功

    private boolean checkBTConnState(boolean isChangeState) {
        boolean sucess = false;
        if (successHeadset && successA2DP) {
            //成功连接
            MyLog.e(BT_LOG, "连接成功");
            if (isChangeState) {
                ll_bt_container.setVisibility(View.GONE);
                bluetoothContextState.setCurrentState(new BTConSuccessState());
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CHECKSTART);
            }

            sucess = true;
        } else {
            //连接失败
            MyLog.e(BT_LOG, "连接失败");
            if (isChangeState) {
                bluetoothContextState.setCurrentState(new BTConFailureState());
            }
            sucess = false;
        }
        return sucess;
    }


    public synchronized void dealBTConFailure() {
//        跳过就进入：发送CHECKSTART
        // 进入设置界：SETTING
//        重连:BTCONSTART

        ll_bt_container.setVisibility(View.VISIBLE);
        tv_bt_status.setText(R.string.bt_connect_fail);
        ll_bt_result_control.setVisibility(View.VISIBLE);


        btn_bt_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_bt_container.setVisibility(View.GONE);
                ll_bt_result_control.setVisibility(View.GONE);
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CHECKSTART);
            }
        });

        btn_bt_reconnect = (Button) findViewById(R.id.btn_bt_reconnect);
        btn_bt_reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_bt_container.setVisibility(View.GONE);
                ll_bt_result_control.setVisibility(View.GONE);

                isStartCheckBTFlag = true;
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BTCONSTART);

            }
        });
        btn_bt_set = (Button) findViewById(R.id.btn_bt_set);
        btn_bt_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_bt_container.setVisibility(View.GONE);
                ll_bt_result_control.setVisibility(View.GONE);
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.SETTINGS);
            }
        });
    }

    public void dealCheckStart() {
        Log.e("ERROR", "开始--处理开始检查信号");

        //设置显示状态

        setBrightnessLow();

        showUiComponent(false, true, false, false);

        //界面切换：
        switchUiComponent(fragmentManager, R.id.fragment_container, new CheckFragment());

        //设置文字内容
        title_txt.setText(R.string.check2);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.SETTINGS);
                return false;
            }
        });

        //启动相关动作
        checkdBattery();
        Log.e("ERROR", "结束--处理开始检查信号");
    }

    //如果20秒得不到时间信号，那么自动跳转到参数设置界面。
    private void checkStartTimeout() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TIMESTAMPTIMEOUT);
            }
        }).start();
    }

    //收到浆员信息后，认证浆员信息
    public synchronized void dealConfirm() {
        Log.e("ERROR", "开始--处理确认信号" + fragmentManager.toString());

        setBrightnessNormal();

        //设置显示状态
        showUiComponent(false, true, false, true);

        //界面切换：
        // 调整出身份证信息和档案信息
        AuthFragment authFragment = new AuthFragment();
        switchUiComponent(fragmentManager, R.id.fragment_container, authFragment);

        //调整出认证预览界面
        AuthPreviewFragment authPreviewFragment = new AuthPreviewFragment();
        switchUiComponent(fragmentManager, R.id.fragment_auth_container, authPreviewFragment);

        //设置文字内容
        title_txt.setText(R.string.auth);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setOnClickListener(null);
        ivLogoAndBack.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.RECORDDONORVIDEO);
//                // TODO: 2016/5/20 录制献浆员视频和护士视频的模块做好后，调整为发送RECORDDONORVIDEO命令
//                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.AUTHPASS);
                return false;
            }
        });

        collect_pb.setProgress(0);
        collect_pb.setMax(PlasmaWeightEntity.getInstance().getSettingWeight());

        Log.e("ERROR", "结束--处理确认信号");
    }

    public synchronized void dealLowPower() {
        Log.e("ERROR", "开始--处理电量低");


        if (onSaveInstanceStateBoolean) {

            //设置显示状态
            showUiComponent(false, true, false, false);

            //界面切换：
            //调出企业文化界面
            switchUiComponent(fragmentManager, R.id.fragment_container, new CheckFragment());

            //设置文字内容
            title_txt.setText(R.string.check1);

            //设置logo按钮事件
            ivLogoAndBack.setEnabled(true);
            ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
            ivLogoAndBack.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.SETTINGS);
                    return false;
                }
            });

            checkdBattery();
        }


        Log.e("ERROR", "结束--处理电量低");
    }


    public synchronized void dealRecordDonorVideo() {
        Log.e("ERROR", "开始--未认证——录制浆员视频");
        //设置显示状态
        showUiComponent(false, true, false, false);

        // 调整录制浆员信息
        RecordDonorFragment recordDonorFragment = new RecordDonorFragment();
        switchUiComponent(fragmentManager, R.id.fragment_container, recordDonorFragment);

        //隐藏认证预览界面
        BlankFragment blankFragment = new BlankFragment();
        switchUiComponent(fragmentManager, R.id.fragment_auth_container, blankFragment);
        //设置文字内容
        title_txt.setText(R.string.record_donor);

        //设置返回按钮事件

        ivLogoAndBack.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CONFIRM);
            }
        });
        Log.e("ERROR", "结束--处理录制浆员视频信号");
    }

    public synchronized void dealRecordNurseVideo() {
        Log.e("ERROR", "开始--未认证——录制护士视频");
        //设置显示状态
        showUiComponent(false, true, false, false);

        // 调整录制护士信息
        RecordNurseFragment recordNurseFragment = new RecordNurseFragment();
        switchUiComponent(fragmentManager, R.id.fragment_container, recordNurseFragment);

        //隐藏认证预览界面
        BlankFragment blankFragment = new BlankFragment();
        switchUiComponent(fragmentManager, R.id.fragment_auth_container, blankFragment);
        //设置文字内容
        title_txt.setText(R.string.record_norse);
        ivLogoAndBack.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CONFIRM);
            }
        });
        Log.e("ERROR", "结束--未认证——录制护士视频信息");
    }


    //认证通过后，等待服务器接收到通过信号
    public synchronized void dealAuthPass() {

        Log.e("ERROR", "开始--处理认证通过信号" + fragmentManager.toString());

        if (onSaveInstanceStateBoolean) {

            //设置显示状态
            showUiComponent(false, true, false, false);

            //界面切换：

            //设置文字内容
            title_txt.setText(R.string.authres);

            //设置logo按钮事件
            ivLogoAndBack.setEnabled(false);
            ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

            //启动相关动作
            showProgress("认证通过，等待应答！", "服务器（**）\n单采机（**）");

            CountDownTimer countDownTimer = new CountDownTimer(11000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    disAllocDevDialog();
                    tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.AUTHRESTIMEOUT);
                }
            };
            countDownTimer.start();
        }

        Log.e("ERROR", "结束--处理认证通过信号" + fragmentManager.toString());
    }

    public synchronized void dealAuthResTimeout() {

        Log.e("ERROR", "开始--处理人证应答超时" + fragmentManager.toString());
        if (onSaveInstanceStateBoolean) {
            //设置显示状态
            showUiComponent(false, true, false, false);

            //界面切换：
            showTimeoutDialog("应答失败！", "超时");

            //设置文字内容
            title_txt.setText(R.string.authres);

            //设置logo按钮事件
            ivLogoAndBack.setEnabled(false);
            ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

            //启动相关动作
        }
    }

    public synchronized void dealSerAuthRes() {

        //设置显示状态
        showUiComponent(false, true, false, false);

        //界面切换：
        //调整应答
        showProgress("认证通过，等待应答！", "服务器（应答）\n单采机（**）");
        //设置文字内容
        title_txt.setText(R.string.authres);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        //启动相关动作

    }

    public synchronized void dealZxdcAuthRes() {

        //设置显示状态
        showUiComponent(false, true, false, false);

        //界面切换：
        showProgress("认证通过，等待应答！", "服务器（**）\n单采机（应答）");

        //设置文字内容
        title_txt.setText(R.string.authres);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        //启动相关动作
    }

    public synchronized void dealAuthResOk() {

        Log.e("ERROR", "开始--处理认证通过信号" + fragmentManager.toString());
        //设置显示状态
        showUiComponent(false, true, false, true);

        //界面切换：
        String name = DonorEntity.getInstance().getIdentityCard().getName();
        String sloganone = MainActivity.this.getString(R.string.sloganoneabove);
        WelcomeFragment welcomeFragment = WelcomeFragment.newInstance(name, sloganone);
        switchUiComponent(fragmentManager, R.id.fragment_container, welcomeFragment);
        //隐藏认证预览界面
        BlankFragment blankFragment = new BlankFragment();
        switchUiComponent(fragmentManager, R.id.fragment_auth_container, blankFragment);

        showProgress("认证通过，等待应答！", "服务器（应答）\n单采机（应答）");
        disAllocDevDialog();

        //设置文字内容
        title_txt.setText(R.string.fragment_welcome_plasm_title);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        Log.e("ERROR", "结束--处理认证通过信号" + fragmentManager.toString());
    }

    //    ***********************对话框***************************************
    //显示认证应答进度框
    private void showProgress(String title, String msg) {
        if (isFinishing()) {
            return;
        }
        if (allocDevDialog == null) {
            allocDevDialog = new ProgressDialog(this);
        }
        allocDevDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        allocDevDialog.setTitle(title);
        allocDevDialog.setMessage(msg);
        allocDevDialog.setIcon(R.mipmap.ic_launcher);


//        设置点击进度对话框外的区域对话框不消失
        allocDevDialog.setCanceledOnTouchOutside(false);
        allocDevDialog.setIndeterminate(false);
        allocDevDialog.setCancelable(false);
        allocDevDialog.show();
    }

    //关闭分配中对话框
    private void disAllocDevDialog() {
        if (!isFinishing()) {
            if (allocDevDialog != null) {
                allocDevDialog.dismiss();
                allocDevDialog = null;
            }
        }
    }

    //超时对话框
    private void showTimeoutDialog(String title, String msg) {
        if (isFinishing()) {
            return;
        }
        AlertDialog.Builder failAllocDialogBuilder = new AlertDialog.Builder(this);
        failAllocDialogBuilder.setIcon(R.mipmap.ic_launcher);
        failAllocDialogBuilder.setTitle(title);
        failAllocDialogBuilder.setMessage(msg);


        failAllocDialogBuilder.setPositiveButton("重发", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.REAUTHPASS);
            }
        });
        failAllocDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CANCLEAUTHPASS);
            }
        });
        if (failAllocDialog == null) {
            failAllocDialog = failAllocDialogBuilder.create();
        }

        failAllocDialog.setCanceledOnTouchOutside(false);
        failAllocDialog.setCancelable(false);
        failAllocDialog.show();
    }
    //    ***********************对话框***************************************


    //收到加压信号，进入等待穿刺状态
    public synchronized void dealCompression() {
        Log.e("ERROR", "开始--处理加压信号");
        //设置显示状态
        showUiComponent(true, true, false, true);

        //界面切换：
        //调出企业文化界面
        //播报加压提示
        PressingFragment pressingFragment = new PressingFragment();
        switchUiComponent(fragmentManager, R.id.fragment_container, pressingFragment);


        //调出采集过程中预览画面
        collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
        switchUiComponent(fragmentManager, R.id.fragment_record_container, collectionPreviewFragment);

        //设置文字内容
        title_txt.setText(R.string.fragment_pressing_title);

        //设置logo按钮事件
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        //启动相关动作
        Log.e("ERROR", "结束--处理加压信号");

    }

    //             处理穿刺
    public synchronized void dealPuncture() {

        Log.e("ERROR", "开始---处理穿刺信号");
        //设置显示状态
        showUiComponent(true, true, false, true);

        //界面切换：
        //调出企业文化界面
        //开始播放采集视频

        JCPlayVideoFragment playVideoFragment = JCPlayVideoFragment.newInstance("/sdcard/donation.mp4", "PunctureVideo");
//        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance("/sdcard/donation.mp4", "PunctureVideo");

        switchUiComponent(fragmentManager, R.id.fragment_container, playVideoFragment);
        //如果加压信号跳过了，需要调出采集中预览画面
        if (collectionPreviewFragment == null) {
            collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
            switchUiComponent(fragmentManager, R.id.fragment_record_container, collectionPreviewFragment);
        }

        //设置文字内容
        title_txt.setText(R.string.fragment_puncture_video);

        //设置logo按钮事件
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        //启动相关动作
        Log.e("ERROR", "结束---处理穿刺信号");
    }


    //处理开始采集信号
    public synchronized void dealStart() {

        Log.e("ERROR", "开始————处理开采信号");
        //设置显示状态
        showUiComponent(true, true, false, true);

        //界面切换：
        //播放采集提示
        switchUiComponent(fragmentManager, R.id.fragment_container, new CollectionFragment());
        //如果加压信号跳过了，需要调出采集中预览画面
        if (collectionPreviewFragment == null) {
            collectionPreviewFragment = CollectionPreviewFragment.newInstance("", "");
            switchUiComponent(fragmentManager, R.id.fragment_record_container, collectionPreviewFragment);
        }
        //设置文字内容
        title_txt.setText(R.string.fragment_collect_title);
        collect_pb.setProgress(PlasmaWeightEntity.getInstance().getCurWeight());
        collect_pb.setMax(PlasmaWeightEntity.getInstance().getSettingWeight());
        //设置logo按钮事件
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);

        Log.e("ERROR", "结束————处理开采信号");
    }

    public synchronized void dealStartCollcetionVideo(String path) {

        Log.e("ERROR", "开始--播放采集开始视频");

        //设置显示状态
        showUiComponent(true, true, false, true);

        //界面切换：
        //播放默认的采集视频
        switchUiComponent(fragmentManager, R.id.fragment_container, new VideoListFragment());

        JCPlayVideoFragment playVideoFragment = JCPlayVideoFragment.newInstance(path, "StartCollcetionVideo");
//        PlayVideoFragment playVideoFragment = PlayVideoFragment.newInstance(path, "StartCollcetionVideo");

        switchUiComponent(fragmentManager, R.id.fragment_container, playVideoFragment);

        //设置文字内容
        title_txt.setText(R.string.play_video);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(true);

        ivLogoAndBack.setImageResource(R.drawable.iv_back_selector);

        //给返回按钮设置回掉函数
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEOCATEGORY);

            }
        });

        //启动相关动作
        collect_pb.setProgress(PlasmaWeightEntity.getInstance().getCurWeight());
        collect_pb.setMax(PlasmaWeightEntity.getInstance().getSettingWeight());
        Log.e("ERROR", "结束--播放采集开始视频");
    }

    public synchronized void dealStartVideo() {

        Log.e("ERROR", "开始--播放视频");
        //设置显示状态
        showUiComponent(true, true, false, true);
        //界面切换：

        JCPlayVideoFragment playVideoFragment = JCPlayVideoFragment.newInstance(VideoPathEntity.videoPath, "selectVideo");
//        PlayVideoFragment playVideoFragment =PlayVideoFragment.newInstance(VideoPathEntity.videoPath, "selectVideo");

        switchUiComponent(fragmentManager, R.id.fragment_container, playVideoFragment);

        //设置文字内容
        title_txt.setText(R.string.play_video);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(true);

        ivLogoAndBack.setImageResource(R.drawable.iv_back_selector);

        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEOLIST);
            }
        });
        //启动相关动作
        Log.e("ERROR", "开始--播放视频");
    }

    //观看影片
    public synchronized void dealToVideoList() {
        Log.e("ERROR", "开始--返回播放列表");
        showUiComponent(true, true, false, true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        title_txt.setText(R.string.video_list);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(true);

        ivLogoAndBack.setImageResource(R.drawable.iv_back_selector);
        ivLogoAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.TOVIDEOCATEGORY);

            }
        });

        switchUiComponent(fragmentManager, R.id.fragment_container, new VideoListFragment());
        Log.e("ERROR", "结束--返回播放列表");
    }

    //影片分类
    public synchronized void dealToVideoCategory() {
        Log.e("ERROR", "开始--返回播放分类");
        showUiComponent(true, true, true, true);

        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.watch_film);
        ivLogoAndBack.setEnabled(false);

        switchUiComponent(fragmentManager, R.id.fragment_container, new VideoCategorizeFragment());
        Log.e("ERROR", "结束--返回播放分类");
    }

    //全屏播放
    public synchronized void dealToVideoFullScreen() {
        Log.e("ERROR", "开始--全屏播放");
        showUiComponent(false, false, false, false);


        Log.e("ERROR", "结束--全屏播放");
    }

    //退出全屏播放
    public synchronized void dealToVideoNotFullScreen() {

        Log.e("ERROR", "开始--退出全屏播放");
        showUiComponent(true, true, false, true);


        Log.e("ERROR", "结束--退出全屏播放");
    }

    //上网娱乐
    public synchronized void dealToSurf() {

        title_txt.setText(R.string.surf_internet);
        switchUiComponent(fragmentManager, R.id.fragment_container, new SurfInternetFragment());

    }

    //建议评价
    public synchronized void dealToAdvice() {

        title_txt.setText(R.string.advice);
        switchUiComponent(fragmentManager, R.id.fragment_container, new AdviceFragment());
    }

    //预约服务
    public synchronized void dealToAppointment() {

        title_txt.setText(R.string.appointment);
        switchUiComponent(fragmentManager, R.id.fragment_container, new AppointmentFragment());
    }

    //握拳
    public synchronized void dealStartFist() {

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
        showUiComponent(true, true, true, true);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        title_txt.setText(R.string.watch_film);
        ivLogoAndBack.setEnabled(false);

        //switch
        switchUiComponent(fragmentManager, R.id.fragment_container, new VideoListFragment());
    }

    //处理采浆结束信号
    public void dealEnd() {

        Log.e("ERROR", "开始--处理结束信号");
        //设置显示状态
        showUiComponent(false, true, false, false);

        //界面切换：
        switchUiComponent(fragmentManager, R.id.fragment_record_container, new BlankFragment());
        switchUiComponent(fragmentManager, R.id.fragment_container, new EndFragment());
        PlasmaWeightEntity.getInstance().setCurWeight(0);
        //设置文字内容
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setEnabled(false);
        title_txt.setText(R.string.end);
        //设置logo按钮事件

        //启动相关动作

        //有可能在结束的时候还未关闭握拳提示，如果不关闭则会在后台一直运行该线程。
        if (startFist != null) {
            startFist.finishAni();
        }
        //服务评价要初始化

        eval_puncture = Status.STATUS_EVL_DEFAULT;
        eval_attitude = Status.STATUS_EVL_DEFAULT;
        iv_good_puncture.setImageResource(R.mipmap.good);
        iv_soso_puncture.setImageResource(R.mipmap.soso);
        iv_terrible_puncture.setImageResource(R.mipmap.terrible);

        iv_good.setImageResource(R.mipmap.good);
        iv_soso.setImageResource(R.mipmap.soso);
        iv_terrible.setImageResource(R.mipmap.terrible);

        ll_not_good_puncture.setVisibility(View.INVISIBLE);
        ll_not_good.setVisibility(View.INVISIBLE);
        btn_submit.setVisibility(View.INVISIBLE);
        fl_service_evalution.setVisibility(View.GONE);


        cb_erzhengchuanci.setChecked(false);
        cb_ruma.setChecked(false);
        cb_tengtong.setChecked(false);
        cb_bulimao.setChecked(false);
        cb_huanman.setChecked(false);

        dlg_call_service_view.setVisibility(View.GONE);
        Log.e("ERROR", "结束--处理结束信号");
    }


    //检查电量和wifi是否已经检测通过

    private void checkdBattery() {
//        batteryIsOk = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    if (batteryIsOk) {
                        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CHECKOVER);

                        break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void dealCheckOver() {
        Log.e("ERROR", "开始--处理检查通过信号" + recordState.getState().toString() + " " + fragmentManager.toString());

        setBrightnessLow();

        //设置显示状态
        showUiComponent(false, true, false, false);

        //界面切换：

        //切换
        WaitingForDonorFragment waitingForDonorFragment = WaitingForDonorFragment.newInstance(getString(R.string.general_welcome), "");
        switchUiComponent(fragmentManager, R.id.fragment_container, waitingForDonorFragment);

        BlankFragment blankFragment = new BlankFragment();
        switchUiComponent(fragmentManager, R.id.fragment_auth_container, blankFragment);

        //设置文字内容
        title_txt.setText(R.string.fragment_wait_plasm_title);

        //设置logo按钮事件
        ivBack.setVisibility(View.GONE);
        ivLogoAndBack.setVisibility(View.VISIBLE);
        ivLogoAndBack.setEnabled(true);
        ivLogoAndBack.setVisibility(View.VISIBLE);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);
        ivLogoAndBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.SETTINGS);
                return false;
            }
        });

        //启动相关动作
        Log.e("ERROR", "结束--处理检查通过信号" + fragmentManager.toString());
    }


    public void dealSettings() {

        //设置显示状态
        setBrightnessNormal();
        showUiComponent(false, true, false, false);

        //界面切换：
        switchUiComponent(fragmentManager, R.id.fragment_container, new ServerSettingFragment());

        //设置文字内容
        title_txt.setText(R.string.fragment_wait_plasm_title);

        //设置logo按钮事件
        ivLogoAndBack.setEnabled(false);
        ivLogoAndBack.setImageResource(R.mipmap.ic_launcher);

        //启动相关动作

    }

    public void dealReStart() {

        //设置显示状态


        //界面切换：


        //设置文字内容


        //设置logo按钮事件


        //启动相关动作
        recordState.recTimeStamp();
        recordState.commit();
        tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.POWEROFF);
        StartMainActivityAgain();
    }

    public void dealPlasmaWeight() {
        Log.e("ERROR", "dealPlasmaWeight");

        //设置显示状态

        //界面切换：

        //设置文字内容

        //设置logo按钮事件

        //启动相关动作

        collect_pb.setProgress(PlasmaWeightEntity.getInstance().getCurWeight());
        collect_pb.setMax(PlasmaWeightEntity.getInstance().getSettingWeight());

    }

    private void showUiComponent(boolean leftHint, boolean titleBar, boolean tabGroup, boolean collection) {
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


    @Override
    public void onClick(View v) {
        Intent it = null;
        switch (v.getId()) {

            case R.id.restart_txt:
                //重启
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.RESTART);
                break;
            case R.id.overflow_image:
                showPopWindow();
                break;
            case R.id.net_state_txt:
                //检测网络和检查服务器配置
//                recordState
                StartMainActivityAgain();
                break;

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

//    private void showCallDialog() {
//        if (mDialog == null) {
//            mDialog = new ProgressDialog(this);
//        }
//        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mDialog.setCancelable(true);
//        mDialog.setCanceledOnTouchOutside(true);
//        mDialog.show();
//        mDialog.setContentView(R.layout.dlg_call_service);
//    }


    private void switchUiComponent(FragmentManager fragmentManager, int containerViewId, Fragment fragment) {


        if (!isFinishing()) {
            fragmentManager.beginTransaction().replace(containerViewId, fragment).commitAllowingStateLoss();
        }
    }

    // 重新开启一个MainActivity
    private void StartMainActivityAgain() {
        Intent intentToNewMainActivity = new Intent(MainActivity.this, MainActivity.class);
        intentToNewMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intentToNewMainActivity);
        MainActivity.this.finish();
    }

    private void startTimeService() {
        Intent it = new Intent(MainActivity.this, TimeService.class);
        it.putExtra("currenttime", ServerTime.curtime);
        startService(it);
    }


    private void startScanBackupVideoService() {
        Intent it = new Intent(MainActivity.this, ScanBackupVideoService.class);
        startService(it);
    }

    private void test() {
        //收到浆员信息
        Button btn_send_donor_info = (Button) this.findViewById(R.id.btn_send_donor_info);
        btn_send_donor_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CONFIRM);
            }
        });
        Button btn_connect_bt = (Button) this.findViewById(R.id.btn_connect_bt);
        btn_connect_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealBTCon();

                tabletStateContext.setCurrentState(WaitingForBTConState.getInstance());

//                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BTCONSTART);
            }
        });
        Button btn_connect_bt_fail = (Button) this.findViewById(R.id.btn_connect_bt_fail);
        btn_connect_bt_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.BTCONFAILURE);
            }
        });

        //认证通过
        Button btn_auth_pass = (Button) this.findViewById(R.id.btn_auth_pass);
        btn_auth_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.AUTHPASS);
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.ZXDCAUTHRES);
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.SERAUTHRES);

            }
        });


        //加压
        Button btn_compression = (Button) this.findViewById(R.id.btn_compression);
        btn_compression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.COMPRESSINON);

                MyLog.e("SCR", "screen:" + BrightnessTools.getScreenBrightness(MainActivity.this));
            }
        });

        //穿刺
        Button btn_puncture = (Button) this.findViewById(R.id.btn_puncture);
        btn_puncture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PUNCTURE);
            }
        });

        //采集开始
        Button btn_collection_start = (Button) this.findViewById(R.id.btn_collection_start);
        btn_collection_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.START);
            }
        });

        //血浆重量
        Button btn_plasma_weight = (Button) this.findViewById(R.id.btn_plasma_weight);
        btn_plasma_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PLASMAWEIGHT);
            }
        });

        //管压过低
        Button btn_pipe_low = (Button) this.findViewById(R.id.btn_pipe_low);
        btn_pipe_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PIPELOW);
            }
        });

        //管压正常
        Button btn_pipe_normal = (Button) this.findViewById(R.id.btn_pipe_normal);
        btn_pipe_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.PIPENORMAL);
            }
        });

        //采集结束
        Button btn_collection_end = (Button) this.findViewById(R.id.btn_collection_end);
        btn_collection_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.END);
            }
        });

        //设备可用
        Button btn_check_pass = (Button) this.findViewById(R.id.btn_device_available);
        btn_check_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.CHECKOVER);
            }
        });

        //设备不可用
        Button btn_device_unavailable = (Button) this.findViewById(R.id.btn_device_unavailable);
        btn_device_unavailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletStateContext.handleMessge(recordState, observableZXDCSignalListenerThread, null, null, RecSignal.LOWPOWER);
            }
        });

    }

    //调低屏幕亮度
    private void setBrightnessLow() {
        BrightnessTools.stopAutoBrightness(this);
        BrightnessTools.setBrightness(this, 2);
    }

    //调正常屏幕亮度
    private void setBrightnessNormal() {
        BrightnessTools.startAutoBrightness(this);
    }

}

