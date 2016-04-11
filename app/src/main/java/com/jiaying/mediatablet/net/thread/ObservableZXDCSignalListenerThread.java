package com.jiaying.mediatablet.net.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.IDataCenterProcess;
import android.softfan.dataCenter.config.DataCenterClientConfig;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.dataCenter.task.IDataCenterNotify;
import android.softfan.util.textUnit;

import android.util.Base64;
import android.util.Log;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.utils.FilterSignal;
import com.jiaying.mediatablet.net.utils.RecordState;


/**
 * Created by hipilee on 2014/11/19.
 */
// Consider using AsyncTask or HandlerThread
public class ObservableZXDCSignalListenerThread extends Thread implements IDataCenterNotify, IDataCenterProcess {

    private ObservableHint observableHint;

    public Boolean getIsContinue() {
        return isContinue;
    }

    private Boolean isContinue = true;

    private RecordState recordState;
    private RecoverState recoverState;
    private FilterSignal filterSignal;


    private static DataCenterClientService clientService;

    public ObservableZXDCSignalListenerThread(RecordState recordState, FilterSignal filterSignal) {
        Log.e("camera", "ObservableZXDCSignalListenerThread constructor" + "construct");

        this.observableHint = new ObservableHint();

        this.recordState = recordState;
        this.recoverState = new RecoverState();
        this.filterSignal = filterSignal;

    }

    public void addObserver(Observer observer) {
        observableHint.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        observableHint.deleteObserver(observer);
    }

    public void notifyObservers(RecSignal signal) {
        observableHint.notifyObservers(signal);
    }

    public void setIsContinue(Boolean isContinue) {
        this.isContinue = isContinue;
    }

    @Override
    public void run() {
        super.run();

        // there must be a pause if without there will be something wrong.
//        recoverState.recover(recordState, observableHint);

        clientService = DataCenterClientService.get("chair001", "*");
        if (clientService == null) {
            DataCenterClientConfig config = new DataCenterClientConfig();
            config.setAddr("192.168.0.94");
            config.setPort(10014);
            config.setAp("chair001");
            config.setOrg("*");
            config.setPassword("123456");
            config.setServerAp("JzDataCenter");
            config.setServerOrg("*");
            config.setProcess(this);
            // config.setPushThreadClass(DataCenterClientTestService.class);

            DataCenterClientService.startup(config);

            clientService = DataCenterClientService.get("chair001", "*");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            dealSignal(RecSignal.START);


        }

        while (isContinue) {
            synchronized (this) {
                try {


                    this.wait(5000);


                } catch (InterruptedException e) {
                }
            }
        }

        finishReceivingSignal();
    }

    public synchronized void finishReceivingSignal() {
        Log.e("camera", " finish");
        notify();
    }

