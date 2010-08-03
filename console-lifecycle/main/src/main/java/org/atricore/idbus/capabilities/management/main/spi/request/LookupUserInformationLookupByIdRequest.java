package org.atricore.idbus.capabilities.management.main.spi.request;

/**
 * Author: Dejan Maric
 */
public class LookupUserInformationLookupByIdRequest {

    private long userInformationLookupId;

    public long getUserInformationLookupId() {
        return userInformationLookupId;
    }

    public void setUserInformationLookupId(long userInformationLookupId) {
        this.userInformationLookupId = userInformationLookupId;
    }
}
