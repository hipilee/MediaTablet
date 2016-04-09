package com.jiaying.mediatablet.net.utils;

import com.jiaying.mediatablet.net.signal.RecSignal;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/3.
 */
public class FilterSignal {
    //true表示该状态信号可以被响应
    //false表示该状态信号不可以被响应
    private HashMap<String, Boolean> signalMap;

    public FilterSignal() {
        this.signalMap = new HashMap<String, Boolean>();
        this.signalMap.put("CONFIRM", true);
        this.signalMap.put("COMPRESSION", false);
        this.signalMap.put("PUNCTURE", false);
        this.signalMap.put("STARTPUNTUREVIDEO", false);
        this.signalMap.put("START", false);
        this.signalMap.put("STARTCOLLECTIONVIDEO", false);
        this.signalMap.put("pipeLow", false);
        this.signalMap.put("pipeNormal", false);
        this.signalMap.put("PAUSED", false);
        this.signalMap.put("END", false);
    }

    public synchronized void recConfirm() {
        this.signalMap.put("CONFIRM", false);
        this.signalMap.put("COMPRESSION", true);
        this.signalMap.put("PUNCTURE", true);
        this.signalMap.put("STARTPUNTUREVIDEO", false);
        this.signalMap.put("START", true);
        this.signalMap.put("STARTCOLLECTIONVIDEO", false);
        this.signalMap.put("pipeLow", false);
        this.signalMap.put("pipeNormal", false);
        this.signalMap.put("PAUSED", false);
        this.signalMap.put("END", false);
    }

    public synchronized void recCompression() {
        this.signalMap.put("CONFIRM", false);
        this.signalMap.put("COMPRESSION", false);
        this.signalMap.put("PUNCTURE", true);
        this.signalMap.put("STARTPUNTUREVIDEO", false);
        this.signalMap.put("START", true);
        this.signalMap.put("STARTCOLLECTIONVIDEO", false);
        this.signalMap.put("pipeLow", false);
        this.signalMap.put("pipeNormal", false);
        this.signalMap.put("PAUSED", false);
        this.signalMap.put("END", false);
    }

    public synchronized void recPuncture() {
        this.signalMap.put("CONFIRM", false);
        this.signalMap.put("COMPRESSION", false);
        this.signalMap.put("PUNCTURE", false);
        this.signalMap.put("STARTPUNTUREVIDEO", true);
        this.signalMap.put("START", true);
        this.signalMap.put("STARTCOLLECTIONVIDEO", false);
        this.signalMap.put("pipeLow", false);
        this.signalMap.put("pipeNormal", false);
        this.signalMap.put("PAUSED", false);
        this.signalMap.put("END", false);
    }

    public synchronized void recStart() {
        this.signalMap.put("CONFIRM", false);
        this.signalMap.put("COMPRESSION", false);
        this.signalMap.put("PUNCTURE", false);
        this.signalMap.put("STARTPUNTUREVIDEO", false);
        this.signalMap.put("START", false);
        this.signalMap.put("STARTCOLLECTIONVIDEO", true);
        this.signalMap.put("pipeLow", true);
        this.signalMap.put("pipeNormal", true);
        this.signalMap.put("PAUSED", true);
        this.signalMap.put("END", true);
    }

    public synchronized void recEnd() {
        this.signalMap.put("CONFIRM", true);
        this.signalMap.put("COMPRESSION", false);
        this.signalMap.put("PUNCTURE", false);
        this.signalMap.put("STARTPUNTUREVIDEO", false);
        this.signalMap.put("START", false);
        this.signalMap.put("STARTCOLLECTIONVIDEO", false);
        this.signalMap.put("pipeLow", false);
        this.signalMap.put("pipeNormal", false);
        this.signalMap.put("PAUSED", false);
        this.signalMap.put("END", false);
    }

    public synchronized boolean checkSignal(RecSignal recSignal) {
        switch (recSignal) {
            case CONFIRM:
                return this.signalMap.get("CONFIRM");
            case COMPRESSINON:
                return this.signalMap.get("COMPRESSION");
            case PUNCTURE:
                return this.signalMap.get("PUNCTURE");
            case STARTPUNTUREVIDEO:
                return this.signalMap.get("STARTPUNTUREVIDEO");
            case START:
                return this.signalMap.get("START");
            case STARTCOLLECTIONVIDEO:
                return this.signalMap.get("STARTCOLLECTIONVIDEO");
            case pipeLow:
                return this.signalMap.get("pipeLow");
            case pipeNormal:
                return this.signalMap.get("pipeNormal");
            case PAUSED:
                return this.signalMap.get("PAUSED");
            case END:
                return this.signalMap.get("END");
        }
        return false;
    }
}
