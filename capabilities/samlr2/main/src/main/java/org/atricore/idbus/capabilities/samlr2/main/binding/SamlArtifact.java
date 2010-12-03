package org.atricore.idbus.capabilities.samlr2.main.binding;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlArtifact implements java.io.Serializable {

    private int type;

    private int endpointIndex;

    private String sourceID;

    private String messageHandle;

    public SamlArtifact(int type, int endpointIndex, String sourceID, String messageHandle) {
        this.type = type;
        this.endpointIndex = endpointIndex;
        this.sourceID = sourceID;
        this.messageHandle = messageHandle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEndpointIndex() {
        return endpointIndex;
    }

    public void setEndpointIndex(int endpointIndex) {
        this.endpointIndex = endpointIndex;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getMessageHandle() {
        return messageHandle;
    }

    public void setMessageHandle(String messageHandle) {
        this.messageHandle = messageHandle;
    }

    @Override
    public String toString() {
        return type + ":" + endpointIndex + ":" + sourceID + ":" + messageHandle;
    }
}