    public synchronized void commitSignal(Boolean isInitiative) {
        try {
            Log.e("camera", "waitToCommitSignal " + 1);

            wait();

            Log.e("camera", "waitToCommitSignal " + 2);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

        // If we close the APP initiative,then reset the states.
        if (isInitiative) {
//			recordState.reset();
        }
//		recordState.commit();
    }

    private class ObservableHint extends Observable {
        private ArrayList<Observer> arrayListObserver;

        private ObservableHint() {
            arrayListObserver = new ArrayList<Observer>();
        }

        @Override
        public void addObserver(Observer observer) {
            super.addObserver(observer);
            arrayListObserver.add(observer);
        }

        @Override
        public synchronized void deleteObserver(Observer observer) {
            super.deleteObserver(observer);
            arrayListObserver.remove(observer);
        }

        @Override
        public void notifyObservers(Object data) {
            super.notifyObservers(data);
            for (Observer observer : arrayListObserver) {
                observer.update(observableHint, data);
            }
        }
    }

    private void dealSignal(RecSignal signal) {
        switch (signal) {

            case WAITING:
                notifyObservers(RecSignal.WAITING);
                break;

            case CONFIRM:
                notifyObservers(RecSignal.CONFIRM);
                break;

            case COMPRESSINON:
                notifyObservers(RecSignal.COMPRESSINON);
                break;

            case PUNCTURE:
                notifyObservers(RecSignal.PUNCTURE);
                break;

            case STARTPUNTUREVIDEO:
                notifyObservers(RecSignal.STARTPUNTUREVIDEO);
                break;

            case START:
                notifyObservers(RecSignal.START);
                break;

            case STARTCOLLECTIONVIDEO:
                notifyObservers(RecSignal.STARTCOLLECTIONVIDEO);
                break;

            case pipeLow:
                notifyObservers(RecSignal.pipeLow);
                break;

            case pipeNormal:
                notifyObservers(RecSignal.pipeNormal);
                break;

            case PAUSED:
                notifyObservers(RecSignal.PAUSED);
                break;

            case END:
                notifyObservers(RecSignal.END);
                break;

            default:
                break;

        }
    }

    private class RecoverState {

    }

    public void recMsg(RecSignal recSignal) {


        if (filterSignal.checkSignal(recSignal)) {
            dealSignal(recSignal);
        }

    }


    public void selfSleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void onSend(DataCenterTaskCmd selfCmd) throws DataCenterException {
    }

    public void onResponse(DataCenterTaskCmd selfCmd, DataCenterTaskCmd responseCmd) throws DataCenterException {
    }

    public void onFree(DataCenterTaskCmd selfCmd) {
    }

    public void processMsg(DataCenterRun dataCenterRun, DataCenterTaskCmd cmd) throws DataCenterException {
        Log.e("ERROR CMD", "=======" + cmd.getCmd() + "==============");

        if ("confirm".equals(cmd.getCmd())) {
            DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
            retcmd.setSeq(cmd.getSeq());
            retcmd.setCmd("response");

            if (filterSignal.checkSignal(RecSignal.CONFIRM)) {
                Donor donor = Donor.getInstance();
                donor.setDonorID(textUnit.ObjToString(cmd.getValue("donor_id")));
                donor.setUserName(textUnit.ObjToString(cmd.getValue("donor_name")));

                donor.setGender(textUnit.ObjToString(cmd.getValue("gender")));
                donor.setNation(textUnit.ObjToString(cmd.getValue("nationality")));
                donor.setYear(textUnit.ObjToString(cmd.getValue("year")));
                donor.setMonth(textUnit.ObjToString(cmd.getValue("month")));
                donor.setDay(textUnit.ObjToString(cmd.getValue("day")));
                donor.setAddress(textUnit.ObjToString(cmd.getValue("address")));
                donor.setFaceBitmap(base64ToBitmap(textUnit.ObjToString(cmd.getValue("face"))));


                recordState.recConfirm();
                filterSignal.recConfirm();
                dealSignal(RecSignal.CONFIRM);

                HashMap<String, Object> values = new HashMap<String, Object>();
                values.put("ok", "true");
                retcmd.setValues(values);
            }
            dataCenterRun.sendResponseCmd(retcmd);
        } else if ("startInfating".equals(cmd.getCmd())) {
            if (filterSignal.checkSignal(RecSignal.COMPRESSINON)) {
                recordState.recCompression();
                filterSignal.recCompression();
                dealSignal(RecSignal.COMPRESSINON);
            }
        } else if ("startPuncture".equals(cmd.getCmd())) {
            if (filterSignal.checkSignal(RecSignal.PUNCTURE)) {
                recordState.recPuncture();
                filterSignal.recPuncture();
                dealSignal(RecSignal.PUNCTURE);
            }

        } else if ("pipeLow".equals(cmd.getCmd())) {
            if (filterSignal.checkSignal(RecSignal.pipeLow)) {
                dealSignal(RecSignal.pipeLow);
            }

        } else if ("pipeNormal".equals(cmd.getCmd())) {
            if (filterSignal.checkSignal(RecSignal.pipeNormal)) {
                dealSignal(RecSignal.pipeNormal);
            }

        } else if ("start".equals(cmd.getCmd())) {
            DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
            retcmd.setSeq(cmd.getSeq());
            retcmd.setCmd("response");

            if (filterSignal.checkSignal(RecSignal.START)) {
                recordState.recStart();
                filterSignal.recStart();
                dealSignal(RecSignal.START);

                HashMap<String, Object> values = new HashMap<String, Object>();
                values.put("ok", "true");
                retcmd.setValues(values);
            }

            dataCenterRun.sendResponseCmd(retcmd);
        } else if ("end".equals(cmd.getCmd())) {
            DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
            retcmd.setSeq(cmd.getSeq());
            retcmd.setCmd("response");

            if (filterSignal.checkSignal(RecSignal.END)) {
                recordState.recEnd();
                filterSignal.recEnd();
                dealSignal(RecSignal.END);

                HashMap<String, Object> values = new HashMap<String, Object>();
                values.put("ok", "true");
                retcmd.setValues(values);
            }
            dataCenterRun.sendResponseCmd(retcmd);
        }
    }

    @Override
    public void onSended(DataCenterTaskCmd selfCmd) throws DataCenterException {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendTimeout(DataCenterTaskCmd selfCmd) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResponseTimeout(DataCenterTaskCmd selfCmd) {
        // TODO Auto-generated method stub

    }

    public void startMsgProcess() {
    }

    public void stopMsgProcess() {
    }

    public static DataCenterClientService getClientService() {
        return clientService;
    }


    private static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
