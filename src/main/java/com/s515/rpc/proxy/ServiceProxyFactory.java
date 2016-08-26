package com.s515.rpc.proxy;

import com.s515.rpc.invoker.Invoker;

import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 8/25/2016.
 */
public class ServiceProxyFactory<T> {
    private Class<T> type;

    public ServiceProxyFactory(Class<T> type) {
        this.type = type;
    }

    public T newInstance(Invoker invoker) {
        if (type.isInterface()) {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, new JdkDynamicProxy(type, invoker));
        } else {
            return new CglibProxy<T>(invoker).getProxy(type);
        }
    }

//    public T newInstance(Invoker invoker) {
//        return newInstance(invoker);
//    }
}
