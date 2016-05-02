package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/16.
 */
public class WaitingForCheckOverState extends AbstractState {
    private static WaitingForCheckOverState waitingForCheckOverState = null;

    private WaitingForCheckOverState() {
    }

    public static WaitingForCheckOverState getInstance() {
        if (waitingForCheckOverState == null) {
            waitingForCheckOverState = new WaitingForCheckOverState();
        }
        return waitingForCheckOverState;
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case CHECKOVER:

                //记录下当前状态，
                recordState.recCheckOver();

                //发送信号
                listenerThread.notifyObservers(recSignal);

                //切换状态
                TabletStateContext.getInstance().setCurrentState(WaitingForResponseState.getInstance());
                break;
            case RESTART:
                //发送信号
                listenerThread.notifyObservers(recSignal);

                break;
        }
    }
}
