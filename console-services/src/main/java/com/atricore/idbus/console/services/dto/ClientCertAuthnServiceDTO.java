package com.atricore.idbus.console.services.dto;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 11/1/12
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientCertAuthnServiceDTO extends AuthenticationServiceDTO {

    // This tells how to get user identifier from the certificate
    private String uid;

    // URL retrieve CRL resources
    private String crlUrl;

    // TODO : Not supported by JDK .. ?! check this.
    /*
    private String[] enabledValidators; // OCSP, CLR

    private String ocspServer;
     */

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCrlUrl() {
        return crlUrl;
    }

    public void setCrlUrl(String crlUrl) {
        this.crlUrl = crlUrl;
    }

}
