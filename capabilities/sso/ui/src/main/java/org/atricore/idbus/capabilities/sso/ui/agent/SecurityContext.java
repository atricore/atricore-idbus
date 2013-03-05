package org.atricore.idbus.capabilities.sso.ui.agent;

import java.util.Collection;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/28/13
 */
public interface SecurityContext {

    String getPrincipal();

    boolean isUserInRole(String role);

    Collection<String> getRoles();

    boolean isSessionValid();

}
