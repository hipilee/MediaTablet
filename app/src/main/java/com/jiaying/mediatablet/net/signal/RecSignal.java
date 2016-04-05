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
    STARTPUNTUREVIDEO,
    STARTCOLLECTIONVIDEO,

    // Switch between the tabs
    TOVIDEO,
    TOSURF,
    TOSUGGEST,
    TOAPPOINT,
}
