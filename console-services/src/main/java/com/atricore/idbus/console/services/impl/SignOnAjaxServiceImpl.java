package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.lifecycle.main.exception.SignOnException;
import com.atricore.idbus.console.services.dto.UserDTO;
import com.atricore.idbus.console.services.spi.AjaxService;
import com.atricore.idbus.console.services.spi.SignOnAjaxService;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.FindUserByUsernameRequest;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.SignOutRequest;
import com.atricore.idbus.console.services.spi.response.FindUserByUsernameResponse;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;
import com.atricore.idbus.console.services.spi.response.SignOutResponse;
import oasis.names.tc.spml._2._0.PSOIdentifierType;
import oasis.names.tc.spml._2._0.StatusCodeType;
import oasis.names.tc.spml._2._0.password.ValidatePasswordRequestType;
import oasis.names.tc.spml._2._0.password.ValidatePasswordResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * Author: Dusan Fisic
 */
public class SignOnAjaxServiceImpl implements SignOnAjaxService, AjaxService {
    private static Log logger = LogFactory.getLog(SignOnAjaxServiceImpl.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();
    private UserProvisioningAjaxService usrProvService;

    private SpmlR2Client spmlService;
    private String pspTargetId;

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnException {
        FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
        userRequest.setUsername(signOnRequest.getUsername());

        try{
            SignOnResponse response = new SignOnResponse();
            FindUserByUsernameResponse finsResp = usrProvService.findUserByUsername(userRequest);
            UserDTO retUser = finsResp.getUser();
            if (retUser != null) {
                PSOIdentifierType psoUserId = new PSOIdentifierType();
                psoUserId.setTargetID(pspTargetId);
                psoUserId.setID(retUser.getId() + "");
                psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

                ValidatePasswordRequestType validatePass = new ValidatePasswordRequestType();
                validatePass.setRequestID(uuidGenerator.generateId());
                validatePass.setPsoID(psoUserId);
                validatePass.setPassword(signOnRequest.getPassword());

                ValidatePasswordResponseType resp = spmlService.spmlValidatePasswordRequest(validatePass);
                if (resp.getStatus() == StatusCodeType.SUCCESS)
                    response.setAuthenticatedUser(retUser);
            }
            if (retUser == null && logger.isTraceEnabled())
                logger.trace("Unknown user with username: " + signOnRequest.getUsername() );

            return response;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SignOnException("Wrong credentials for user : " + userRequest.getUsername() + " : " + e.getMessage(), e);
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

    public String getPspTargetId() {
        return pspTargetId;
    }

    public void setPspTargetId(String pspTargetId) {
        this.pspTargetId = pspTargetId;
    }

    public SpmlR2Client getSpmlService() {
        return spmlService;
    }

    public void setSpmlService(SpmlR2Client spmlService) {
        this.spmlService = spmlService;
    }



}
