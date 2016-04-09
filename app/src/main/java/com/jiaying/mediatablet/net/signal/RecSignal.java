package com.jiaying.mediatablet.net.signal;

/**
 * Created by hipil on 2016/4/2.
 */
public enum RecSignal {
    // The signals received from the plasma.
    CONFIRM,
    COMPRESSINON,
    PUNCTURE,
    START,
    pipeLow,
    pipeNormal,
    PAUSED,
    END,
    //
    WAITING,
    STARTPUNTUREVIDEO,
    STARTCOLLECTIONVIDEO,

    // Switch between the tabs
    TOVIDEO,
    TOSURF,
    TOSUGGEST,
    TOAPPOINT,

    //between activity and fragment
    VIDEOTOMAIN,
    INPUTSUGGESTION,
    INPUTEVALUATION,
    INPUTAPPOINTMENT,
    AUTH
}
