package com.jiaying.mediatable.activity;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jiaying.mediatable.R;
import com.jiaying.mediatable.fragment.AdviceFragment;
import com.jiaying.mediatable.fragment.AppointmentFragment;
import com.jiaying.mediatable.fragment.CollectionFragment;
import com.jiaying.mediatable.fragment.FistFragment;
import com.jiaying.mediatable.fragment.InitializeFragment;
import com.jiaying.mediatable.fragment.OverFragment;
import com.jiaying.mediatable.fragment.OverServiceEvaluateFragment;
import com.jiaying.mediatable.fragment.PlayVideoFragment;
import com.jiaying.mediatable.fragment.PressingFragment;
import com.jiaying.mediatable.fragment.PunctureEvaluateFragment;
import com.jiaying.mediatable.fragment.PunctureFragment;
import com.jiaying.mediatable.fragment.SurfInternetFragment;
import com.jiaying.mediatable.fragment.VideoFragment;
import com.jiaying.mediatable.fragment.WaitingPlasmFragment;
import com.jiaying.mediatable.fragment.WelcomePlasmFragment;
import com.jiaying.mediatable.utils.AppInfoUtils;
import com.jiaying.mediatable.utils.ToastUtils;


/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;

    private RadioGroup mGroup;
    private ImageView overflow_image;//弹出功能
    private PopupWindow mPopupWindow;
    private View mPopView;
    private View mParentView;
    private TextView fun_txt;//功能设置
    private TextView server_txt;//服务器设置

    private TextView net_state_txt;//网络链接状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            //判断电量
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
//                 正在充电
                    ToastUtils.showToast(MainActivity.this, R.string.recharging);
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    {
                        ToastUtils.showToast(MainActivity.this, R.string.not_recharging);
                    }
                }
            }
            else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                    net_state_txt.setVisibility(View.VISIBLE);
                } else {
                    net_state_txt.setVisibility(View.GONE);
                }
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
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new InitializeFragment()).commit();
        initTitleBar();
        initGroup();
        Test();
    }

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


    private void initTitleBar() {
        mParentView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        mPopView = getLayoutInflater().inflate(R.layout.popupwin_main, null);
        net_state_txt = (TextView) findViewById(R.id.net_state_txt);
        net_state_txt.setOnClickListener(this);
        //选择功能设置，服务器地址设置以及重启
        overflow_image = (ImageView) findViewById(R.id.overflow_image);
        overflow_image.setOnClickListener(this);
        fun_txt = (TextView) mPopView.findViewById(R.id.fun_txt);
        fun_txt.setOnClickListener(this);
        server_txt = (TextView) mPopView.findViewById(R.id.server_txt);
        server_txt.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void initVariables() {
    }

    private void Test() {

        //等待献浆元
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new WaitingPlasmFragment()).commit();
            }
        });
        //欢迎献浆
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new WelcomePlasmFragment()).commit();
            }
        });

        //加压提示
        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PressingFragment()).commit();
            }
        });

        //穿刺提示
        Button btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PunctureFragment()).commit();
            }
        });

        //穿刺视频播放
        Button btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PlayVideoFragment()).commit();
            }
        });

        //穿刺评价
        Button btn6 = (Button) findViewById(R.id.btn6);
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PunctureEvaluateFragment()).commit();
            }
        });

        //采集提示
        Button btn7 = (Button) findViewById(R.id.btn7);
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectionFragment()).commit();
            }
        });

        //采集播放视频，并且显示采集的进度等信息
        Button btn8 = (Button) findViewById(R.id.btn8);
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PlayVideoFragment()).commit();
            }
        });
        //握拳提示
        Button btn9 = (Button) findViewById(R.id.btn9);
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new FistFragment()).commit();
            }
        });

        //结束服务评价
        Button btn10 = (Button) findViewById(R.id.btn10);
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverServiceEvaluateFragment()).commit();
            }
        });

        //结束欢送
        Button btn11 = (Button) findViewById(R.id.btn11);
        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new OverFragment()).commit();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent it = null;
        switch (v.getId()) {
            case R.id.fun_txt:
                //功能设置
                it = new Intent(MainActivity.this, FunctionSettingActivity.class);
                mPopupWindow.dismiss();
                break;
            case R.id.server_txt:
                //服务器设置
                it = new Intent(MainActivity.this, ServerSettingActivity.class);
                mPopupWindow.dismiss();
                break;
            case R.id.restar_txt:
                //重启
                mPopupWindow.dismiss();
                break;
            case R.id.overflow_image:
                showPopWindow();
                break;
            case R.id.net_state_txt:
                //检测网络和检查服务器配置
                it = new Intent(MainActivity.this,ServerSettingActivity.class);
                break;
        }
        if (it != null) {
            startActivity(it);
        }
    }

    private void showPopWindow() {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mPopView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    false);
            mPopupWindow.setHeight(AppInfoUtils.dip2px(MainActivity.this, 195));
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
}
