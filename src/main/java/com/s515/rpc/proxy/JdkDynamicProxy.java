package com.s515.rpc.proxy;

import com.s515.rpc.invoker.Invoker;
import com.s515.rpc.invoker.data.Request;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * Created by Administrator on 8/25/2016.
 */
public class JdkDynamicProxy<T> implements InvocationHandler, Serializable {
    private final Class<T> mapperInterface;
    private Invoker invoker;

    public JdkDynamicProxy(Class<T> mapperInterface, Invoker invoker) {
        this.mapperInterface = mapperInterface;
        this.invoker = invoker;
    }

    public JdkDynamicProxy(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoker.execute(method, args);
    }
}
