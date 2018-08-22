package org.atricore.idbus.kernel.monitoring.builtin;

import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.atricore.idbus.kernel.monitoring.core.Request;
import org.atricore.idbus.kernel.monitoring.core.Response;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.Map;

@Service
public class BuiltinMonitoringServer implements MonitoringServer {

    public void recordMetric(String name, float value) {

    }

    public void recordResponseTimeMetric(String name, long millis) {

    }

    public void incrementCounter(String name) {

    }

    public void incrementCounter(String name, int count) {

    }

    public void noticeError(Throwable throwable, Map<String, String> params) {

    }

    public void noticeError(Throwable throwable) {

    }

    public void noticeError(String message, Map<String, String> params) {

    }

    public void noticeError(String message) {

    }

    public void addCustomParameter(String key, Number value) {

    }

    public void addCustomParameter(String key, String value) {

    }

    public void setTransactionName(String category, String name) {

    }

    public void ignoreTransaction() {
    }

    public void ignoreApdex() {
    }

    public void setRequestAndResponse(Request request, Response response) {
    }

    public void setUserName(String name) {
    }

    public void setAccountName(String name) {
    }

    public void setProductName(String name) {
    }
}
