package org.atricore.idbus.kernel.monitoring.command;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;

@Command(scope = "monitoring", name = "record-response-time-metric", description = "Record Response Time Metric")
public class RecordResponseTimeMetricCommand extends OsgiCommandSupport {

    private MonitoringServer monitoringServer;

    @Option(name = "-n", aliases = "--name", description = "Name", required = true, multiValued = false)
    String name;

    @Option(name = "-m", aliases = "--millis", description = "Milliseconds", required = true, multiValued = false)
    String millis;

    @Override
    protected Object doExecute() throws Exception {
        
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
