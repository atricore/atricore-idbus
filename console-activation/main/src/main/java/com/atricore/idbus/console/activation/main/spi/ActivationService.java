package com.atricore.idbus.console.activation.main.spi;

import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest;
import com.atricore.idbus.console.activation.main.spi.request.ConfigureAgentRequest;
import com.atricore.idbus.console.activation.main.spi.response.ActivateAgentResponse;
import com.atricore.idbus.console.activation.main.spi.response.ConfigureAgentResponse;
import com.atricore.idbus.console.activation.main.spi.response.ActivateSamplesResponse;
import com.atricore.idbus.console.activation.main.spi.response.PlatformSupportedResponse;
import com.atricore.idbus.console.activation.main.spi.request.PlatformSupportedRequest;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ActivationService {

    PlatformSupportedResponse isSupported(PlatformSupportedRequest request) throws ActivationException;

    ActivateAgentResponse activateAgent(ActivateAgentRequest request) throws ActivationException;

    ConfigureAgentResponse configureAgent(ConfigureAgentRequest request) throws ActivationException;

    ActivateSamplesResponse activateSamples(ActivateSamplesRequest request) throws ActivationException;

}
