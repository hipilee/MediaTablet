package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.util.Log;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/13.
 */
public class TabletStateContext {
    private AbstractState state;
    private static TabletStateContext tabletStateContext;

    private Boolean isContinue = false;

    public void setAbility(Boolean b) {
        isContinue = b;
    }

    private TabletStateContext() {

    }

    public static synchronized TabletStateContext getInstance() {
        if (tabletStateContext == null) {
            tabletStateContext = new TabletStateContext();
        }
        return tabletStateContext;
    }

    public synchronized void setCurrentState(AbstractState istate) {
        this.state = istate;
    }
    public synchronized AbstractState getCurrentState( ) {
        return state;
    }
    public synchronized void handleMessge(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun,
                                          DataCenterTaskCmd cmd, RecSignal recSignal) {
        if (cmd != null) {
            Log.e("error", cmd.getCmd());
        }
        Log.e("error", this.state.toString());

        if (isContinue) {
            if (recSignal == RecSignal.POWEROFF) {
                setAbility(false);
                listenerThread.notifyObservers(recSignal);
            } else {
                state.handleMessage(recordState, listenerThread, dataCenterRun, cmd, recSignal);
            }
        }
    }
}
