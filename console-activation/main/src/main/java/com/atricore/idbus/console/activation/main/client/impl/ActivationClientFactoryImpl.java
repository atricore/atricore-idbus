package com.atricore.idbus.console.activation.main.client.impl;

import com.atricore.idbus.console.activation.main.client.ActivationClient;
import com.atricore.idbus.console.activation.main.client.ActivationClientFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivationClientFactoryImpl implements ActivationClientFactory {

    public ActivationClient newActivationClient(String location) {
        return new ActivationClientImpl(location);
    }

    public ActivationClient newActivationClient(String location, String servicePath) {
        return new ActivationClientImpl(location, servicePath);
    }
}
