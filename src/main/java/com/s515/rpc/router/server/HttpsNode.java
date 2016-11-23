package com.s515.rpc.router.server;

/**
 * Created by Administrator on 8/31/2016.
 */
public class HttpsNode extends Node {
    private final static String URL_PREFIX = "https://";
    
    public HttpsNode(String appName, Host host) {
		super(appName, host);
	}

	public HttpsNode(String appName, int weight, Host host) {
		super(appName, weight, host);
	}
    

	@Override
    public String getNodeUrl() {
        StringBuffer url = new StringBuffer();
        url.append(URL_PREFIX);
        url.append(getHost().getHost());
        if (getHost().getPort() != 80) {
        	url.append(":");
            url.append(getHost().getPort());
        }
        url.append("/");
        url.append(getAppName());
        return url.toString();
    }
}
