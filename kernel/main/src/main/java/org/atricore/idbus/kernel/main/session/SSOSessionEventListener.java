package org.atricore.idbus.kernel.main.session;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface SSOSessionEventListener {

    void handleEvent(String type, SSOSession session, Object data);
}
