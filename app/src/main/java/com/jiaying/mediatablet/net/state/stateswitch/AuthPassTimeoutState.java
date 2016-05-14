package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.DevEntity;
import com.jiaying.mediatablet.entity.DonorEntity;
import com.jiaying.mediatablet.entity.ServerTime;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

import java.util.HashMap;

/**
 * Created by hipil on 2016/5/11.
 */
public class AuthPassTimeoutState extends AbstractState {
    private static AuthPassTimeoutState ourInstance = new AuthPassTimeoutState();

    public static AuthPassTimeoutState getInstance() {
        return ourInstance;
    }

    private AuthPassTimeoutState() {
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case TIMESTAMP:
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                break;

            case CANCLEAUTHPASS:
                //记录状态
                recordState.recGetRes();

                //发送信号
                listenerThread.notifyObservers(RecSignal.GETRES);

                //切换等待献浆员状态
                TabletStateContext.getInstance().setCurrentState(WaitingForDonorState.getInstance());

                break;
            case REAUTHPASS:

                //再次向发送认证通过信号
                sendAuthPassCmd();

                listenerThread.notifyObservers(RecSignal.AUTHPASS);

                //切换到等待服务器和浆机应答状态
                TabletStateContext.getInstance().setCurrentState(WaitingForSerZxdcResState.getInstance());
                break;

            case RESTART:

                //发送信号
                listenerThread.notifyObservers(RecSignal.RESTART);

                break;

        }
    }

    private void sendAuthPassCmd() {
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
        retcmd.setCmd("authentication_donor");
        retcmd.setHasResponse(true);
        retcmd.setLevel(2);
        HashMap<String, Object> values = new HashMap<>();
        values.put("donorId", DonorEntity.getInstance().getDonorID());
        values.put("deviceId", DevEntity.getInstance().getAp());
        retcmd.setValues(values);
        clientService.getApDataCenter().addSendCmd(retcmd);
    }
}
