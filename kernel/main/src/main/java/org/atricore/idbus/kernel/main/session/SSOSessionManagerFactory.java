package org.atricore.idbus.kernel.main.session;

import org.atricore.idbus.kernel.main.session.service.SSOSessionManagerImpl;

/**
 * SSO Session Manager factory to create new SSO Session manager instances.
 */
public interface SSOSessionManagerFactory {

    String getName();

    String getDescription();

    SSOSessionManagerImpl getInstance();

}
