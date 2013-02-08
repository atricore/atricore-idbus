package org.atricore.idbus.kernel.monitoring.core;

import java.util.HashMap;
import java.util.Map;

public class ResponseImpl implements Response {

    private int status;
    private String statusMessage;
    private Map<String,String> headers = new HashMap<String,String>();

    public ResponseImpl(int status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public int getStatus() throws Exception {
        return status;
    }

    public String getStatusMessage() throws Exception {
        return statusMessage;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
}
