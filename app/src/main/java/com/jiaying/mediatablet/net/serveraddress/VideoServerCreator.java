package com.jiaying.mediatablet.net.serveraddress;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hipil on 2016/5/7.
 */
public class VideoServerCreator extends AbstractCreator {
    @Override
    public AbstractServer creator(Activity activity) {
        SharedPreferences settings;
        VideoServer videoServer;

        settings = activity.getPreferences(Context.MODE_PRIVATE);
        videoServer = new VideoServer();

        videoServer.setIp(settings.getString("video_server_ip", "127.0.0.0"));
        videoServer.setPort(settings.getInt("video_server_port", 8000));

        return videoServer;
    }
}
