package com.jiaying.mediatablet.net.state.RecoverState;

import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.net.state.stateswitch.CollectionState;
import com.jiaying.mediatablet.net.state.stateswitch.TabletStateContext;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForAuthState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForCheckState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForCompressionState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForDonorState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForPunctureState;
import com.jiaying.mediatablet.net.state.stateswitch.WaitingForStartState;
import com.jiaying.mediatablet.net.thread.ObservableZXDCSignalListenerThread;

/**
 * Created by hipil on 2016/4/3.
 */
public class RecoverState {
    public void recover(RecordState recordState, ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread) {
        recordState.retrieve();
        String state = recordState.getState();
        if(state==null){
            TabletStateContext.getInstance().setCurrentState(WaitingForDonorState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.WAITING);
        }
        else if (StateIndex.WAITINGFORDONOR.equals(state)) {
            TabletStateContext.getInstance().setCurrentState(WaitingForDonorState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.WAITING);
        }
        else if(StateIndex.WAITINGFORAUTH.equals(state)){
            TabletStateContext.getInstance().setCurrentState(WaitingForAuthState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.CONFIRM);
        }
        else if(StateIndex.WAITINGFORCOMPRESSION.equals(state)){
            TabletStateContext.getInstance().setCurrentState(WaitingForCompressionState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.AUTHPASS);
        }
        else if(StateIndex.WAITINGFORPUNCTURE.equals(state)){
            TabletStateContext.getInstance().setCurrentState(WaitingForPunctureState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.COMPRESSINON);
        }
        else if(StateIndex.WAITINGFORSTART.equals(state)){
            TabletStateContext.getInstance().setCurrentState(WaitingForStartState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.PUNCTURE);
        }
        else if(StateIndex.WAITINGFOREND.equals(state)){
            TabletStateContext.getInstance().setCurrentState(CollectionState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.START);
        }
        else if(StateIndex.WAITINGFORCHECK.equals(state)){
            TabletStateContext.getInstance().setCurrentState(WaitingForCheckState.getInstance());
            observableZXDCSignalListenerThread.notifyObservers(RecSignal.END);
        }
    }
}
