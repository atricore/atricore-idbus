package org.atricore.idbus.capabilities.management.main.spi.request;

/**
 * Author: Dejan Maric
 */
public class ListIdentityAppliancesRequest extends AbstractManagementRequest {

    boolean startedOnly;
    boolean projectedOnly;

    int fetchDepth;

    public ListIdentityAppliancesRequest() {
        this.startedOnly = false;
        this.projectedOnly = false;
        this.fetchDepth = 3;
    }

    public boolean isStartedOnly() {
        return startedOnly;
    }

    public void setStartedOnly(boolean startedOnly) {
        this.startedOnly = startedOnly;
    }

    public boolean isProjectedOnly() {
        return projectedOnly;
    }

    public void setProjectedOnly(boolean projectedOnly) {
        this.projectedOnly = projectedOnly;
    }

    public int getFetchDepth() {
        return fetchDepth;
    }

    public void setFetchDepth(int fetchDepth) {
        if(fetchDepth < 0){
            this.fetchDepth = 1;
        } else if(fetchDepth == 0) {
            this.fetchDepth = 3; //workaround to set default value. after the constructor, flex calls setFetchDepth(0) when fetchDepth is not initialized in flex.
        }
        else {
            this.fetchDepth = fetchDepth;   
        }
    }
}
