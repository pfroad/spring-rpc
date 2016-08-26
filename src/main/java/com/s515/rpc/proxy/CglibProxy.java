package com.s515.rpc.proxy;

import com.s515.rpc.invoker.Invoker;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 8/25/2016.
 */
public class CglibProxy<T> implements MethodInterceptor {
    private Invoker invoker;

    public CglibProxy(Invoker invoker) {
        this.invoker = invoker;
    }

    public T getProxy(Class<T> tClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(tClass);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return null;
    }
}
