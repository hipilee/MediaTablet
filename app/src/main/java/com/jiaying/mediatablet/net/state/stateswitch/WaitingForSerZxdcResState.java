package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.ServerTime;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/5/11.
 */
public class WaitingForSerZxdcResState extends AbstractState {

    private static WaitingForSerZxdcResState waitingForSerZxdcResState = null;

    private WaitingForSerZxdcResState() {
    }

    public static WaitingForSerZxdcResState getInstance() {
        if (waitingForSerZxdcResState == null) {
            waitingForSerZxdcResState = new WaitingForSerZxdcResState();
        }
        return waitingForSerZxdcResState;
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

            case SERAUTHRES:

                //发送信号
                listenerThread.notifyObservers(RecSignal.SERAUTHRES);

                TabletStateContext.getInstance().setCurrentState(WaitingForZxdcResState.getInstance());

                break;

            case ZXDCAUTHRES:
                //发送信号
                listenerThread.notifyObservers(RecSignal.ZXDCAUTHRES);

                TabletStateContext.getInstance().setCurrentState(WaitingForSerResState.getInstance());

                break;

            case AUTHRESTIMEOUT:
                //发送信号
                listenerThread.notifyObservers(RecSignal.AUTHRESTIMEOUT);

                TabletStateContext.getInstance().setCurrentState(AuthPassTimeoutState.getInstance());
                break;

            case RESTART:
                //发送信号
                listenerThread.notifyObservers(RecSignal.RESTART);

                break;


        }
    }
}
