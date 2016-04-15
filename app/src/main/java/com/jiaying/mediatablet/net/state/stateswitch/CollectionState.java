package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.net.utils.Conversion;

import org.opencv.imgproc.LineSegmentDetector;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class CollectionState extends AbstractState {
    private static CollectionState collectionState = null;

    private CollectionState() {
    }

    public static CollectionState getInstance() {
        if (collectionState == null) {
            collectionState = new CollectionState();
        }
        return collectionState;
    }

    @Override
    public synchronized void handleMessage(ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {
            case STARTCOLLECTIONVIDEO:
                listenerThread.notifyObservers(RecSignal.STARTCOLLECTIONVIDEO);
                break;

            case PIPELOW:
                listenerThread.notifyObservers(RecSignal.PIPELOW);
                break;

            case PIPENORMAL:
                listenerThread.notifyObservers(RecSignal.PIPENORMAL);
                break;

            case TOVIDEO:
                listenerThread.notifyObservers(RecSignal.TOVIDEO);
                break;

            case TOSURF:
                listenerThread.notifyObservers(RecSignal.TOSURF);
                break;

            case TOSUGGEST:
                listenerThread.notifyObservers(RecSignal.TOSUGGEST);
                break;

            case TOAPPOINT:
                listenerThread.notifyObservers(RecSignal.TOAPPOINT);
                break;

            case CLICKAPPOINTMENT:
                listenerThread.notifyObservers(RecSignal.CLICKAPPOINTMENT);
                break;

            case CLICKSUGGESTION:
                listenerThread.notifyObservers(RecSignal.CLICKSUGGESTION);
                break;

            case CLICKEVALUATION:
                listenerThread.notifyObservers(RecSignal.CLICKEVALUATION);
                break;

            case SAVEAPPOINTMENT:
                listenerThread.notifyObservers(RecSignal.SAVEAPPOINTMENT);
                break;

            case SAVESUGGESTION:
                listenerThread.notifyObservers(RecSignal.SAVESUGGESTION);
                break;

            case SAVEEVALUATION:
                listenerThread.notifyObservers(RecSignal.SAVEEVALUATION);
                break;
            case VIDEOTOMAIN:
                listenerThread.notifyObservers(RecSignal.VIDEOTOMAIN);
                break;

            case BACKTOVIDEOLIST:
                listenerThread.notifyObservers(RecSignal.BACKTOVIDEOLIST);
                break;

            case STARTVIDEO:
                listenerThread.notifyObservers(RecSignal.STARTVIDEO);
                break;

            case END:
                listenerThread.notifyObservers(RecSignal.END);

                TabletStateContext.getInstance().setCurrentState(WaitingForDonorState.getInstance());

                //Construct cmd
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
