package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;
import android.util.Log;

import com.jiaying.mediatablet.entity.PlasmaWeightEntity;
import com.jiaying.mediatablet.entity.ServerTime;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

import java.util.HashMap;
import java.util.Random;

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
    public synchronized void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {
            case TIMESTAMP:
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                break;

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

            case AUTOTRANFUSIONSTART:
                listenerThread.notifyObservers(RecSignal.AUTOTRANFUSIONSTART);
                Log.e("error", "还输开始");
                break;
            case AUTOTRANFUSIONEND:
                listenerThread.notifyObservers(RecSignal.AUTOTRANFUSIONEND);
                Log.e("error", "还输结束");
                break;
            case PLASMAWEIGHT:
                if (cmd != null) {
                    PlasmaWeightEntity.getInstance().setCurWeight(Integer.parseInt(textUnit.ObjToString(cmd.getValue("current_weight"))));
                    PlasmaWeightEntity.getInstance().setCurWeight(Integer.parseInt(textUnit.ObjToString(cmd.getValue("setting_weight"))));
                } else {
                    PlasmaWeightEntity.getInstance().setCurWeight(new Random().nextInt(600));
                    PlasmaWeightEntity.getInstance().setSettingWeight(600);
                }
                listenerThread.notifyObservers(RecSignal.PLASMAWEIGHT);


                Log.e("error", "血浆电子称重量");
                break;

            case END:

                //记录状态
                recordState.recEnd();

                //发送信号
                listenerThread.notifyObservers(RecSignal.END);

                //状态切换
                TabletStateContext.getInstance().setCurrentState(EndState.getInstance());

                break;

            case RESTART:
                //发送信号
                listenerThread.notifyObservers(recSignal);

                break;
        }
    }


}
