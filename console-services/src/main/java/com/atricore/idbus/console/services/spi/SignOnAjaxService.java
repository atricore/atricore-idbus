package com.atricore.idbus.console.services.spi;

import org.atricore.idbus.capabilities.management.main.exception.SignOnServiceException;
import org.atricore.idbus.capabilities.management.main.spi.request.SignOnRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.SignOutRequest;
import org.atricore.idbus.capabilities.management.main.spi.request.UserLoggedRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.SignOnResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.SignOutResponse;
import org.atricore.idbus.capabilities.management.main.spi.response.UserLoggedResponse;

/**
 * Author: Dejan Maric
 */
public interface SignOnAjaxService {

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnServiceException;

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnServiceException;

    UserLoggedResponse userLogged(UserLoggedRequest userLoggedRequest) throws SignOnServiceException;
}
