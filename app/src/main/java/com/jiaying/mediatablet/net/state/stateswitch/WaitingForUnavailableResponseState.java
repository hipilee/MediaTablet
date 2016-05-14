package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/5/14.
 */
public class WaitingForUnavailableResponseState extends AbstractState {
    private static WaitingForUnavailableResponseState ourInstance = new WaitingForUnavailableResponseState();

    public static WaitingForUnavailableResponseState getInstance() {
        return ourInstance;
    }

    private WaitingForUnavailableResponseState() {
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {

    }
}
