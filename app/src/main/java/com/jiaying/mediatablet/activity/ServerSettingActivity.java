package com.jiaying.mediatablet.activity;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.utils.SetTopView;

/**
 * 服务器配置页面
 */
public class ServerSettingActivity extends BaseActivity {

    @Override
    public void initVariables() {

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_server_setting);
        new SetTopView(this,R.string.server_setting,true);
    }

    @Override
    public void loadData() {

    }
}
