package com.jiaying.mediatablet.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jiaying.mediatablet.R;
import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.entity.DevEntity;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;

/*
服务器配置
 */
public class ServerSettingFragment extends Fragment {

    EditText et_dev_id;

    EditText log_server_ip, log_server_port;

    EditText signal_server_ip, signal_server_port;

    EditText video_server_ip, video_server_port;

    Button btn_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_setting, null);

        et_dev_id = (EditText) view.findViewById(R.id.et_dev_ap);
        et_dev_id.setText(DevEntity.getInstance().getAp());

        //显示当前日志服务器ip地址和端口
        log_server_ip = (EditText) view.findViewById(R.id.log_server_ip);
        log_server_ip.setText(MainActivity.logServer.getIp());
        log_server_port = (EditText) view.findViewById(R.id.log_server_port);
        log_server_port.setText(String.valueOf(MainActivity.logServer.getPort()));

        //显示当前信号服务器ip地址和端口
        signal_server_ip = (EditText) view.findViewById(R.id.signal_server_ip);
        signal_server_ip.setText(MainActivity.signalServer.getIp());
        signal_server_port = (EditText) view.findViewById(R.id.signal_server_port);
        signal_server_port.setText(String.valueOf(MainActivity.signalServer.getPort()));

        //显示当前视频服务器ip地址和端口
        video_server_ip = (EditText) view.findViewById(R.id.video_server_ip);
        video_server_ip.setText(MainActivity.videoServer.getIp());
        video_server_port = (EditText) view.findViewById(R.id.video_server_port);
        video_server_port.setText(String.valueOf(MainActivity.videoServer.getPort()));

        btn_save = (Button) view.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存相关参数到本地
                SharedPreferences settings;
                SharedPreferences.Editor editor;
                settings = getActivity().getPreferences(Context.MODE_PRIVATE);
                editor = settings.edit();

                //日志IP
                editor.putString("log_server_ip", log_server_ip.getText().toString().trim());
                editor.putInt("log_server_port", Integer.parseInt(log_server_port.getText().toString().trim()));

                editor.putString("signal_server_ip", signal_server_ip.getText().toString().trim());
                editor.putInt("signal_server_port", Integer.parseInt(signal_server_port.getText().toString().trim()));

                editor.putString("video_server_ip", video_server_ip.getText().toString().trim());
                editor.putInt("video_server_port", Integer.parseInt(video_server_port.getText().toString().trim()));

                //ap
                editor.putString("ap", et_dev_id.getText().toString().trim());
                editor.commit();

                MainActivity mainActivity = (MainActivity) getActivity();
                TabletStateContext.getInstance().handleMessge(mainActivity.getRecordState(), mainActivity.getObservableZXDCSignalListenerThread(), null, null, RecSignal.RESTART);
            }
        });

        return view;
    }


}
