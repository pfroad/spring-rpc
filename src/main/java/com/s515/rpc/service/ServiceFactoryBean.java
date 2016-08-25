package com.s515.rpc.service;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by Administrator on 8/25/2016.
 */
public class ServiceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> serviceType;
    private ServiceRegistry serviceRegistry;

    public T getObject() throws Exception {
        return null;
    }

    public Class<T> getObjectType() {
        return this.serviceType;
    }

    public boolean isSingleton() {
        return true;
    }
}
