package org.ops4j.pax.web.service.jetty.wadi;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.catalina.tribes.membership.StaticMember;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.wadi.group.Dispatcher;
import org.codehaus.wadi.tribes.TribesDispatcher;
import org.codehaus.wadi.web.impl.URIEndPoint;
import org.mortbay.jetty.servlet.wadi.WadiCluster;

/**
 * WADI Cluster extension that allows Tribes transport configuration.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class TribesWadiCluster extends WadiCluster {

    private static final Log LOG = LogFactory.getLog(TribesWadiCluster.class);

    protected String clusterName;
    protected String nodeName;
    protected URI endPointURI;

    private Integer receivingPort;
    private boolean disableMulticast;

    private Integer mcastPort;
    private String mcastAddress;
    private Integer memberDropTime;
    private Integer mcastFrequency;
    private Integer tcpListenPort;
    private String tcpListenHost;
    private String bindAddress;


    // TODO : Declare new properties if needed
    private Properties multicastProperties = new Properties();

    private final Collection<StaticMember> _staticMembers;

    public TribesWadiCluster(String clusterName,
                             String nodeName,
                             String endPointURI) throws Exception {
        super(clusterName, nodeName, endPointURI);
        this.clusterName = clusterName;
        this.nodeName = nodeName;
        this.endPointURI = new URI(endPointURI);

        receivingPort = 4000;
        disableMulticast = false;

        _staticMembers = new ArrayList<StaticMember>();

        if (LOG.isDebugEnabled())
            LOG.debug("Created Cluster Node: " + clusterName + ":" + nodeName);
    }

    public void addStaticMember(StaticMember member)
    {
        _staticMembers.add(member);
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public URI getEndPointURI() {
        return endPointURI;
    }

    public void setEndPointURI(URI endPointURI) {
        this.endPointURI = endPointURI;
    }

    public int getReceivingPort() {
        return receivingPort;
    }

    public void setReceivingPort(int receivingPort) {
        this.receivingPort = receivingPort;
    }

    public boolean isDisableMulticast() {
        return disableMulticast;
    }

    public void setDisableMulticast(boolean disableMulticast) {
        this.disableMulticast = disableMulticast;
    }

    public Properties getMulticastProperties() {
        return multicastProperties;
    }

    public void setMulticastProperties(Properties multicastProperties) {
        this.multicastProperties = multicastProperties;
    }

    public Integer getMcastPort() {
        return mcastPort;
    }

    public void setMcastPort(Integer mcastPort) {
        this.mcastPort = mcastPort;
    }

    public String getMcastAddress() {
        return mcastAddress;
    }

    public void setMcastAddress(String mcastAddress) {
        this.mcastAddress = mcastAddress;
    }

    public Integer getMemberDropTime() {
        return memberDropTime;
    }

    public void setMemberDropTime(Integer memberDropTime) {
        this.memberDropTime = memberDropTime;
    }

    public Integer getMcastFrequency() {
        return mcastFrequency;
    }

    public void setMcastFrequency(Integer mcastFrequency) {
        this.mcastFrequency = mcastFrequency;
    }

    public Integer getTcpListenPort() {
        return tcpListenPort;
    }

    public void setTcpListenPort(Integer tcpListenPort) {
        this.tcpListenPort = tcpListenPort;
    }

    public String getTcpListenHost() {
        return tcpListenHost;
    }

    public void setTcpListenHost(String tcpListenHost) {
        this.tcpListenHost = tcpListenHost;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    protected Dispatcher newDispatcher() {

        if (mcastPort != null)
            multicastProperties.setProperty("mcastPort", mcastPort.toString());

        if (LOG.isDebugEnabled())
            LOG.debug("Using 'mcastPort' " + mcastPort);

        if (mcastAddress != null)
            multicastProperties.setProperty("mcastAddress", mcastAddress);

        if (LOG.isDebugEnabled())
            LOG.debug("Using 'mcastAddress' " + mcastAddress);

        if (mcastFrequency != null )
            multicastProperties.setProperty("mcastFrequency", mcastFrequency.toString());

        if (LOG.isDebugEnabled())
            LOG.debug("Using 'mcastFrequency' " + mcastFrequency);

        if (memberDropTime != null)
            multicastProperties.setProperty("memberDropTime", memberDropTime.toString());

        if (LOG.isDebugEnabled())
            LOG.debug("Using 'memberDropTime' " + memberDropTime);

        if (bindAddress != null)
            multicastProperties.setProperty("bindAddress", bindAddress);

        if (LOG.isDebugEnabled())
            LOG.debug("Using 'bindAddress' " + bindAddress);

        return new TribesDispatcher(clusterName, nodeName, new URIEndPoint(endPointURI), _staticMembers,
                disableMulticast, multicastProperties, receivingPort);
    }

}
