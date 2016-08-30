package com.s515.rpc.invoker.data;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by CYMAC on 8/29/16.
 */
public class Request {
    public Request() {
    }

    public Request(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    private Map<String, Object> parameters;

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    public Integer getInt(String key) throws NumberFormatException {
        return parameters.containsKey(key) ? StringUtils.isEmpty(parameters.get(key)) ? null : Integer.parseInt(parameters.get(key).toString()) : null;
    }

    public Long getLong(String key) throws NumberFormatException {
        return parameters.containsKey(key) ? StringUtils.isEmpty(parameters.get(key)) ? null : Long.parseLong(parameters.get(key).toString()) : null;
    }

    public Double getDouble(String key) throws NumberFormatException {
        return parameters.containsKey(key) ? StringUtils.isEmpty(parameters.get(key)) ? null : Double.parseDouble(parameters.get(key).toString()) : null;
    }

    public String getString(String key) {
        return parameters.containsKey(key) ? StringUtils.isEmpty(parameters.get(key)) ? null : parameters.get(key).toString() : null;
    }

    public boolean containKey(String key) {
        return parameters.containsKey(key);
    }
}
