package com.atricore.idbus.console.activation.main.spi;

import com.atricore.idbus.console.activation.main.exception.ActivationException;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface Activator {

    void doActivate() throws ActivationException;
}
