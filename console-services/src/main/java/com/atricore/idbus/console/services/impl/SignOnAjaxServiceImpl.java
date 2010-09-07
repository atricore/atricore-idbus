package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.lifecycle.main.exception.SignOnException;
import com.atricore.idbus.console.services.spi.SignOnAjaxService;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.SignOutRequest;
import com.atricore.idbus.console.services.spi.request.UserLoggedRequest;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;
import com.atricore.idbus.console.services.spi.response.SignOutResponse;
import com.atricore.idbus.console.services.spi.response.UserLoggedResponse;
import org.dozer.DozerBeanMapper;

/**
 * Author: Dejan Maric
 */
public class SignOnAjaxServiceImpl implements SignOnAjaxService {


    private DozerBeanMapper dozerMapper;

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnException {
        // TODO: lookup user validating credentials and create session
        throw new UnsupportedOperationException("Use new SPML Service !");
    }

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnException {
        // TODO: dispose session
        throw new UnsupportedOperationException("Use new SPML Service !");
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
}
