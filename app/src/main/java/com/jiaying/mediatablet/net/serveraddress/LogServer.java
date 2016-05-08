package com.jiaying.mediatablet.net.serveraddress;

/**
 * Created by hipil on 2016/5/6.
 */
public class LogServer extends AbstractServer {

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }
}
