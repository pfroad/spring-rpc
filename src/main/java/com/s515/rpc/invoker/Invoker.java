package com.s515.rpc.invoker;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 8/25/2016.
 */
public interface Invoker {
    Object execute(Method method, Object[] args);
}
