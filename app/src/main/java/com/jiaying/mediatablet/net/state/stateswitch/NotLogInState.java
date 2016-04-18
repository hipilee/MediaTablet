package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/13.
 */
public class NotLogInState extends AbstractState {
    private static NotLogInState notLogInState = null;

    private NotLogInState() {
    }

    public static NotLogInState getInstance() {
        if (notLogInState == null) {
            notLogInState = new NotLogInState();
        }
        return notLogInState;
    }

    @Override
    public synchronized void handleMessage(RecordState recordState,ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun,
                       DataCenterTaskCmd cmd, RecSignal recSignal) {

    }
}
