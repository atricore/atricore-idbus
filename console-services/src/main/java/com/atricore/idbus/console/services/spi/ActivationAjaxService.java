package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.activation.main.exception.ActivationException;
import com.atricore.idbus.console.services.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.services.spi.request.ActivateSamplesRequest;
import com.atricore.idbus.console.services.spi.response.ActivateAgentResponse;
import com.atricore.idbus.console.services.spi.response.ActivateSamplesResponse;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ActivationAjaxService {

    ActivateAgentResponse activateAgent(ActivateAgentRequest request) throws ActivationException;

    ActivateSamplesResponse activateSamples(ActivateSamplesRequest request) throws ActivationException;

}
