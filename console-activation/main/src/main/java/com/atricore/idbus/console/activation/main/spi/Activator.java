package com.atricore.idbus.console.activation.main.spi;

import com.atricore.idbus.console.activation.main.exception.ActivationException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface Activator {

    void doActivate() throws ActivationException;
}
