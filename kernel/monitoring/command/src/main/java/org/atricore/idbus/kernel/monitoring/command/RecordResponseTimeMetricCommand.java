package org.atricore.idbus.kernel.monitoring.command;


import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;

@Command(scope = "monitoring", name = "record-response-time-metric", description = "Record Response Time Metric")
@Service
public class RecordResponseTimeMetricCommand implements Action {


    @Reference
    private MonitoringServer monitoringServer;

    @Option(name = "-n", aliases = "--name", description = "Name", required = true, multiValued = false)
    String name;

    @Option(name = "-m", aliases = "--millis", description = "Milliseconds", required = true, multiValued = false)
    String millis;

    @Override
    public Object execute() throws Exception {
        
        monitoringServer.recordResponseTimeMetric(name, Long.valueOf(millis));

        return null;
    }

    public MonitoringServer getMonitoringServer() {
        return monitoringServer;
    }

    public void setMonitoringServer(MonitoringServer monitoringServer) {
        this.monitoringServer = monitoringServer;
    }


}
