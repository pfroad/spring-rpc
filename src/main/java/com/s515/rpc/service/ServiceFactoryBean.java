package com.s515.rpc.service;

import com.s515.rpc.invoker.Invoker;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by Administrator on 8/25/2016.
 */
public class ServiceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> serviceType;
    private ServiceRegistry serviceRegistry;
    private Invoker invoker;

    public T getObject() throws Exception {
        return serviceRegistry.getService(serviceType, invoker);
    }

    public Class<T> getObjectType() {
        return this.serviceType;
    }

    public boolean isSingleton() {
        return true;
    }

    public Class<T> getServiceType() {
        return serviceType;
    }

    public void setServiceType(Class<T> serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }
}
