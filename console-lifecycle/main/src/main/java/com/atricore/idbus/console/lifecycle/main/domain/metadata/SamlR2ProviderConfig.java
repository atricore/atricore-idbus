package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2ProviderConfig extends AbstractProviderConfig {

    private Keystore signer;

    private Keystore encrypter;
    private static final long serialVersionUID = 8401310209898123598L;

    public Keystore getSigner() {
        return signer;
    }

    public void setSigner(Keystore signer) {
        this.signer = signer;
    }

    public Keystore getEncrypter() {
        return encrypter;
    }

    public void setEncrypter(Keystore encrypter) {
        this.encrypter = encrypter;
    }
}
