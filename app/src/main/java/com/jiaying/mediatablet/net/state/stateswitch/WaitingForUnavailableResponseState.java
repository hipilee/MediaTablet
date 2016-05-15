package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.ServerTime;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/5/14.
 */
public class WaitingForUnavailableResponseState extends AbstractState {
    private static WaitingForUnavailableResponseState ourInstance = new WaitingForUnavailableResponseState();

    public static WaitingForUnavailableResponseState getInstance() {
        return ourInstance;
    }

    private WaitingForUnavailableResponseState() {
    }

    @Override
    void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case TIMESTAMP:
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                //发送信号
                //改变状态
                //更新数据
                break;


            case SETTINGS:
                listenerThread.notifyObservers(recSignal);
                break;

            case RESTART:

                //发送信号
                listenerThread.notifyObservers(RecSignal.RESTART);

                //发送信号
                //改变状态
                //更新数据

                break;

            case UNAVAILABLERES:
                //发送信号
                //改变状态
                //更新数据
                listenerThread.notifyObservers(RecSignal.UNAVAILABLERES);


                TabletStateContext.getInstance().setCurrentState(WaitingForCheckOverState.getInstance());
                break;
        }
    }
}
