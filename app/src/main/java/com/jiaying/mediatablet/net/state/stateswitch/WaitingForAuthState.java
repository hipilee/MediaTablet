package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class WaitingForAuthState extends AbstractState {
    private static WaitingForAuthState waitingForAuthState = null;

    private WaitingForAuthState() {
    }

    public static WaitingForAuthState getInstance() {
        if (waitingForAuthState == null) {
            waitingForAuthState = new WaitingForAuthState();
        }
        return waitingForAuthState;
    }

    @Override
    public synchronized void  handleMessage(RecordState recordState,ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {
            case AUTHPASS:
                //record state
                recordState.recAuth();

                listenerThread.notifyObservers(RecSignal.AUTHPASS);

                //send authpass cmd.
                sendAuthPassCmd();

                //switch the state
                TabletStateContext.getInstance().setCurrentState(WaitingForCompressionState.getInstance());

//                TabletStateContext.getInstance().setCurrentState(CollectionState.getInstance());

//                listenerThread.notifyObservers(RecSignal.COMPRESSINON);
                listenerThread.notifyObservers(RecSignal.PIPELOW);


//                TabletStateContext.getInstance().handleMessge(recordState,listenerThread,null,null,RecSignal.PUNCTURE);

                break;
        }
    }

    private void sendAuthPassCmd(){
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
        retcmd.setCmd("authentication_donor");
        retcmd.setHasResponse(true);
        retcmd.setLevel(2);
        HashMap<String, Object> values = new HashMap<>();
        values.put("donorId", Donor.getInstance().getDonorID());
        values.put("deviceId", "chair001");
        retcmd.setValues(values);
        clientService.getApDataCenter().addSendCmd(retcmd);
    }
}
