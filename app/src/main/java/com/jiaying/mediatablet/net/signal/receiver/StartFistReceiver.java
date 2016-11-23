package com.jiaying.mediatablet.net.signal.receiver;

import android.view.View;
import android.widget.ImageView;

import com.jiaying.mediatablet.activity.MainActivity;
import com.jiaying.mediatablet.net.signal.RecSignal;
import com.jiaying.mediatablet.thread.AniThread;

/**
 * Created by hipil on 2016/9/16.
 */
public class StartFistReceiver extends Receiver {
    private MainActivity mainActivity;

    public StartFistReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void work() {
        ImageView ivStartFistHint = this.mainActivity.getIvStartFistHint();
        if (ivStartFistHint.getVisibility() != View.VISIBLE) {
            ivStartFistHint.setVisibility(View.VISIBLE);
            AniThread startFist = this.mainActivity.getStartFist();

            if (startFist != null) {

                mainActivity.getTabletStateContext().handleMessge(mainActivity.getRecordState(),
                        mainActivity.getObservableZXDCSignalListenerThread(), null, null, RecSignal.PIPENORMAL);
            }

            startFist = new AniThread(mainActivity, ivStartFistHint, "startfist.gif", 300);
            this.mainActivity.setStartFist(startFist);
            startFist.startAni();



        }
    }
}
