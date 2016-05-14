package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterException;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.DonorEntity;
import com.jiaying.mediatablet.entity.ServerTime;
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

            case TIMESTAMP:
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                break;

            case CONFIRM:
                //获取到浆员信息状态
                recordState.recConfirm();

                //记录浆员信息
                if (cmd != null) {
                    setDonor(DonorEntity.getInstance(), cmd);
                }
                listenerThread.notifyObservers(RecSignal.CONFIRM);

                //切换到认证状态
                TabletStateContext.getInstance().setCurrentState(WaitingForAuthState.getInstance());

                break;
//            case LOWPOWER:
//
//                listenerThread.notifyObservers(RecSignal.LOWPOWER);
//
//                TabletStateContext.getInstance().setCurrentState(WaitingForResponseState.getInstance());
//                break;

            case SETTINGS:

                //发送信号
                listenerThread.notifyObservers(RecSignal.SETTINGS);

                break;

            case RESTART:

                //发送信号
                listenerThread.notifyObservers(RecSignal.RESTART);

                break;

        }
    }

//    private

    private void setDonor(DonorEntity donorEntity, DataCenterTaskCmd cmd) {

        //献浆员ID号
        donorEntity.setDonorID(textUnit.ObjToString(cmd.getValue("donor_id")));

        //档案里的名字和身份证里的名字
        donorEntity.setIdName(textUnit.ObjToString(cmd.getValue("donor_name")));
        donorEntity.setDocumentName(textUnit.ObjToString(cmd.getValue("name")));

        //档案里的性别和身份证里的性别
        donorEntity.setGender(textUnit.ObjToString(cmd.getValue("gender")));
        donorEntity.setSex(textUnit.ObjToString(cmd.getValue("sex")));

        //档案里的地址和身份证里的地址
        donorEntity.setAddress(textUnit.ObjToString(cmd.getValue("address")));
        donorEntity.setDz(textUnit.ObjToString(cmd.getValue("dz")));

        //档案里的照片和身份证里的照片
        donorEntity.setFaceBitmap(BitmapUtils.base64ToBitmap(textUnit.ObjToString(cmd.getValue("face"))));
        donorEntity.setDocumentFaceBitmap(BitmapUtils.base64ToBitmap(textUnit.ObjToString(cmd.getValue("photo"))));

        //年龄
        donorEntity.setAge(textUnit.ObjToString(cmd.getValue("age")));

        //民族
        donorEntity.setNation(textUnit.ObjToString(cmd.getValue("nationality")));

        //出生年月日
        donorEntity.setYear(textUnit.ObjToString(cmd.getValue("year")));
        donorEntity.setMonth(textUnit.ObjToString(cmd.getValue("month")));
        donorEntity.setDay(textUnit.ObjToString(cmd.getValue("day")));
    }

}
