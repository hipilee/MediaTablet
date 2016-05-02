package com.jiaying.mediatablet.fragment;

import android.app.Fragment;
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
    Button btn_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_setting, null);

        et_dev_id = (EditText) view.findViewById(R.id.et_dev_id);
        et_dev_id.setText(DevEntity.getInstance().getDeviceId());

        btn_save = (Button) view.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevEntity devEntity = DevEntity.getInstance();
                devEntity.setDeviceId(et_dev_id.getText().toString().trim());
                MainActivity mainActivity = (MainActivity) getActivity();
                TabletStateContext.getInstance().handleMessge(mainActivity.getRecordState(), mainActivity.getObservableZXDCSignalListenerThread(), null, null, RecSignal.RESTART);
            }
        });

        return view;
    }


}
