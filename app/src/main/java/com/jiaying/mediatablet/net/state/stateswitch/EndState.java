package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/20.
 */
public class EndState extends AbstractState {
    private static EndState ourInstance = new EndState();

    public static EndState getInstance() {
        return ourInstance;
    }

    private EndState() {
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {
            case CHECKSTART:
                //record state
                recordState.recEnd();

                listenerThread.notifyObservers(RecSignal.CHECKSTART);

                //switch the state
                TabletStateContext.getInstance().setCurrentState(WaitingForCheckState.getInstance());

                break;
        }

    }
}
