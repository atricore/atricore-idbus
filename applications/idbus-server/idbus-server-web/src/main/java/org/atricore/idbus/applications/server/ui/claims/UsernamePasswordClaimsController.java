/*
 * Atricore IDBus
 *
 * Copyright (c) 2011, Atricore Inc.
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

package org.atricore.idbus.applications.server.ui.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.applications.server.ui.util.DashboardMessage;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class UsernamePasswordClaimsController extends SimpleFormController {

    private static final Log logger = LogFactory.getLog(UsernamePasswordClaimsController.class);

    private UUIDGenerator idGenerator = new UUIDGenerator();

    private MessageQueueManager artifactQueueManager;

    private IdentityMediationUnitRegistry idauRegistry;

    private String fallbackUrl;

    @Override
    protected ModelAndView showForm(HttpServletRequest hreq, HttpServletResponse hres, BindException errors) throws Exception {

        if (fallbackUrl != null) {
            try {
                CollectUsernamePasswordClaims claims = (CollectUsernamePasswordClaims) hreq.getSession().getAttribute("CollectUsernamePasswordClaims");

                if (claims == null || claims.getClaimsRequest() == null)
                    return new ModelAndView(new RedirectView(fallbackUrl));

            } catch (Exception e) {
                return new ModelAndView(new RedirectView(fallbackUrl));
            }
        }

        return super.showForm(hreq, hres, errors);
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest hreq, HttpServletResponse hres, BindException errors, Map controlModel) throws Exception {

        // TODO : Get from configuration

        if (fallbackUrl != null) {
            try {
                CollectUsernamePasswordClaims claims = (CollectUsernamePasswordClaims) hreq.getSession().getAttribute("CollectUsernamePasswordClaims");

                if (claims == null || claims.getClaimsRequest() == null)
                    return new ModelAndView(new RedirectView(fallbackUrl));

            } catch (Exception e) {
                return new ModelAndView(new RedirectView(fallbackUrl));
            }
        }

        return super.showForm(hreq, hres, errors, controlModel);
    }

    @Override
    protected Object formBackingObject(HttpServletRequest hreq) throws Exception {

        String artifactId = hreq.getParameter(SsoHttpArtifactBinding.SSO_ARTIFACT_ID);
        CollectUsernamePasswordClaims collectClaims;

        // No request parameter , try to get the form from the session
        if (artifactId == null) {

            collectClaims = (CollectUsernamePasswordClaims) hreq.getSession().getAttribute("CollectUsernamePasswordClaims");
            // No collect claims in session, build an empty value and return.
            if (collectClaims == null)
                collectClaims = new CollectUsernamePasswordClaims();

            return collectClaims;
        }

        collectClaims = new CollectUsernamePasswordClaims();


        if (logger.isDebugEnabled())
            logger.debug("Creating form backing object for artifact " + artifactId);                

        // Lookup for ClaimsRequest!
        ClaimsRequest claimsRequest = (ClaimsRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));

        if (claimsRequest != null) {

            if (logger.isDebugEnabled())
                logger.debug("Received claims request " + claimsRequest.getId() +
                        " from " + claimsRequest.getIssuerChannel() +
                        " at " + claimsRequest.getIssuerEndpoint());

            if (claimsRequest.getLastErrorId() != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Received last error ID : " +
                        claimsRequest.getLastErrorId() +
                        " ("+claimsRequest.getLastErrorMsg()+")");

                hreq.setAttribute("statusMessageKey", "claims.text.invalidCredentials");
            }

            List<DashboardMessage> ssoPolicyMsgs = new ArrayList<DashboardMessage>();

            // Publish SSO Policies information to be displayed ...
            for (SSOPolicyEnforcementStatement ssoPolicyEnforcement : claimsRequest.getSsoPolicyEnforcements()) {
                List<Object> values = null;
                if (ssoPolicyEnforcement.getValues().size() > 0) {
                    values = new ArrayList<Object>();
                    values.addAll(ssoPolicyEnforcement.getValues());
                }
                ssoPolicyMsgs.add(new DashboardMessage("claims.text." + ssoPolicyEnforcement.getName(), values));

            }

            if (ssoPolicyMsgs.size() > 0)
                hreq.setAttribute("ssoPolicyMessages", ssoPolicyMsgs);



            collectClaims.setClaimsRequest(claimsRequest);

        } else {
            // TODO : redirect User to configured fallback URL
            logger.debug("No claims request received!");
        }

        hreq.getSession().setAttribute("CollectUsernamePasswordClaims", collectClaims);

        return collectClaims;
    }



    @Override
    protected ModelAndView onSubmit(HttpServletRequest hreq,
                                    HttpServletResponse hres,
                                    Object o,
                                    BindException error) throws Exception {

        CollectUsernamePasswordClaims cmd = (CollectUsernamePasswordClaims) o;

        if (logger.isDebugEnabled())
            logger.debug("Received CMD" + cmd);

        ClaimsRequest request = cmd.getClaimsRequest();
        if (logger.isDebugEnabled())
            logger.debug("Collecting usenrame/password claims for request " +
                    (request != null ? request.getId() : "NULL"));

        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(new ClaimImpl("username", cmd.getUsername()));
        claims.addClaim(new ClaimImpl("password", cmd.getPassword()));
        claims.addClaim(new ClaimImpl("rememberMe", cmd.isRememberMe()));

        ClaimsResponse response = new ClaimsResponseImpl(idGenerator.generateId(),
                null,
                request.getId(),
                claims,
                request.getRelayState());

        EndpointDescriptor claimsEndpoint = resolveClaimsEndpoint(request);
        if (claimsEndpoint == null) {
            logger.error("No claims endpoint found!");
            // TODO : Create error and redirect to error view using 'IDBusErrArt'
        }

        // We want the binding factory to use a binding component to build this URL, if possible
        Channel claimsChannel = request.getClaimsChannel();
        claimsChannel = getNonSerializedChannel(claimsChannel);

        String claimsEndpointUrl = null;
        if (claimsChannel != null) {

            MediationBindingFactory f = claimsChannel.getIdentityMediator().getBindingFactory();
            MediationBinding b = f.createBinding(SSOBinding.SSO_ARTIFACT.getValue(), request.getClaimsChannel());

            claimsEndpointUrl = claimsEndpoint.getResponseLocation();
            if (claimsEndpointUrl == null)
                claimsEndpointUrl = claimsEndpoint.getLocation();

            if (b instanceof AbstractMediationHttpBinding) {
                AbstractMediationHttpBinding httpBinding = (AbstractMediationHttpBinding) b;
                claimsEndpointUrl = ((AbstractMediationHttpBinding) b).buildHttpTargetLocation(hreq, claimsEndpoint, true);

            } else {
                logger.warn("Cannot delegate URL construction to binding, non-http binding found " + b);
                claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                        claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
            }
        } else {

            logger.warn("Cannot delegate URL construction to binding, valid definition of channel " +
                    request.getClaimsChannel().getName() + " not foud ...");
            claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                    claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();
        }

        if (logger.isDebugEnabled())
            logger.debug("Using claims endpoint URL ["+claimsEndpointUrl+"]");

        Artifact a = getArtifactQueueManager().pushMessage(response);
        claimsEndpointUrl += "?SSOArt=" + a.getContent();

        if (logger.isDebugEnabled())
            logger.debug("Returing claims to " + claimsEndpointUrl);

        hreq.getSession().removeAttribute("CollectUsernamePasswordClaims");

        return new ModelAndView(new RedirectView(claimsEndpointUrl));
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }

    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public IdentityMediationUnitRegistry getIdauRegistry() {
        return idauRegistry;
    }

    public void setIdauRegistry(IdentityMediationUnitRegistry idauRegistry) {
        this.idauRegistry = idauRegistry;
    }

    protected EndpointDescriptor resolveClaimsEndpoint(ClaimsRequest request) throws IdentityMediationException {

        for (IdentityMediationEndpoint endpoint : request.getClaimsChannel().getEndpoints()) {
            // Look for PWD endpoint using Artifacct binding
            if (AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue().equals(endpoint.getType()) &&
                    SSOBinding.SSO_ARTIFACT.getValue().equals(endpoint.getBinding())) {

                if (logger.isDebugEnabled())
                    logger.debug("Resolved claims endpoint " + endpoint);

                return new EndpointDescriptorImpl(endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        request.getClaimsChannel().getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                request.getClaimsChannel().getLocation() + endpoint.getResponseLocation() : null);

            }
        }

        return null;
    }

    protected Channel getNonSerializedChannel(Channel serChannel) {

        for (IdentityMediationUnit idu : idauRegistry.getIdentityMediationUnits()) {
            for (Channel c : idu.getChannels()) {
                if (c.getName().equals(serChannel.getName()))
                    return c;
            }
        }

        return null;
    }

    public String getFallbackUrl() {
        return fallbackUrl;
    }

    public void setFallbackUrl(String fallbackUrl) {
        this.fallbackUrl = fallbackUrl;
    }
}


