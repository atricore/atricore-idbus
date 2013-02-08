package org.atricore.idbus.kernel.monitoring.core;

import java.util.Map;

public interface MonitoringServer {

    void recordMetric(String name, float value);

    void recordResponseTimeMetric(String name, long millis);

    void incrementCounter(String name);

    void incrementCounter(String name, int count);

    void noticeError(Throwable throwable, Map<String, String> params);

    void noticeError(Throwable throwable);

    void noticeError(String message, Map<String, String> params);

    void noticeError(String message);

    void addCustomParameter(String key, Number value);

    void addCustomParameter(String key, String value);

    void setTransactionName(String category, String name);

    void ignoreTransaction();

    void ignoreApdex();

    void setRequestAndResponse(Request request, Response response);

    void setUserName(String name);

    void setAccountName(String name);

    void setProductName(String name);
}
