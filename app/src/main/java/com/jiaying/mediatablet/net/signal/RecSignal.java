package com.jiaying.mediatablet.net.signal;

/**
 * Created by hipil on 2016/4/2.
 */
public enum RecSignal {

    // The signals received from the plasma.
    TIMESTAMP,
    CONFIRM,
    AUTHPASS,
    SERAUTHRES,
    ZXDCAUTHRES,
    AUTHRESOK,
    AUTHRESTIMEOUT,
    REAUTHPASS,
    CANCLEAUTHPASS,
    COMPRESSINON,
    PUNCTURE,
    START,
    AUTOTRANFUSIONSTART,
    AUTOTRANFUSIONEND,
    PLASMAWEIGHT,
    PIPELOW,
    PIPENORMAL,
    PAUSED,
    END,

    //
    LOWPOWER,
    CHECKSTART,
    CHECKOVER,
    GETRES,
    WAITING,
    STARTPUNTUREVIDEO,
    STARTCOLLECTIONVIDEO,


    //
    SETTINGS,
    RESTART,

    // Switch between the tabs
    TOVIDEO,
    TOSURF,
    TOSUGGEST,
    TOAPPOINT,

    //between activity and fragment
    VIDEOTOMAIN,
    CLICKSUGGESTION,
    CLICKEVALUATION,
    CLICKAPPOINTMENT,
    SAVEAPPOINTMENT,
    SAVESUGGESTION,
    SAVEEVALUATION,
    AUTH,
    STARTVIDEO,

    //back button
    BACKTOVIDEOLIST,
    BACKTOADVICE,
    BACKTOAPPOINTMENT,

    //
    NOTHING,

    //The three physical keys
    POWEROFF,
    RECENT,
    HOME

}
