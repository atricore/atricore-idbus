package com.atricore.idbus.console.activation.main.spi;

import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest;
import com.atricore.idbus.console.activation.main.spi.request.ConfigureAgentRequest;
import com.atricore.idbus.console.activation.main.spi.response.ActivateAgentResponse;
import com.atricore.idbus.console.activation.main.spi.response.ActivateSamplesResponse;
import com.atricore.idbus.console.activation.main.spi.response.ConfigureAgentResponse;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ActivationService {

    ActivateAgentResponse activateAgent(ActivateAgentRequest request) throws ActivationException;

    ConfigureAgentResponse configureAgent(ConfigureAgentRequest request) throws ActivationException;

    ActivateSamplesResponse activateSamples(ActivateSamplesRequest request) throws ActivationException;
    
}
