package org.atricore.idbus.capabilities.sts.main;

import org.atricore.idbus.kernel.main.authn.SecurityToken;

/**
 * Store MUST remove tokens upon expiration
 *
 * Created by sgonzalez.
 */
public interface TokenStore {

    void init();

    void shutdown();

    SecurityToken retrieve(String tokenId);

    void store(SecurityToken token);

}
