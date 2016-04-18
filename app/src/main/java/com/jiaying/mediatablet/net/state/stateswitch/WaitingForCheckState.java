package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/16.
 */
public class WaitingForCheckState extends AbstractState {
    private static WaitingForCheckState waitingForCheckState = null;

    private WaitingForCheckState() {
    }

    public static WaitingForCheckState getInstance() {
        if (waitingForCheckState == null) {
            waitingForCheckState = new WaitingForCheckState();
        }
        return waitingForCheckState;
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
switch (recSignal){
    case WAITING:
        TabletStateContext.getInstance().setCurrentState(WaitingForDonorState.getInstance());
        listenerThread.notifyObservers(recSignal);
        break;
}
    }
}
