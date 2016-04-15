package com.jiaying.mediatablet.net.handler;

import android.os.Message;


import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.net.signal.RecSignal;

import java.lang.ref.SoftReference;
import java.util.Observable;

/**
 * Created by Administrator on 2015/9/13 0013.
 */
public class ObserverZXDCSignalUIHandler extends android.os.Handler implements java.util.Observer {

    private SoftReference<MainActivity> srMActivity;

    public ObserverZXDCSignalUIHandler(SoftReference<MainActivity> mActivity) {
        this.srMActivity = mActivity;
    }

    @Override
    public void handleMessage(Message msg) {

        super.handleMessage(msg);

        switch ((RecSignal) msg.obj) {

            case WAITING:

                dealSignalWaiting(this);
                break;

            // The nurse make sure the info of the donor is right.
            case CONFIRM:

                dealSignalConfirm(this);
                break;

            case AUTHPASS:
                dealSignalAuthPass(this);
                break;

            case COMPRESSINON:

                dealSignalComprssion(this);
                break;

            // The nurse punctuate the donor.
            case PUNCTURE:

                dealSignalPuncture(this);
                break;
            case STARTPUNTUREVIDEO:
                dealSignalStartPunctureVideo(this);
                break;
            // Start the collection of plasma.
            case START:

                dealSignalStart(this);
                break;

            // Start the play the video collection of plasma.
            case STARTCOLLECTIONVIDEO:

                dealSignalStartCollcetionVideo(this);
                break;

            case TOVIDEO:
                dealSignalToVideo(this);
                break;

            case TOSURF:
                dealSignalToSurf(this);
                break;

            case TOSUGGEST:
                dealSignalToSuggest(this);
                break;

            case CLICKSUGGESTION:
                dealSignalClickSuggestion(this);
                break;

            case CLICKEVALUATION:
                dealSignalClickEvaluation(this);
                break;

            case TOAPPOINT:
                dealSignalToAppoint(this);
                break;

            case CLICKAPPOINTMENT:
                dealSignalClickAppointment(this);
                break;

            // The pressure is not enough, recommend the donor to make a fist.
            case PIPELOW:
                dealSignalStartFist(this);
                break;

            case PIPENORMAL:
                dealSignalStopFist(this);
                break;

            case SAVEAPPOINTMENT:
                dealSignalSaveAppointment(this);
                break;

            case SAVESUGGESTION:
                dealSignalSaveSuggestion(this);
                break;

            case SAVEEVALUATION:
                dealSignalSaveEvaluation(this);
                break;

            case VIDEOTOMAIN:
                dealSignalVideoFinish(this);
                break;

            case BACKTOVIDEOLIST:
                dealSignalBackToVideoList(this);
                break;

            case STARTVIDEO:
                dealSignalStartVideo(this);
                break;

            // The collection is over.
            case END:

                dealSignalEnd(this);
                break;

            default:
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Message msg = Message.obtain();
        switch ((RecSignal) data) {
            case WAITING:
                msg.obj = RecSignal.WAITING;
                sendMessage(msg);
                break;

            case CONFIRM:
                msg.obj = RecSignal.CONFIRM;
                sendMessage(msg);
                break;

            case AUTHPASS:
                msg.obj = RecSignal.AUTHPASS;
                sendMessage(msg);
                break;

            case COMPRESSINON:
                msg.obj = RecSignal.COMPRESSINON;
                sendMessage(msg);
                break;

            case PUNCTURE:
                msg.obj = RecSignal.PUNCTURE;
                sendMessage(msg);
                break;

            case STARTPUNTUREVIDEO:
                msg.obj = RecSignal.STARTPUNTUREVIDEO;
                sendMessage(msg);
                break;

            case START:
                msg.obj = RecSignal.START;
                sendMessage(msg);
                break;

            case STARTCOLLECTIONVIDEO:
                msg.obj = RecSignal.STARTCOLLECTIONVIDEO;
                sendMessage(msg);
                break;

            case TOVIDEO:
                msg.obj = RecSignal.TOVIDEO;
                sendMessage(msg);
                break;

            case TOSURF:
                msg.obj = RecSignal.TOSURF;
                sendMessage(msg);
                break;

            case TOSUGGEST:
                msg.obj = RecSignal.TOSUGGEST;
                sendMessage(msg);
                break;

            case CLICKSUGGESTION:
                msg.obj = RecSignal.CLICKSUGGESTION;
                sendMessage(msg);
                break;

            case CLICKEVALUATION:
                msg.obj = RecSignal.CLICKEVALUATION;
                sendMessage(msg);
                break;

            case TOAPPOINT:
                msg.obj = RecSignal.TOAPPOINT;
                sendMessage(msg);
                break;

            case CLICKAPPOINTMENT:
                msg.obj = RecSignal.CLICKAPPOINTMENT;
                sendMessage(msg);
                break;

            case PIPELOW:
                msg.obj = RecSignal.PIPELOW;
                sendMessage(msg);
                break;

            case PIPENORMAL:
                msg.obj = RecSignal.PIPENORMAL;
                sendMessage(msg);
                break;

            case SAVEAPPOINTMENT:
                msg.obj = RecSignal.SAVEAPPOINTMENT;
                sendMessage(msg);
                break;

            case SAVESUGGESTION:
                msg.obj = RecSignal.SAVESUGGESTION;
                sendMessage(msg);
                break;

            case SAVEEVALUATION:
                msg.obj = RecSignal.SAVEEVALUATION;
                sendMessage(msg);
                break;
            case BACKTOVIDEOLIST:
                msg.obj = RecSignal.BACKTOVIDEOLIST;
                sendMessage(msg);
                break;

            case STARTVIDEO:
                msg.obj = RecSignal.STARTVIDEO;
                sendMessage(msg);
                break;

            case END:
                msg.obj = RecSignal.END;
                sendMessage(msg);
                break;

            default:
                break;
        }
    }

    private void dealSignalWaiting(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealWaiting();

    }

    private void dealSignalConfirm(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealConfirm();
    }

    private void dealSignalAuthPass(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealAuthPass();
    }

    private void dealSignalComprssion(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealCompression();
    }

    private void dealSignalPuncture(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealPuncture();
    }

    private void dealSignalStartPunctureVideo(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealStartPunctureVideo("/sdcard/kindness.mp4");
    }

    private void dealSignalStart(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealStart();
    }

    private void dealSignalStartCollcetionVideo(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealStartCollcetionVideo("/sdcard/kindness.mp4");
    }

    private void dealSignalToVideo(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealToVideo();
    }

    private void dealSignalToSurf(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealToSurf();
    }

    private void dealSignalToSuggest(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealToAdvice();
    }

    private void dealSignalClickSuggestion(ObserverZXDCSignalUIHandler observerMainHandler) {
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealSuggestClick();
    }

    private void dealSignalClickEvaluation(ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler) {
        MainActivity mainActivity = observerZXDCSignalUIHandler.srMActivity.get();
        mainActivity.dealEvaluationClick();
    }

    private void dealSignalToAppoint(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealToAppointment();
    }

    private void dealSignalClickAppointment(ObserverZXDCSignalUIHandler observerMainHandler) {
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealAppointClick();
    }

    private void dealSignalSaveSuggestion(ObserverZXDCSignalUIHandler observerMainHandler) {
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealSaveSuggestion();
    }

    private void dealSignalSaveEvaluation(ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler) {
        MainActivity mainActivity = observerZXDCSignalUIHandler.srMActivity.get();
        mainActivity.dealSaveEvaluation();
    }

    private void dealSignalSaveAppointment(ObserverZXDCSignalUIHandler observerMainHandler) {
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealSaveAppointment();
    }


    private void dealSignalStartFist(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealStartFist();
    }

    private void dealSignalStopFist(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealStopFist();
    }

    private void dealSignalVideoFinish(ObserverZXDCSignalUIHandler observerMainHandler) {
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealBackToVideoList();
    }

    private void dealSignalBackToVideoList(ObserverZXDCSignalUIHandler observerMainHandler) {
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealBackToVideoList();
    }

    private void dealSignalStartVideo(ObserverZXDCSignalUIHandler observerMainHandler){
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealStartVideo();
    }

    private void dealSignalEnd(ObserverZXDCSignalUIHandler observerMainHandler) {

        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        mainActivity.dealEnd();
    }

}


