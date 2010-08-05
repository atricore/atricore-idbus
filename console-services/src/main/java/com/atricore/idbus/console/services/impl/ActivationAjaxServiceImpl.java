package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.ActivationAjaxService;
import com.atricore.idbus.console.services.spi.request.ActivateAgentRequest;
import com.atricore.idbus.console.services.spi.request.ActivateSamplesRequest;
import com.atricore.idbus.console.services.spi.response.ActivateAgentResponse;
import com.atricore.idbus.console.services.spi.response.ActivateSamplesResponse;

import com.atricore.idbus.console.activation.main.spi.ActivationService;

import org.dozer.DozerBeanMapper;

import java.rmi.activation.ActivationException;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivationAjaxServiceImpl implements ActivationAjaxService {

    private ActivationService activationService;
    private DozerBeanMapper dozerMapper;

    public ActivateAgentResponse activateAgent(ActivateAgentRequest request) throws com.atricore.idbus.console.activation.main.exception.ActivationException {
        com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest beReq =
                dozerMapper.map(request, com.atricore.idbus.console.activation.main.spi.request.ActivateAgentRequest.class);

        com.atricore.idbus.console.activation.main.spi.response.ActivateAgentResponse beRes = activationService.activateAgent(beReq);
        return dozerMapper.map(beRes, ActivateAgentResponse.class);

    }

    public ActivateSamplesResponse activateSamples(ActivateSamplesRequest request) throws com.atricore.idbus.console.activation.main.exception.ActivationException {
        com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest beReq =
                dozerMapper.map(request, com.atricore.idbus.console.activation.main.spi.request.ActivateSamplesRequest.class);

        com.atricore.idbus.console.activation.main.spi.response.ActivateSamplesResponse beRes = activationService.activateSamples(beReq);
        return dozerMapper.map(beRes, ActivateSamplesResponse.class);

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
