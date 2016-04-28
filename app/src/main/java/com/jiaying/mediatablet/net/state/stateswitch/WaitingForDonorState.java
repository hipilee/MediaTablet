package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.Donor;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.mediatablet.utils.BitmapUtils;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class WaitingForDonorState extends AbstractState {
    private static WaitingForDonorState waittingForDonorState = null;

    private WaitingForDonorState() {
    }

    public static WaitingForDonorState getInstance() {
        if (waittingForDonorState == null) {
            waittingForDonorState = new WaitingForDonorState();
        }
        return waittingForDonorState;
    }

    @Override
    public synchronized void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread, DataCenterRun dataCenterRun,
                                           DataCenterTaskCmd cmd, RecSignal recSignal) {
        switch (recSignal) {

            case CONFIRM:
                //record state
                recordState.recConfirm();

                //new the response cmd
                DataCenterTaskCmd retcmd = new DataCenterTaskCmd();

                //deal info
                setDonor(Donor.getInstance(), cmd);
                listenerThread.notifyObservers(RecSignal.CONFIRM);

                //Construct cmd
                setConfirmResCmd(retcmd,cmd);

                //send retcmd
                try {
                    dataCenterRun.sendResponseCmd(retcmd);
                } catch (DataCenterException e) {
                    e.printStackTrace();
                } finally {
                }

                //switch the state
                TabletStateContext.getInstance().setCurrentState(WaitingForAuthState.getInstance());

                break;
        }
    }

//    private

    private void setDonor(Donor donor, DataCenterTaskCmd cmd) {

        //献浆员ID号
        donor.setDonorID(textUnit.ObjToString(cmd.getValue("donor_id")));

        //档案里的名字和身份证里的名字
        donor.setIdName(textUnit.ObjToString(cmd.getValue("donor_name")));
        donor.setDocumentName(textUnit.ObjToString(cmd.getValue("name")));

        //档案里的性别和身份证里的性别
        donor.setGender(textUnit.ObjToString(cmd.getValue("gender")));
        donor.setSex(textUnit.ObjToString(cmd.getValue("sex")));

        //档案里的地址和身份证里的地址
        donor.setAddress(textUnit.ObjToString(cmd.getValue("address")));
        donor.setDz(textUnit.ObjToString(cmd.getValue("dz")));

        //档案里的照片和身份证里的照片
        donor.setFaceBitmap(BitmapUtils.base64ToBitmap(textUnit.ObjToString(cmd.getValue("face"))));
        donor.setDocumentFaceBitmap(BitmapUtils.base64ToBitmap(textUnit.ObjToString(cmd.getValue("photo"))));

        //年龄
        donor.setAge(textUnit.ObjToString(cmd.getValue("age")));

        //民族
        donor.setNation(textUnit.ObjToString(cmd.getValue("nationality")));

        //出生年月日
        donor.setYear(textUnit.ObjToString(cmd.getValue("year")));
        donor.setMonth(textUnit.ObjToString(cmd.getValue("month")));
        donor.setDay(textUnit.ObjToString(cmd.getValue("day")));
    }

    private void setConfirmResCmd(DataCenterTaskCmd retcmd,DataCenterTaskCmd cmd){
        retcmd.setSeq(cmd.getSeq());
        retcmd.setCmd("response");
        HashMap<String, Object> values = new HashMap<>();
        values.put("ok", "true");
        retcmd.setValues(values);
    }


}
