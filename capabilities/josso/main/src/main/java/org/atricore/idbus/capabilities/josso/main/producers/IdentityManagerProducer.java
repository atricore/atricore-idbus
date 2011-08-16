/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.josso.main.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoAuthenticationAssertion;
import org.atricore.idbus.capabilities.josso.main.JossoAuthnContext;
import org.atricore.idbus.capabilities.josso.main.JossoMediator;
import org.atricore.idbus.capabilities.josso.main.binding.JossoBinding;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.josso.gateway.ws._1_2.protocol.*;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityManagerProducer extends AbstractJossoProducer {

    private static final Log logger = LogFactory.getLog(IdentityManagerProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public IdentityManagerProducer(Endpoint endpoint) {
        super(endpoint);
    }

    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        Object request = in.getMessage().getContent();
        Object response = null;

        EndpointDescriptor destination = new EndpointDescriptorImpl("SSOIdentityProviderService",
                "SSOIdentityProviderService",
                JossoBinding.JOSSO_SOAP.getValue(),
                null, null);

        if (logger.isDebugEnabled())
            logger.debug("Processing Identity Manager request : " + request);

        if (request instanceof FindUserInSessionRequestType) {

            FindUserInSessionRequestType r = (FindUserInSessionRequestType) request;
            response = findUserInSession(in.getMessage().getState(), r);
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    response, "FindUserInSessionResponse", null, destination, in.getMessage().getState()));

        } else if (request instanceof FindRolesBySSOSessionIdRequestType) {

            FindRolesBySSOSessionIdRequestType r = (FindRolesBySSOSessionIdRequestType) request;
            response = findRolesBySSOSessionIdRequest(in.getMessage().getState(), r);
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    response, "FindRolesBySSOSessionIdResponse", null, destination, in.getMessage().getState()));

        } else if (request instanceof UserExistsRequestType) {
            response = userExists(in, (UserExistsRequestType)request);
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    response, "UserExistsResponse", null, destination, in.getMessage().getState()));

        } else {
            throw new UnsupportedOperationException("Unknown request type " + request);
        }

        exchange.setOut(out);
    }

    protected FindUserInSessionResponseType findUserInSession(MediationState state, FindUserInSessionRequestType request) {

        String ssoSessionId = request.getSsoSessionId();
        if (logger.isDebugEnabled())
            logger.debug("Find user in requester/session " + request.getRequester()+ "/" + ssoSessionId);

        JossoAuthnContext authnCtx = (JossoAuthnContext) state.getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx");
        JossoAuthenticationAssertion aa = authnCtx != null ? authnCtx.getAuthnAssertion() : null;
        if (aa == null) {
            logger.error("No Authentication Assertion found for requester/session " + request.getRequester()+ "/" + ssoSessionId);
            throw new RuntimeException("No Authentication Assertion found for requester/session " + request.getRequester()+ "/" + ssoSessionId);
        }

        Subject subject = aa.getSubject();

        if (logger.isTraceEnabled())
            logger.trace("Found subject " + subject);

        // Create a SSO User based on the received Subject
        SSOUser user = toSSOUser(subject);
        FindUserInSessionResponseType response = new FindUserInSessionResponseType ();
        response.setSSOUser(toSSOUserType(user));

        return response;


    }

    protected FindRolesBySSOSessionIdResponseType findRolesBySSOSessionIdRequest(MediationState state, FindRolesBySSOSessionIdRequestType request) {
        String ssoSessionId = request.getSsoSessionId();
        if (logger.isDebugEnabled())
            logger.debug("Find user in session " + ssoSessionId);

        try {
            JossoAuthenticationAssertion aa =
                    ((JossoAuthnContext) state.getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx")).getAuthnAssertion();
            
            Subject subject = aa.getSubject();
            Collection<SSORole> roles = toSSORoles(subject);

            FindRolesBySSOSessionIdResponseType response = new FindRolesBySSOSessionIdResponseType ();

            for (SSORoleType role : adaptRoles(roles.toArray(new SSORole[roles.size()]))) {
                response.getRoles().add(role);
            }

            return response;

        } catch (Exception e) {
            // TODO !
            throw new RuntimeException(e);
        }

    }

    /**
     * Only for backward compatibility
     *
     */
    @Deprecated
    protected UserExistsResponseType userExists(CamelMediationMessage in, UserExistsRequestType request) throws SSOIdentityException {

        JossoMediator mediator = (JossoMediator) channel.getIdentityMediator();

        SSOIdentityManager im = mediator.getIdentityManager();

        UserExistsResponseType res = new UserExistsResponseType();
        try {
            im.userExists(request.getUsername());
            res.setUserexists(true);
        } catch (NoSuchUserException e) {
            res.setUserexists(false);
        }

        return res;

    }


    protected SSOUserType toSSOUserType(SSOUser user) {

        SSOUserType userType = new SSOUserType();
        userType.setName(user.getName());
        userType.setSecuritydomain(null);

        // Properties
        SSONameValuePairType [] nvpts = adaptNameValuePairs(user.getProperties());

        for (SSONameValuePairType nvpt : nvpts) {
            userType.getProperties().add(nvpt);
        }



        return userType;

    }

    protected SSONameValuePairType[] adaptNameValuePairs(SSONameValuePair[] nvps) {
        SSONameValuePairType [] nvpts = new SSONameValuePairType [nvps.length];
        for (int i = 0; i < nvps.length; i++) {
            SSONameValuePair nvp = nvps[i];
            SSONameValuePairType nvpt = adaptNameValuePair(nvp);
            nvpts[i] = nvpt;
        }
        return nvpts;

    }

    protected SSONameValuePairType adaptNameValuePair(SSONameValuePair nvp) {
        SSONameValuePairType nvpt = new SSONameValuePairType();
        nvpt.setName(nvp.getName());
        nvpt.setValue(nvp.getValue());

        return nvpt;
    }

    protected SSORoleType[] adaptRoles(SSORole[] roles) {
        SSORoleType [] roleTypes = new SSORoleType [roles.length];

        for (int i = 0; i < roles.length; i++) {
            SSORole role = roles[i];
            roleTypes[i] = adaptRole(role);
        }

        return roleTypes;

    }

    protected SSORoleType adaptRole(SSORole r) {
        SSORoleType rt = new SSORoleType();
        rt.setName(r.getName());

        return rt;
    }




}
