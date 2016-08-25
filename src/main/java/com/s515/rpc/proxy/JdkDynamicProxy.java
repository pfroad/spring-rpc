package com.s515.rpc.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 8/25/2016.
 */
public class JdkDynamicProxy<T> implements InvocationHandler, Serializable {
    private final Class<T> mapperInterface;

    public JdkDynamicProxy(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
