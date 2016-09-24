package com.jiaying.mediatablet.net.signal.receiver;

import android.view.View;
import android.widget.ImageView;

import com.jiaying.mediatablet.activity.MainActivity;
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
            if(startFist!=null){
                startFist.finishAni();
            }
            else{
                startFist = new AniThread(mainActivity, ivStartFistHint, "startfist.gif", 150);
                this.mainActivity.setStartFist(startFist);
                startFist.startAni();
            }
        }
    }
}
