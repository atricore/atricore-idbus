package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/8/13
 */
public class PrepareAddUserResponse extends AbstractProvisioningResponse {

    private String transactionId;

    private User user;

    private String password;

    public PrepareAddUserResponse(String id, User u, String password) {
        this.transactionId = id;
        this.user = u;
        this.password = password;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public User getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
