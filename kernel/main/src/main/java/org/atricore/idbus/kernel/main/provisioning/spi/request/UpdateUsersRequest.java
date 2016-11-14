package org.atricore.idbus.kernel.main.provisioning.spi.request;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.util.List;

/**
 *
 */
public class UpdateUsersRequest extends AbstractProvisioningRequest {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
