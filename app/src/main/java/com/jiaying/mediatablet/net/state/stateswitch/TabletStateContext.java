package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/13.
 */
public class TabletStateContext {
    private AbstractState state;
    private static TabletStateContext tabletStateContext;

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

    public synchronized void handleMessge(ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun,
                                          DataCenterTaskCmd cmd, RecSignal recSignal) {
        state.handleMessage(listenerThread,dataCenterRun,cmd,recSignal);
    }
}
