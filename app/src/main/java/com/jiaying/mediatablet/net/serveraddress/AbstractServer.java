package com.jiaying.mediatablet.net.serveraddress;

/**
 * Created by hipil on 2016/5/6.
 */
public abstract class AbstractServer {
    protected String ip;
    protected int port;

    public abstract String getIp();

    public abstract int getPort();


    public abstract void setIp(String ip);

    public abstract void setPort(int port);
}
