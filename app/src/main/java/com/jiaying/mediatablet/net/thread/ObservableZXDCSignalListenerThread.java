package com.jiaying.mediatablet.net.thread;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


import android.app.Activity;
import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.IDataCenterProcess;
import android.softfan.dataCenter.config.DataCenterClientConfig;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.dataCenter.task.IDataCenterNotify;

import android.util.Log;

import com.jiaying.mediatablet.entity.DevEntity;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecoverState;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;
import com.jiaying.mediatablet.net.utils.Conversion;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;


/**
 * Created by hipilee on 2014/11/19.
 */
// Consider using AsyncTask or HandlerThread
public class ObservableZXDCSignalListenerThread extends Thread implements IDataCenterNotify, IDataCenterProcess {

    private ObservableHint observableHint;

    public Boolean isContinue() {
        return isContinue;
    }

    private Boolean isContinue = true;

    private RecordState recordState;
    private RecoverState recoverState;


    private static DataCenterClientService clientService;

    public ObservableZXDCSignalListenerThread(RecordState recordState) {
        Log.e("camera", "ObservableZXDCSignalListenerThread constructor" + this.toString());

        this.observableHint = new ObservableHint();

        this.recordState = recordState;
        this.recoverState = new RecoverState();

    }

    public void addObserver(Observer observer) {
        observableHint.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        observableHint.deleteObserver(observer);
    }

    public synchronized void notifyObservers(RecSignal signal) {
        if (signal == RecSignal.POWEROFF) {
            recordState.commit();
        } else {
            observableHint.notifyObservers(signal);
        }
    }


    @Override
    public void run() {
        super.run();

        // there must be a pause if without there will be something wrong.
        Log.e("camera", "run()" + this.toString());
        recoverState.recover(recordState, this);
        TabletStateContext.getInstance().setAbility(true);
        DataCenterClientService.shutdown();

        clientService = DataCenterClientService.get(DevEntity.getInstance().getDeviceId(), "*");
        if (clientService == null) {
            DataCenterClientConfig config = new DataCenterClientConfig();
            config.setAddr("192.168.0.94");
            config.setPort(10014);
            config.setAp(DevEntity.getInstance().getDeviceId());
            config.setOrg("*");
            config.setPassword("123456");
            config.setServerAp("JzDataCenter");
            config.setServerOrg("*");
            config.setProcess(this);
            // config.setPushThreadClass(DataCenterClientTestService.class);

            DataCenterClientService.startup(config);

            clientService = DataCenterClientService.get(DevEntity.getInstance().getDeviceId(), "*");
        }

        //检查通过
        TabletStateContext.getInstance().handleMessge(recordState, this, null, null, RecSignal.CHECKOVER);


//        while (isContinue) {
//            synchronized (this) {
//                try {
//
//                    this.wait(5000);
//
//                } catch (InterruptedException e) {
//                }
//            }
//        }
    }

    public synchronized void finishReceivingSignal() {
        Log.e("camera", " finish");
        notify();
    }

    public synchronized void commitSignal(Boolean isInitiative) {
        try {
            wait();
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


    public void onSend(DataCenterTaskCmd selfCmd) throws DataCenterException {
    }

    public void onResponse(DataCenterTaskCmd selfCmd, DataCenterTaskCmd responseCmd) throws DataCenterException {
    }

    public void onFree(DataCenterTaskCmd selfCmd) {
    }

    public void processMsg(DataCenterRun dataCenterRun, DataCenterTaskCmd cmd) throws DataCenterException {
        Log.e("ERROR CMD", "=======" + cmd.getCmd() + "==============" + this.toString());

        RecSignal recSignal = Conversion.conver(cmd.getCmd());

        TabletStateContext tabletStateContext = TabletStateContext.getInstance();
        tabletStateContext.handleMessge(recordState, this, dataCenterRun, cmd, Conversion.conver(cmd.getCmd()));

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


}
