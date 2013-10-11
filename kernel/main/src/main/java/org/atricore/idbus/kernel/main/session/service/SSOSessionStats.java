package org.atricore.idbus.kernel.main.session.service;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SSOSessionStats {

    public long getMaxSessions();

    public long getCreatedSessions();

    public long getDestroyedSessions();

    public long getCurrentSessions();
}
