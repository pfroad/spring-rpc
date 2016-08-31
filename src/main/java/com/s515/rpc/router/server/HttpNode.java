package com.s515.rpc.router.server;

/**
 * Created by Administrator on 8/31/2016.
 */
public class HttpNode extends Node {
    private final static String URL_PREFIX = "http://";
    
    public HttpNode(String appName, Host host) {
		super(appName, host);
	}

	public HttpNode(String appName, int weight, Host host) {
		super(appName, weight, host);
	}
    

    @Override
    public String getNodeUrl() {
        StringBuffer url = new StringBuffer();
        url.append(URL_PREFIX);
        url.append(getHost().getHost());
        url.append(":");
        url.append(getHost().getPort());
        url.append("/");
        url.append(getAppName());
        return url.toString();
    }
}
