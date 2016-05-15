package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.ServerTime;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/29.
 */
public class WaitingForAvailableResponseState extends AbstractState {

    private static WaitingForAvailableResponseState waitingForAvailableResponseState = null;

    private WaitingForAvailableResponseState() {
    }

    public static WaitingForAvailableResponseState getInstance() {
        if (waitingForAvailableResponseState == null) {
            waitingForAvailableResponseState = new WaitingForAvailableResponseState();
        }
        return waitingForAvailableResponseState;
    }

    @Override
    public void setTabletStateContext(TabletStateContext tabletStateContext) {
        super.setTabletStateContext(tabletStateContext);
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case TIMESTAMP:
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                break;


            case AVAILABLERES:

                //记录状态
//                recordState.recGetRes();

                //发送信号
                listenerThread.notifyObservers(RecSignal.AVAILABLERES);

                //切换状态
                TabletStateContext.getInstance().setCurrentState(WaitingForDonorState.getInstance());

            case SETTINGS:
                listenerThread.notifyObservers(recSignal);
                break;

            case RESTART:

                //发送信号
                listenerThread.notifyObservers(RecSignal.RESTART);

                break;

        }
    }

}
