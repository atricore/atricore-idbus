package org.atricore.idbus.kernel.common.support.pki;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 11/14/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidTrustStoreLocation extends PKIException {

    public InvalidTrustStoreLocation(String location) {
        super("Invalid Key Store location : " + location);
    }
}
