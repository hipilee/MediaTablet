package com.jiaying.mediatablet.net.state.RecoverState;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.stateswitch.CollectionState;
import com.jiaying.mediatablet.net.state.stateswitch.EndState;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForAuthState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForCheckOverState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForCompressionState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForPunctureState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForResponseState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForStartState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/3.
 */
public class RecoverState {
    public void recover(RecordState recordState, ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread) {
        recordState.retrieve();
        String state = recordState.getState();
        if (state == null) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(WaitingForCheckOverState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.CHECKSTART);
        } else if (StateIndex.WAITINGFORCHECKOVER.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(WaitingForCheckOverState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.CHECKSTART);
        } else if (StateIndex.WAITINGFORGETRES.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(WaitingForResponseState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.CHECKOVER);
        } else if (StateIndex.WAITINGFORAUTH.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(WaitingForAuthState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.CONFIRM);
        } else if (StateIndex.WAITINGFORCOMPRESSION.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(WaitingForCompressionState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.AUTHRESOK);
        } else if (StateIndex.WAITINGFORPUNCTURE.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(WaitingForPunctureState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.COMPRESSINON);
        } else if (StateIndex.WAITINGFORSTART.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(WaitingForStartState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.PUNCTURE);
        } else if (StateIndex.COLLECTION.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(CollectionState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.START);
        } else if (StateIndex.END.equals(state)) {
            //设置当前的状态
            TabletStateContext.getInstance().setCurrentState(EndState.getInstance());
            //使当前页面跳转到该状态下的样子
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.END);
        }
    }

}
