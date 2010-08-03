package org.atricore.idbus.capabilities.samlr2.management;

import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.management.openmbean.TabularData;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface IdentityProviderMBean extends ProviderMBean {



    boolean invalidateSession(String sessionId);

    boolean invalidateAllSessions();

    boolean invalidateUserSessions(String username);

    TabularData listSessionsAsTable();

    TabularData listUserSessionsAsTable(String username);

    SSOSession[] listSessions();

    SSOSession[] listUserSessions(String username);

    long getMaxInactiveInterval();


}
