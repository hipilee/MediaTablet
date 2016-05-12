package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

import java.util.HashMap;

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
                //记录状态
                recordState.recCheckStart();

                //发送信号
                listenerThread.notifyObservers(RecSignal.CHECKSTART);

                //状态切换
                TabletStateContext.getInstance().setCurrentState(WaitingForCheckOverState.getInstance());

                break;

            case RESTART:
                //发送信号
                listenerThread.notifyObservers(recSignal);

                break;
        }

    }

}
