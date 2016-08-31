package com.s515.rpc.router.server;

/**
 * Created by Administrator on 8/31/2016.
 */
abstract public class Node {
	private final static int DEFAULT_WEIGHT = 100;
	
    private String appName;
    private int weight;
    private Host host;
    
    public Node(String appName, Host host) {
		super();
		this.appName = appName;
		this.host = host;
		this.weight = DEFAULT_WEIGHT;
	}

	public Node(String appName, int weight, Host host) {
		super();
		this.appName = appName;
		this.weight = weight;
		this.host = host;
	}

	public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    abstract public String getNodeUrl();
}
