package org.atricore.idbus.kernel.monitoring.core;

public interface Response
{
    int getStatus() throws Exception;

    String getStatusMessage() throws Exception;

    void setHeader(String key, String value);
}
