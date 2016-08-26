package com.s515.rpc.invoker.http;

import com.s515.rpc.invoker.Invoker;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by Administrator on 8/25/2016.
 */
public class HttpInvoker implements Invoker {
    private CloseableHttpClient httpClient;

    @Override
    public Object execute(Object args) {
        return null;
    }
}
