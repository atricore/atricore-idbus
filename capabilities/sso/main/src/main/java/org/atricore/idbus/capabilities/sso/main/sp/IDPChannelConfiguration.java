package org.atricore.idbus.capabilities.sso.main.sp;

import org.atricore.idbus.capabilities.sso.main.common.ChannelConfiguration;

import java.io.Serializable;

/**
 * Created by sgonzalez.
 */
public class IDPChannelConfiguration extends ChannelConfiguration {

    private String name;

    private String signatureHash;

    public IDPChannelConfiguration(String name) {
        super(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignatureHash() {
        return signatureHash;
    }

    public void setSignatureHash(String signatureHash) {
        this.signatureHash = signatureHash;
    }
}
