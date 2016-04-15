package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class TransfusionState extends AbstractState {
    private static TransfusionState transfusionState = null;

    private TransfusionState() {
    }

    public static TransfusionState getInstance() {
        if (transfusionState == null) {
            transfusionState = new TransfusionState();
        }
        return transfusionState;
    }

    @Override
    public synchronized void handleMessage(ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun,
                       DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {
            case END:
                listenerThread.notifyObservers(RecSignal.END);
                DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
                retcmd.setSeq(cmd.getSeq());
                retcmd.setCmd("response");

                HashMap<String, Object> values = new HashMap<String, Object>();
                values.put("ok", "true");
                retcmd.setValues(values);

                try {
                    dataCenterRun.sendResponseCmd(retcmd);
                } catch (DataCenterException e) {
                    e.printStackTrace();
                } finally {
                }
                break;
        }
    }
}
