package com.jiaying.mediatablet.net.state.stateswitch;

import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.DataCenterRun;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.softfan.util.textUnit;

import com.jiaying.mediatablet.entity.AuthPassFace;
import com.jiaying.mediatablet.entity.DeviceEntity;
import com.jiaying.mediatablet.entity.DonorEntity;
import com.jiaying.mediatablet.entity.PersonInfo;


import com.jiaying.mediatablet.entity.ServerTime;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.RecoverState.RecordState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;


import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Date;

import java.util.HashMap;

/**
 * Created by hipil on 2016/4/13.
 */
public class WaitingForAuthState extends AbstractState {
    private static WaitingForAuthState waitingForAuthState = null;

    private WaitingForAuthState() {
    }

    public static WaitingForAuthState getInstance() {
        if (waitingForAuthState == null) {
            waitingForAuthState = new WaitingForAuthState();
        }
        return waitingForAuthState;
    }

    @Override
    public synchronized void handleMessage(RecordState recordState, ObservableZXDCSignalListenerThread listenerThread,
                                           DataCenterRun dataCenterRun, DataCenterTaskCmd cmd, RecSignal recSignal, TabletStateContext tabletStateContext) {
        switch (recSignal) {

            //记录状态

            //获取数据

            //切换状态

            //发送信号
            case TIMESTAMP:
                //记录状态

                //获取数据
                if ("timestamp".equals(cmd.getCmd())) {
                    ServerTime.curtime = Long.parseLong(textUnit.ObjToString(cmd.getValue("t")));
                }
                //切换状态

                //发送信号
                listenerThread.notifyObservers(RecSignal.TIMESTAMP);

                break;

            case AUTHPASS:

                //记录状态

                //获取数据

                //切换状态
                tabletStateContext.setCurrentState(WaitingForSerZxdcResState.getInstance());

                //发送信号
                listenerThread.notifyObservers(RecSignal.AUTHPASS);
                sendAuthPassCmd();

//                sendAuthPassPic();


                break;

//            case MANUALAUTHPASS:
//                //记录状态
//
//                //获取数据
//
//                //切换状态
//                tabletStateContext.setCurrentState(WaitingForSerZxdcResState.getInstance());
//
//                //发送信号
//                listenerThread.notifyObservers(RecSignal.AUTHPASS);
//                sendManualAuthPassCmd();
//
//                break;

            case RECORDDONORVIDEO:

                //记录状态

                //获取数据

                //切换状态
                tabletStateContext.setCurrentState(RecordAuthVideoState.getInstance());

                //发送信号
                listenerThread.notifyObservers(RecSignal.RECORDDONORVIDEO);
                break;

            case RESTART:
                //记录状态

                //获取数据

                //切换状态

                //发送信号
                listenerThread.notifyObservers(RecSignal.RESTART);
                break;
        }
    }

    private void sendAuthPassCmd() {
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
        retcmd.setCmd("authentication_donor");
        retcmd.setHasResponse(true);
        retcmd.setLevel(2);
        HashMap<String, Object> values = new HashMap<>();
        values.put("donorId", DonorEntity.getInstance().getIdentityCard().getId());
        values.put("deviceId", DeviceEntity.getInstance().getAp());
        values.put("isManual", "false");
        retcmd.setValues(values);
        clientService.getApDataCenter().addSendCmd(retcmd);
    }

    private void sendManualAuthPassCmd() {
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
        retcmd.setCmd("authentication_donor");
        retcmd.setHasResponse(true);
        retcmd.setLevel(2);
        HashMap<String, Object> values = new HashMap<>();
        values.put("donorId", DonorEntity.getInstance().getIdentityCard().getId());
        values.put("deviceId", DeviceEntity.getInstance().getAp());
        values.put("isManual", "true");
        retcmd.setValues(values);
        clientService.getApDataCenter().addSendCmd(retcmd);
    }

    private void sendAuthPassPic() {
        PersonInfo personInfo = DonorEntity.getInstance().getIdentityCard();
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        DataCenterTaskCmd retcmd = new DataCenterTaskCmd();

        if (AuthPassFace.authFace != null)
        // 这里的copy是整张图像
        {
            Mat copy = AuthPassFace.authFace;

            MatOfByte mob = new MatOfByte();

            Imgcodecs.imencode(".jpg", copy, mob);

            byte[] byteArray = mob.toArray();

            retcmd.setCmd("authpass_pic");
            retcmd.setHasResponse(true);
            retcmd.setLevel(2);
            HashMap<String, Object> values = new HashMap<String, Object>();
            values.put("face", byteArray);
            values.put("face_w", copy.cols());
            values.put("face_h", copy.rows());
            values.put("faceType", copy.type());
            values.put("date", new Date(System.currentTimeMillis()));

            values.put("donorId", personInfo.getId());

            retcmd.setValues(values);
            clientService.getApDataCenter().addSendCmd(retcmd);
        }

    }

}
