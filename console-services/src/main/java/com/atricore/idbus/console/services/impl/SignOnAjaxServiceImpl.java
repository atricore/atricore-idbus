package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.SignOnAjaxService;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.SignOutRequest;
import com.atricore.idbus.console.services.spi.request.UserLoggedRequest;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;
import com.atricore.idbus.console.services.spi.response.SignOutResponse;
import com.atricore.idbus.console.services.spi.response.UserLoggedResponse;
import com.atricore.idbus.console.lifecycle.main.exception.SignOnServiceException;
import com.atricore.idbus.console.lifecycle.main.spi.SignOnService;
import org.dozer.DozerBeanMapper;

/**
 * Author: Dejan Maric
 */
public class SignOnAjaxServiceImpl implements SignOnAjaxService {

    private SignOnService signOnService;
    private DozerBeanMapper dozerMapper;

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnServiceException {
        com.atricore.idbus.console.lifecycle.main.spi.request.SignOnRequest beReq =
                dozerMapper.map(signOnRequest, com.atricore.idbus.console.lifecycle.main.spi.request.SignOnRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.SignOnResponse beRes = signOnService.signOn(beReq);
        return dozerMapper.map(beRes, SignOnResponse.class);
    }

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnServiceException {
        com.atricore.idbus.console.lifecycle.main.spi.request.SignOutRequest beReq =
                dozerMapper.map(signOutRequest, com.atricore.idbus.console.lifecycle.main.spi.request.SignOutRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.SignOutResponse beRes = signOnService.signOut(beReq);
        return dozerMapper.map(beRes, SignOutResponse.class);
    }

    public UserLoggedResponse userLogged(UserLoggedRequest userLoggedRequest) throws SignOnServiceException {
        com.atricore.idbus.console.lifecycle.main.spi.request.UserLoggedRequest beReq =
                dozerMapper.map(userLoggedRequest, com.atricore.idbus.console.lifecycle.main.spi.request.UserLoggedRequest.class);

        com.atricore.idbus.console.lifecycle.main.spi.response.UserLoggedResponse beRes = signOnService.userLogged(beReq);
        return dozerMapper.map(beRes, UserLoggedResponse.class);
    }

    public void setSignOnService(SignOnService signOnService) {
        this.signOnService = signOnService;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
}
