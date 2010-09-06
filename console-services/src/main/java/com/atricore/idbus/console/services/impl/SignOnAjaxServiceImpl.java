package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.SignOnAjaxService;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.SignOutRequest;
import com.atricore.idbus.console.services.spi.request.UserLoggedRequest;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;
import com.atricore.idbus.console.services.spi.response.SignOutResponse;
import com.atricore.idbus.console.services.spi.response.UserLoggedResponse;
import com.atricore.idbus.console.lifecycle.main.exception.SignOnServiceException;
import org.dozer.DozerBeanMapper;

/**
 * Author: Dejan Maric
 */
public class SignOnAjaxServiceImpl implements SignOnAjaxService {


    private DozerBeanMapper dozerMapper;

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnServiceException {
        throw new UnsupportedOperationException("Use new SPML Service !");
    }

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnServiceException {
        throw new UnsupportedOperationException("Use new SPML Service !");
    }

    public UserLoggedResponse userLogged(UserLoggedRequest userLoggedRequest) throws SignOnServiceException {
        throw new UnsupportedOperationException("Use new SPML Service !");
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
}
