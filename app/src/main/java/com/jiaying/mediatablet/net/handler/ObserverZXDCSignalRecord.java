package com.jiaying.mediatablet.net.handler;


import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;

import java.util.Observable;

/**
 * Created by Administrator on 2015/9/30 0030.
 */
public class ObserverZXDCSignalRecord implements java.util.Observer {
    public ObserverZXDCSignalRecord(RecordState recordState) {
        this.recordState = recordState;
    }

    RecordState recordState;

    @Override
    public void update(Observable observable, Object data) {
        switch ((RecSignal) data) {

            case CONFIRM:
                recordState.recConfirm();
                break;

            case COMPRESSINON:
                recordState.recCompression();
                break;

            case PUNCTURE:
                recordState.recPuncture();
                break;

            case START:
                recordState.recStart();
                break;

            case END:
                recordState.recEnd();
                break;

            default:
                break;

        }
    }
}

