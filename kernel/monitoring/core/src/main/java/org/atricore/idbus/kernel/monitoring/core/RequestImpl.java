package org.atricore.idbus.kernel.monitoring.core;

import java.util.*;

public class RequestImpl implements Request {
    private String requestURI;
    private Map<String,String> headers = new HashMap<String,String>();
    private String remoteUser;
    private Map<String,String> parameters = new HashMap<String,String>();
    private Map<String,Object> attributes = new HashMap<String,Object>();

    public RequestImpl(String requestURI, String remoteUser) {
        this.requestURI = requestURI;
        this.remoteUser = remoteUser;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }
    
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }
    
    public String getRequestURI() {
        return requestURI;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(parameters.keySet());
    }

    public Collection<String> getParameterValues(String key) {
        return Collections.unmodifiableCollection(parameters.values());
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
