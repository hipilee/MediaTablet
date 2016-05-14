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
public class WaitingForZxdcResState extends AbstractState {
    private static WaitingForZxdcResState ourInstance = new WaitingForZxdcResState();

    public static WaitingForZxdcResState getInstance() {
        return ourInstance;
    }

    private WaitingForZxdcResState() {
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case TIMESTAMP:
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                break;

            case ZXDCAUTHRES:
                //记录状态
                recordState.recAuth();
                //发送信号
                listenerThread.notifyObservers(RecSignal.AUTHRESOK);

                TabletStateContext.getInstance().setCurrentState(WaitingForCompressionState.getInstance());

                break;
            case AUTHRESTIMEOUT:
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
