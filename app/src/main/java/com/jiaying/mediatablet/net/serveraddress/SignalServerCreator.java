package com.jiaying.mediatablet.net.serveraddress;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hipil on 2016/5/7.
 */
public class SignalServerCreator extends AbstractCreator {
    @Override
    public AbstractServer creator(Activity activity) {
        SharedPreferences settings;
        LogServer signalServer;

        settings = activity.getPreferences(Context.MODE_PRIVATE);
        signalServer = new LogServer();

        signalServer.setIp(settings.getString("signal_server_ip", "127.0.0.0"));
        signalServer.setPort(settings.getInt("signal_server_port", 8000));
        return signalServer;
    }
}
