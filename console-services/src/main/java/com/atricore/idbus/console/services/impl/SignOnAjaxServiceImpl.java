package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.lifecycle.main.exception.SignOnException;
import com.atricore.idbus.console.services.dto.UserDTO;
import com.atricore.idbus.console.services.spi.SignOnAjaxService;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.FindUserByUsernameRequest;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.SignOutRequest;
import com.atricore.idbus.console.services.spi.response.FindUserByUsernameResponse;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;
import com.atricore.idbus.console.services.spi.response.SignOutResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Author: Dusan Fisic
 */
public class SignOnAjaxServiceImpl implements SignOnAjaxService {
    private static Log logger = LogFactory.getLog(SignOnAjaxServiceImpl.class);

    private UserProvisioningAjaxService usrProvService;

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnException {
        FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
        userRequest.setUsername(signOnRequest.getUsername());

        try{
            SignOnResponse response = new SignOnResponse();
            FindUserByUsernameResponse resp = usrProvService.findUserByUsername(userRequest);
            UserDTO retUser = resp.getUser();
            if (retUser != null &&
                    retUser.getUserPassword().equals(signOnRequest.getPassword())) {
                response.setAuthenticatedUser(retUser);
            }
            if (retUser == null && logger.isTraceEnabled())
                 logger.trace("Unknown user with username: " + signOnRequest.getUsername() );

            return response;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SignOnException("Error finding user with username: " + userRequest.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnException {
        FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
        try{
            SignOutResponse response = new SignOutResponse();
           
            return response;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SignOnException("Error finding user with username: " + userRequest.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public UserProvisioningAjaxService getUsrProvService() {
        return usrProvService;
    }

    public void setUsrProvService(UserProvisioningAjaxService usrProvService) {
        this.usrProvService = usrProvService;
    }


}
