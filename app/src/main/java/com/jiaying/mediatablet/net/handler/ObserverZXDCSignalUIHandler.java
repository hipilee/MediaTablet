package com.jiaying.mediatablet.net.handler;

import android.os.Message;
import android.util.Log;


import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.fragment.OverFragment;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.thread.AniThread;

import java.lang.ref.SoftReference;
import java.util.Observable;

/**
 * Created by Administrator on 2015/9/13 0013.
 */
public class ObserverZXDCSignalUIHandler extends android.os.Handler implements java.util.Observer {

    private SoftReference<MainActivity> srMActivity;
    private AniThread aniThread = null;
    private Boolean isDeal = true;

    public Boolean getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(Boolean isDeal) {
        this.isDeal = isDeal;
    }

    public ObserverZXDCSignalUIHandler(SoftReference<MainActivity> mActivity) {
        Log.e("camera", "ObserverZXDCSignalUIHandler constructor" + mActivity.get().toString());

        this.srMActivity = mActivity;
    }

    @Override
    public void handleMessage(Message msg) {

        super.handleMessage(msg);
        if (isDeal) {
            switch ((RecSignal) msg.obj) {

                case WAITING:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-WAITING");

                    dealSignalWaiting(this);
                    break;

                // The nurse make sure the info of the donor is right.
                case CONFIRM:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-CONFIRM");

                    dealSignalConfirm(this);
                    break;

                case COMPRESSINON:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-COMPRESSINON");

                    dealSignalComprssion(this);
                    break;

                // The nurse punctuate the donor.
                case PUNCTURE:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-PUNCTURE");

                    dealSignalPuncture(this);
                    break;
                case STARTPUNTUREVIDEO:
                    dealSignalStartPunctureVideo(this);
                    break;
                // Start the collection of plasma.
                case START:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-START");

                    dealSignalStart(this);
                    break;

                // Start the play the video collection of plasma.
                case STARTCOLLECTIONVIDEO:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-START");

                    dealSignalStartCollcetionVideo(this);
                    break;

                // The pressure is not enough, recommend the donor to make a fist.
                case pipeLow:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-pipeLow");

                    dealSignalStartFist(this);
                    break;

                case pipeNormal:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-pipeNormal");

                    dealSignalStopFist(this);
                    break;

                    // The collection is over.
                case END:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-handleMessage-end");

                    dealSignalEnd(this);
                    break;
//                case TOHOME:
////                    // Initialize the first fragment which is the slogan which says "献血献浆同样光荣！"
////                    SloganFragment sloganFragment = SloganFragment.newInstance(srMActivity.get().getString(R.string.slogan), "");
////                    // Construct the fragment manager to manager the fragment.
////                    FragmentManager fragmentManager = srMActivity.get().getFragmentManager();
////                    FragmentTransaction transaction = fragmentManager.beginTransaction();
////                    // Add whatever is in the fragment_container view with this fragment,
////                    // and add the transaction to the back stack
////                    transaction.replace(R.id.main_ui_fragment_container, sloganFragment);
////                    // Commit the transaction which won't occur immediately.
////                    transaction.commit();
//                    break;

                default:
            }
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

            case pipeLow:
                msg.obj = RecSignal.pipeLow;
                sendMessage(msg);
                break;

            case pipeNormal:
                msg.obj = RecSignal.pipeNormal;
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
        Log.e("error", "dealSignalConfirm");
//        if (observerMainHandler.srMActivity.get().getVisibility()) {
        Log.e("error", "dealSignalConfirm true");
        // The main ui switches to welcome fragment which says "某某，欢迎你来献浆！"
        observerMainHandler.srMActivity.get().dealWaiting();

//        } else {
//            Log.e("error", "dealSignalConfirm false");
//
//    }
    }

    private void dealSignalConfirm(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalConfirm");
//        if (observerMainHandler.srMActivity.get().getVisibility()) {
        Log.e("error", "dealSignalConfirm true");
        // The main ui switches to welcome fragment which says "某某，欢迎你来献浆！"
        observerMainHandler.srMActivity.get().dealConfirm();

//        } else {
//            Log.e("error", "dealSignalConfirm false");
//
//    }
    }

    private void dealSignalComprssion(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalPuncture");
        observerMainHandler.srMActivity.get().dealCompression();
//        if (observerMainHandler.srMActivity.get().getVisibility()) {
//            Log.e("error", "dealSignalPuncture true");
////            observerMainHandler.srMActivity.get().getActionBar().hide();
//            // Begin playing the promotion video.
//            observerMainHandler.srMActivity.get().playVideoFragment = PlayVideoFragment.newInstance("", "");
//            observerMainHandler.srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.main_ui_fragment_container, observerMainHandler.srMActivity.get().playVideoFragment).commit();
//        } else {
//            Log.e("error", "dealSignalPuncture false");
//
//        }

    }

    private void dealSignalPuncture(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalPuncture");
        observerMainHandler.srMActivity.get().dealPuncture();
//        if (observerMainHandler.srMActivity.get().getVisibility()) {
//            Log.e("error", "dealSignalPuncture true");
////            observerMainHandler.srMActivity.get().getActionBar().hide();
//            // Begin playing the promotion video.
//            observerMainHandler.srMActivity.get().playVideoFragment = PlayVideoFragment.newInstance("", "");
//            observerMainHandler.srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.main_ui_fragment_container, observerMainHandler.srMActivity.get().playVideoFragment).commit();
//        } else {
//            Log.e("error", "dealSignalPuncture false");
//
//        }

    }

    private void dealSignalStartPunctureVideo(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalPuncture");
        observerMainHandler.srMActivity.get().dealStartPunctureVideo("/sdcard/kindness.mp4");
//        if (observerMainHandler.srMActivity.get().getVisibility()) {
//            Log.e("error", "dealSignalPuncture true");
////            observerMainHandler.srMActivity.get().getActionBar().hide();
//            // Begin playing the promotion video.
//            observerMainHandler.srMActivity.get().playVideoFragment = PlayVideoFragment.newInstance("", "");
//            observerMainHandler.srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.main_ui_fragment_container, observerMainHandler.srMActivity.get().playVideoFragment).commit();
//        } else {
//            Log.e("error", "dealSignalPuncture false");
//
//        }

    }

    private void dealSignalStart(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalStart");
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        observerMainHandler.srMActivity.get().dealStart();

    }

    private void dealSignalStartCollcetionVideo(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalStart");
        MainActivity mainActivity = observerMainHandler.srMActivity.get();
        observerMainHandler.srMActivity.get().dealSignalStartCollcetionVideo("/sdcard/donation.mp4");
    }

    private void dealSignalStartFist(ObserverZXDCSignalUIHandler observerMainHandler) {
        observerMainHandler.srMActivity.get().dealStartFist();
//        Log.e("error", "dealSignalStartFist");

    }

    private void dealSignalStopFist(ObserverZXDCSignalUIHandler observerMainHandler) {
//        Log.e("error", "dealSignalStopFist");
//        MainActivity mainActivity = observerMainHandler.srMActivity.get();
//        ImageView ivStopFistHint = (ImageView) mainActivity.findViewById(R.id.iv_stop_fist);
//        AniThread startFist = new AniThread(mainActivity, ivStopFistHint, "startfist.gif", 150);
//        ivStopFistHint.setVisibility(View.VISIBLE);
        observerMainHandler.srMActivity.get().dealStopFist();
    }

    private void dealSignalEnd(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "结束");

        observerMainHandler.srMActivity.get().dealEnd();
    }

}


