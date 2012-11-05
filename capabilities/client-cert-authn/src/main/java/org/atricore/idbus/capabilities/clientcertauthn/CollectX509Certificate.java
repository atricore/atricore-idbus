package org.atricore.idbus.capabilities.clientcertauthn;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 10/24/12
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectX509Certificate implements ClientCertAuthnMessage {

    private String endpoint;

    public CollectX509Certificate(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
