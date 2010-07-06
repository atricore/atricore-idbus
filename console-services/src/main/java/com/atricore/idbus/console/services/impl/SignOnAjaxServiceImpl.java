package com.atricore.idbus.console.services.impl;

import org.atricore.idbus.capabilities.management.main.exception.SignOnServiceException;
import org.atricore.idbus.capabilities.management.main.spi.SignOnService;
import org.atricore.idbus.capabilities.management.main.spi.request.SignOnRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.SignOutRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UserLoggedRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.SignOnResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.SignOutResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UserLoggedResponse;
import com.atricore.idbus.console.services.spi.SignOnAjaxService;

/**
 * Author: Dejan Maric
 */
public class SignOnAjaxServiceImpl implements SignOnAjaxService {

    private SignOnService signOnService;

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnServiceException {
        return signOnService.signOn(signOnRequest);
    }

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnServiceException {
        return signOnService.signOut(signOutRequest);
    }

    public UserLoggedResponse userLogged(UserLoggedRequest userLoggedRequest) throws SignOnServiceException {
        return signOnService.userLogged(userLoggedRequest);
    }

    public void setSignOnService(SignOnService signOnService) {
        this.signOnService = signOnService;
    }
}
