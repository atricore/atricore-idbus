package com.atricore.idbus.console.lifecycle.main.exception;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.ExecutionEnvironment;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ExecEnvAlreadyActivated extends IdentityServerException {

    public ExecEnvAlreadyActivated(ExecutionEnvironment execEnv) {
        super(execEnv.getName() + " execution environment already active");
    }
}
