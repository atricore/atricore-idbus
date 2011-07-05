package com.atricore.idbus.console.activation.main.client;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ActivationClientFactory {

    ActivationClient newActivationClient(String location);

    ActivationClient newActivationClient(String location, String servicePath);

}
