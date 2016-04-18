package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class WaitingForPunctureState extends AbstractState {
    private static WaitingForPunctureState waitingForPunctureState = null;

    private WaitingForPunctureState() {
    }

    public static WaitingForPunctureState getInstance() {
        if (waitingForPunctureState == null) {
            waitingForPunctureState = new WaitingForPunctureState();
        }
        return waitingForPunctureState;
    }

    @Override
    public synchronized void handleMessage(RecordState recordState,ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case PUNCTURE:
                //record state
                recordState.recPuncture();

                listenerThread.notifyObservers(RecSignal.PUNCTURE);
                TabletStateContext.getInstance().setCurrentState(WaitingForStartState.getInstance());
                break;

            case START:
                DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
                retcmd.setSeq(cmd.getSeq());
                retcmd.setCmd("response");


                HashMap<String, Object> values = new HashMap<String, Object>();
                values.put("ok", "true");
                retcmd.setValues(values);


                TabletStateContext.getInstance().setCurrentState(CollectionState.getInstance());
                listenerThread.notifyObservers(RecSignal.START);
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
