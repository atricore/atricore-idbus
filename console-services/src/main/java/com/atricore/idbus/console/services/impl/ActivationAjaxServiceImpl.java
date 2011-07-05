package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.activation.main.client.ActivationClientFactory;
import com.atricore.idbus.console.services.spi.ActivationAjaxService;
import com.atricore.idbus.console.services.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.services.spi.request.ActivateSamplesRequest;
import com.atricore.idbus.console.services.spi.response.ActivateAgentResponse;
import com.atricore.idbus.console.services.spi.response.ActivateSamplesResponse;

import com.atricore.idbus.console.activation.main.spi.ActivationService;

import org.dozer.DozerBeanMapper;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivationAjaxServiceImpl implements ActivationAjaxService {

    // Factory for remote activations!
    private ActivationClientFactory activationFactory;

    private ActivationService activationService;

    private DozerBeanMapper dozerMapper;


    public ActivateAgentResponse activateAgent(ActivateAgentRequest request) throws com.atricore.idbus.console.activation.main.exception.ActivationException {

        // TODO : For remote activations, the client must be used, sending username and password in the request!
        // TODO : For remote activations, agent config resources must be sent in a ConfigureAgentRequet, if necessary!

        com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest beReq =
                dozerMapper.map(request, com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest.class);

        com.atricore.idbus.console.activation.main.spi.response.ActivateAgentResponse beRes = activationService.activateAgent(beReq);
        return dozerMapper.map(beRes, ActivateAgentResponse.class);

    }

    public ActivateSamplesResponse activateSamples(ActivateSamplesRequest request) throws com.atricore.idbus.console.activation.main.exception.ActivationException {

        // TODO : For remote activations, the client must be used, sending username and password in the request!

        com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest beReq =
                dozerMapper.map(request, com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest.class);

        com.atricore.idbus.console.activation.main.spi.response.ActivateSamplesResponse beRes = activationService.activateSamples(beReq);
        return dozerMapper.map(beRes, ActivateSamplesResponse.class);

    }

    public ActivationClientFactory getActivationFactory() {
        return activationFactory;
    }

    public void setActivationFactory(ActivationClientFactory activationFactory) {
        this.activationFactory = activationFactory;
    }

    public void setActivationService(ActivationService activationService) {
        this.activationService = activationService;
    }

    public ActivationService getActivationService() {
        return activationService;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }

    public DozerBeanMapper getDozerMapper() {
        return dozerMapper;
    }


}
