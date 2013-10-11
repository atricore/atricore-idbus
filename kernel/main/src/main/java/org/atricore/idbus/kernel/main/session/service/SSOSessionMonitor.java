package org.atricore.idbus.kernel.main.session.service;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SSOSessionMonitor {

    void start();

    void stop();

    void setInterval(long millis);
}
