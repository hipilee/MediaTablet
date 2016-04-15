package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.utils.Conversion;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class AuthenticationState extends AbstractState {
    private static AuthenticationState authenticationState = null;

    private AuthenticationState() {
    }

    public static AuthenticationState getInstance() {
        if (authenticationState == null) {
            authenticationState = new AuthenticationState();
        }
        return authenticationState;
    }

    @Override
    public synchronized void  handleMessage(ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {
            case AUTHPASS:
                listenerThread.notifyObservers(RecSignal.AUTHPASS);
                TabletStateContext.getInstance().setCurrentState(WaitingForCompressionState.getInstance());
                DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
                DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
                retcmd.setCmd("authentication_donor");
                retcmd.setHasResponse(true);
                retcmd.setLevel(2);
                HashMap<String, Object> values = new HashMap<String, Object>();
                values.put("donorId", Donor.getInstance().getDonorID());
                values.put("deviceId", "chair001");
                retcmd.setValues(values);
                clientService.getApDataCenter().addSendCmd(retcmd);
//                TabletStateContext.getInstance().handleMessge(listenerThread, dataCenterRun, cmd, RecSignal.START);
                break;
        }
    }
}
