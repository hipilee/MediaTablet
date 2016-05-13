package com.jiaying.mediatablet.net.serveraddress;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hipil on 2016/5/7.
 */
public class LogServerCreator extends AbstractCreator {
    @Override
    public  AbstractServer creator(Activity activity) {
        SharedPreferences settings;
        LogServer logServer;

        settings = activity.getPreferences(Context.MODE_PRIVATE);
        logServer = new LogServer();

        logServer.setIp(settings.getString("log_server_ip", "127.0.0.0"));
        logServer.setPort(settings.getInt("log_server_port", 8000));
        return logServer;
    }
}
