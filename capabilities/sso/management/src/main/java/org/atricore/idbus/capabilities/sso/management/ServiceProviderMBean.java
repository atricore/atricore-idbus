package org.atricore.idbus.capabilities.sso.management;

import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.management.openmbean.TabularData;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface ServiceProviderMBean extends ProviderMBean {

    TabularData listSessionsAsTable();

    TabularData listUserSessionsAsTable(String username);

    SSOSession[] listSessions();

    SSOSession[] listUserSessions(String username);

}
