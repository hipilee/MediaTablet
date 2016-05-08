package com.jiaying.mediatablet.entity;

/**
 * Created by hipil on 2016/4/25.
 */
public class DevEntity {
    private static DevEntity ourInstance = new DevEntity();
    private String ap;
    private String Org;
    private String Password;
    private String ServerAp;
    private String ServerOrg;

    private DevEntity() {

    }

    public String getAp() {
        return ap;
    }

    public String getOrg() {
        return Org;
    }

    public String getPassword() {
        return Password;
    }

    public String getServerAp() {
        return ServerAp;
    }

    public String getServerOrg() {
        return ServerOrg;
    }

    public void setAp(String ap) {
        this.ap = ap;
    }

    public void setOrg(String org) {
        Org = org;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setServerAp(String serverAp) {
        ServerAp = serverAp;
    }

    public void setServerOrg(String serverOrg) {
        ServerOrg = serverOrg;
    }

    public synchronized static DevEntity getInstance() {
        return ourInstance;
    }

}
