package org.atricore.idbus.kernel.main.provisioning.spi.request;

/**
 * Created by sgonzalez on 5/5/15.
 */
public class ListUserAccountsRequest extends AbstractProvisioningRequest {

    //  Optional,
    private String resourceOid;

    private String userOid;

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }

    public String getResourceOid() {
        return resourceOid;
    }

    public void setResourceOid(String resourceOid) {
        this.resourceOid = resourceOid;
    }
}
