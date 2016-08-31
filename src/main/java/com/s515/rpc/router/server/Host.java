package com.s515.rpc.router.server;

/**
 * Created by Administrator on 8/31/2016.
 */
public class Host {
    private String host;
    private int port;

    public Host(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
