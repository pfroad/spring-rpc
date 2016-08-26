package com.s515.rpc.service;

import com.s515.rpc.exceptions.BindingException;
import com.s515.rpc.exceptions.ServiceNotFoundException;
import com.s515.rpc.invoker.Invoker;
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

    public <T> T getService(Class<T> type, Invoker invoker) {
        if (!knownServices.containsKey(type)) {
            throw new ServiceNotFoundException("Type " + type + " is not known to the ServiceRegistry.");
        }

        final ServiceProxyFactory<T> serviceProxyFactory = (ServiceProxyFactory<T>) knownServices.get(type);

        try {
            return serviceProxyFactory.newInstance(invoker);
        } catch (Exception e) {
            throw new BindingException("Error getting service instance. Cause: " + e, e);
        }
    }
}
