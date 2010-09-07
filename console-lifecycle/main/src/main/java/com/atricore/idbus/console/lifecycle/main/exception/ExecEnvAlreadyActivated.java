package com.atricore.idbus.console.lifecycle.main.exception;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.Activation;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ExecEnvAlreadyActivated extends IdentityServerException {

    public ExecEnvAlreadyActivated(Activation a) {
        super(a.getName() + " already active for " + a.getSp().getName());
    }
}
