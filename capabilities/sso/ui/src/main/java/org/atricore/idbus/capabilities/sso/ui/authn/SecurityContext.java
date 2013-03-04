package org.atricore.idbus.capabilities.sso.ui.authn;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/28/13
 */
public interface SecurityContext {

    String getPrincipal();

    boolean isUserInRole(String role);

    boolean isSessionValid();

}
