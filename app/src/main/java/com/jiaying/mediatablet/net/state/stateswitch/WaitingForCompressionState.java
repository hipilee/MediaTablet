package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.DeviceEntity;
import com.jiaying.mediatablet.entity.DonorEntity;
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
    public synchronized void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread,
                                           DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal,TabletStateContext tabletStateContext) {
        switch (recSignal) {
            //记录状态

            //获取数据

            //切换状态

            //发送信号
            case TIMESTAMP:
                //记录状态

                //获取数据
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                //切换状态

                //发送信号
                listenerThread.notifyObservers(RecSignal.TIMESTAMP);
                break;

            case COMPRESSINON:
                //记录状态
                recordState.recCompression();

                //获取数据

                //切换状态
                tabletStateContext.setCurrentState(WaitingForPunctureState.getInstance());
                //发送信号
                listenerThread.notifyObservers(RecSignal.COMPRESSINON);

                break;

            case PUNCTURE:

                //记录状态
                recordState.recPuncture();

                //获取数据

                //切换状态
                tabletStateContext.setCurrentState(WaitingForStartState.getInstance());

                //发送信号
                listenerThread.notifyObservers(RecSignal.PUNCTURE);
                break;

            case START:
                //记录状态
                recordState.recCollection();
                //获取数据

                //切换状态
                tabletStateContext.setCurrentState(CollectionState.getInstance());

                //发送信号
                listenerThread.notifyObservers(RecSignal.START);
                if (cmd != null) {
                    sendTabletRevStartCmdRes();
                }

                break;

            case RESTART:
                //记录状态

                //获取数据

                //切换状态

                //发送信号
                listenerThread.notifyObservers(recSignal);

                break;


            case TOVIDEO_FULLSCREEN:

                //记录状态

                //获取数据

                //切换状态

                //发送信号
                listenerThread.notifyObservers(RecSignal.TOVIDEO_FULLSCREEN);
                break;
            case TOVIDEO_NOT_FULLSCREEN:

                //记录状态

                //获取数据

                //切换状态

                //发送信号
                listenerThread.notifyObservers(RecSignal.TOVIDEO_NOT_FULLSCREEN);
                break;

        }
    }

    private void sendTabletRevStartCmdRes() {
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
        retcmd.setCmd("tablet_rev_start");
        retcmd.setHasResponse(false);
        retcmd.setLevel(2);
        clientService.getApDataCenter().addSendCmd(retcmd);
    }


}
