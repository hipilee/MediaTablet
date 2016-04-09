package com.jiaying.mediatablet.activity;

import android.widget.CompoundButton;
import android.widget.Switch;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.utils.SetTopView;

/**
 * 功能配置页面
 */
public class FunctionSettingActivity extends BaseActivity {
    private Switch watch_film_switch;
    private Switch surf_it_switch;
    private Switch advice_switch;
    private Switch appointment_switch;

    @Override
    public void initVariables() {

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_function_setting);
        new SetTopView(this,R.string.func_setting,true);
        watch_film_switch = (Switch) findViewById(R.id.watch_film_switch);
        watch_film_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        surf_it_switch = (Switch) findViewById(R.id.surf_it_switch);
        surf_it_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        advice_switch = (Switch) findViewById(R.id.advice_switch);
        advice_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        appointment_switch = (Switch) findViewById(R.id.appointment_switch);
        appointment_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    public void loadData() {

    }
}
