package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/7/10.
 */
public class BTConFailureState extends AbstractState {

    private static BTConFailureState btConFailureState = null;

    private BTConFailureState() {
    }

    public static BTConFailureState getInstance() {
        if (btConFailureState == null) {
            btConFailureState = new BTConFailureState();
        }
        return btConFailureState;
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal, TabletStateContext tabletStateContext) {
        switch (recSignal) {
            case CHECKSTART:
                break;
            case SETTINGS:
                break;
            case BTCONSTART:
                break;
        }
    }
}
