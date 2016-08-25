package com.s515.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 8/25/2016.
 */
public class ServiceProxyFactory<T> {
    private Class<T> type;

    public ServiceProxyFactory(Class<T> type) {
        this.type = type;
    }

    public T newInstance(Class<T> type) {
        if (type.isInterface()) {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, new JdkDynamicProxy(type));
        } else {
            return new CglibProxy<T>().getProxy(type);
        }
    }

    public T newInstance() {
        return newInstance(type);
    }
}
