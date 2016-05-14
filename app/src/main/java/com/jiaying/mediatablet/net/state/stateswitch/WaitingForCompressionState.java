package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.ServerTime;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.utils.Conversion;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class WaitingForCompressionState extends AbstractState {
    private static WaitingForCompressionState waitingForCompressionState = null;

    private WaitingForCompressionState() {
    }

    public static WaitingForCompressionState getInstance() {
        if (waitingForCompressionState == null) {
            waitingForCompressionState = new WaitingForCompressionState();
        }
        return waitingForCompressionState;
    }

    @Override
    public synchronized void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case TIMESTAMP:
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                break;

            case COMPRESSINON:
                //记录状态
                recordState.recCompression();

                //发送信号
                listenerThread.notifyObservers(RecSignal.COMPRESSINON);

                //状态切换到等待穿刺状态
                TabletStateContext.getInstance().setCurrentState(WaitingForPunctureState.getInstance());

                break;

            case PUNCTURE:
                //记录状态
                recordState.recPuncture();

                //发送信号
                listenerThread.notifyObservers(RecSignal.PUNCTURE);

                //状态切换到等待开始采集状态
                TabletStateContext.getInstance().setCurrentState(WaitingForStartState.getInstance());

                break;

            case START:
                //记录状态
                recordState.recCollection();

                //发送信号
                listenerThread.notifyObservers(RecSignal.START);


                //状态切换
                TabletStateContext.getInstance().setCurrentState(CollectionState.getInstance());

                break;

            case RESTART:
                //发送信号
                listenerThread.notifyObservers(recSignal);

                break;
        }
    }

}
