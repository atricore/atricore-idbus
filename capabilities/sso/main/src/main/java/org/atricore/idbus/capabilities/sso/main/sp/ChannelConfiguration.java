package org.atricore.idbus.capabilities.sso.main.sp;

import java.io.Serializable;

/**
 * Created by sgonzalez.
 */
public class ChannelConfiguration implements Serializable {

    private String name;

    private String signatureAlgorithm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }
}
