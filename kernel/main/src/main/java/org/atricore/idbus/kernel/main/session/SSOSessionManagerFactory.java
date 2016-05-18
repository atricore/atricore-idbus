package org.atricore.idbus.kernel.main.session;

/**
 * SSO Session Manager factory to create new SSO Session manager instances.
 */
public interface SSOSessionManagerFactory {

    String getName();

    String getDescription();

    SSOSessionManager getInstance();

}
