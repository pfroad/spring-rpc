package com.s515.rpc.service;

import com.s515.rpc.proxy.ServiceProxyFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 8/25/2016.
 */
public class ServiceRegistry {
    private final Map<Class<?>, ServiceProxyFactory<?>> knownServices = new ConcurrentHashMap<Class<?>, ServiceProxyFactory<?>>();

    public <T> void addService(Class<T> type) {
        if (!knownServices.containsKey(type)) {
            knownServices.put(type, new ServiceProxyFactory<T>(type));
        }
    }
}
