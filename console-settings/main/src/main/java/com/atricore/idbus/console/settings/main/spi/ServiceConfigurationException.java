package com.atricore.idbus.console.settings.main.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ServiceConfigurationException extends Exception {

    public ServiceConfigurationException() {
        super();
    }

    public ServiceConfigurationException(String s) {
        super(s);
    }

    public ServiceConfigurationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceConfigurationException(Throwable throwable) {
        super(throwable);
    }
}
