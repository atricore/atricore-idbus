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

package org.atricore.idbus.capabilities.sso.main.common.producers;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.plans.SSOPlanningConstants;
import org.atricore.idbus.capabilities.sso.main.sp.SPSecurityContext;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2MessagingConstants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.StatusCode;
import org.atricore.idbus.capabilities.sso.support.core.util.ProtocolUtils;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.planning.IdentityPlan;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchangeImpl;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SSOProducer.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public abstract class SSOProducer extends AbstractCamelProducer<CamelMediationExchange>
        implements SAMLR2Constants, SAMLR2MessagingConstants, SSOPlanningConstants {

    private static final Log logger = LogFactory.getLog(SSOProducer.class);

    protected SSOProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange e) throws Exception {
        // DO Nothing!
    }

    protected FederatedLocalProvider getProvider() {
        if (channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getProvider();
        } else if (channel instanceof BindingChannel) {
            return ((BindingChannel) channel).getProvider();
        } else if (channel instanceof ClaimChannel) {
            return ((ClaimChannel) channel).getProvider();
        } else {
            throw new IllegalStateException("Configured channel does not support Federated Provider : " + channel);
        }
    }

    protected IdentityPlan findIdentityPlanOfType(Class planClass) throws SSOException {

        Collection<IdentityPlan> plans = this.endpoint.getIdentityPlans();
        if (plans != null) {
            for (IdentityPlan plan : plans) {
                if (planClass.isInstance(plan))
                    return plan;
            }
        }

        logger.warn("No identity plan of class " + planClass.getName() + " was found for endpoint " + endpoint.getName());
        return null;

    }

    protected Collection<IdentityPlan> findIdentityPlansOfType(Class planClass) throws SSOException {

        java.util.List<IdentityPlan> found = new java.util.ArrayList<IdentityPlan>();

        Collection<IdentityPlan> plans = this.endpoint.getIdentityPlans();
        for (IdentityPlan plan : plans) {
            if (planClass.isInstance(plan))
                found.add(plan);
        }

        return found;

    }

    protected IdentityPlanExecutionExchange createIdentityPlanExecutionExchange() {

        IdentityPlanExecutionExchange ex = new IdentityPlanExecutionExchangeImpl();

        // Publish some important attributes:
        // Circle of trust will allow actions to access identity configuration

        ex.setProperty(VAR_COT, this.getCot());
        ex.setProperty(VAR_COT_MEMBER, this.getCotMemberDescriptor());
        ex.setProperty(VAR_CHANNEL, this.channel);
        ex.setProperty(VAR_ENDPOINT, this.endpoint);

        return ex;

    }

    protected CircleOfTrust getCot() {
        if (this.channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getCircleOfTrust();
        }

        if (logger.isDebugEnabled())
            logger.debug("There is no associated circle of trust, channel is not a federation channel");

        return null;
    }

    protected CircleOfTrustManager getCotManager() {
        if (this.channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getProvider().getCotManager();
        } else if (this.channel instanceof BindingChannel) {
            return ((BindingChannel) channel).getProvider().getCotManager();
        }

        if (logger.isDebugEnabled())
            logger.debug("There is no associated circle of trust, channel is not a federation channel");

        return null;
    }


    protected CircleOfTrustMemberDescriptor getCotMemberDescriptor() {
        if (this.channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getMember();
        }

        if (logger.isDebugEnabled())
            logger.debug("There is no associated circle of trust member descriptor, channel is not a federation channel");

        return null;
    }

    protected CircleOfTrustMemberDescriptor resolveProviderDescriptor(NameIDType issuer) {

        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDFormat.ENTITY.getValue())) {
            logger.warn("Invalid issuer format for entity : " + issuer.getFormat());
            return null;
        }

        return getCotManager().lookupMemberByAlias(issuer.getValue());
    }

    protected EndpointDescriptor resolveSpSloEndpoint(String spAlias,
                                                      SSOBinding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SSOException {

        try {

            CircleOfTrustManager cotMgr = getProvider().getCotManager();

            MetadataEntry md = cotMgr.findEntityRoleMetadata(spAlias,
                    "urn:oasis:names:tc:SAML:2.0:metadata:SPSSODescriptor");

            SPSSODescriptorType samlr2sp = (SPSSODescriptorType) md.getEntry();

            EndpointDescriptor ed = resolveEndpoint(samlr2sp.getSingleLogoutService(),
                    preferredBindings, SSOService.SingleLogoutService, true);
            if (ed == null)
                logger.warn("No SLO Endpoint found for SP " + spAlias);

            return ed;

        } catch (CircleOfTrustManagerException e) {
            throw new SSOException(e);
        }

    }

    protected EndpointDescriptor resolveSpSloEndpoint(NameIDType spId,
                                                      SSOBinding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SSOException {

        CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(spId);
        return resolveSpSloEndpoint(sp.getAlias(), preferredBindings, onlyPreferredBinding);
    }

    protected EndpointDescriptor resolveIdPSloEndpoint(NameIDType idpId,
                                                      SSOBinding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SSOException {

        CircleOfTrustMemberDescriptor idp = resolveProviderDescriptor(idpId);
        return resolveIdPSloEndpoint(idp.getAlias(), preferredBindings, onlyPreferredBinding);
    }


    protected EndpointDescriptor resolveIdPSloEndpoint(String idpAlias,
                                                      SSOBinding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SSOException {

        try {

            CircleOfTrustManager cotMgr = getProvider().getCotManager();

            MetadataEntry md = cotMgr.findEntityRoleMetadata(idpAlias,
                    "urn:oasis:names:tc:SAML:2.0:metadata:IDPSSODescriptor");

            IDPSSODescriptorType samlr2idp = (IDPSSODescriptorType) md.getEntry();

            EndpointDescriptor ed = resolveEndpoint(samlr2idp.getSingleLogoutService(),
                    preferredBindings, SSOService.SingleLogoutService, true);

            if (ed == null)
                throw new SSOException("No SLO Endpoint found for IDP " + idpAlias);

            return ed;

        } catch (CircleOfTrustManagerException e) {
            throw new SSOException(e);
        }

    }


    protected EndpointDescriptor resolveEndpoint(List<EndpointType> endpointTypes,
                                                 SSOBinding[] preferredBindings,
                                                 SSOService service,
                                                 boolean onlyPreferredBinding) {

        EndpointType endpointType = null;
        EndpointType preferredEndpointType = null;

        // Preferred bindings are in preference order
        for (SSOBinding preferredBinding : preferredBindings) {

            for (EndpointType currentSloEndpoint : endpointTypes) {

                if (endpointType == null)
                    endpointType = currentSloEndpoint ;

                if (currentSloEndpoint.getBinding().equals(preferredBinding.getValue()))
                    preferredEndpointType = currentSloEndpoint;

                if (preferredEndpointType != null)
                    break;
            }

            if (preferredEndpointType != null)
                break;
        }

        if (onlyPreferredBinding || preferredEndpointType != null)
            endpointType = preferredEndpointType;

        if (logger.isDebugEnabled())
            logger.debug("Selected endpoint " + (endpointType != null ? endpointType.getBinding() : "<NONE>"));

        if (endpointType == null)
            return null;

        return new EndpointDescriptorImpl(endpointType.getBinding(),
                service.toString(),
                endpointType.getBinding(),
                endpointType.getLocation(),
                endpointType.getResponseLocation());

    }

    protected IDPSSODescriptorType getIDPSSODescriptor() {
        CircleOfTrustMemberDescriptor cotDescr = this.getCotMemberDescriptor();

        EntityDescriptorType samlMd = (EntityDescriptorType) cotDescr.getMetadata().getEntry();

        for (RoleDescriptorType roleDescr : samlMd.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {
            if (roleDescr instanceof IDPSSODescriptorType)
                return (IDPSSODescriptorType) roleDescr;
        }
        return null;

    }

    protected SPSSODescriptorType getSPSSODescriptor() {
        CircleOfTrustMemberDescriptor cotDescr = this.getCotMemberDescriptor();

        EntityDescriptorType samlMd = (EntityDescriptorType) cotDescr.getMetadata().getEntry();

        for (RoleDescriptorType roleDescr : samlMd.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {
            if (roleDescr instanceof SPSSODescriptorType)
                return (SPSSODescriptorType) roleDescr;
        }
        return null;

    }

    protected boolean isStatusCodeValid(String statusCode) {
        try {
            StatusCode.asEnum(statusCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    protected SubjectType toSubjectType(Subject subject) {
        return ProtocolUtils.toSubjectType(subject);
    }

    protected Subject toSubjectType(SubjectType subjectType) {
        return ProtocolUtils.toSubject(subjectType);
    }

    /**
     * @return
     */
    protected FederationChannel resolveIdpChannel(CircleOfTrustMemberDescriptor idpDescriptor) {
        // Resolve IdP channel, then look for the ACS endpoint
        BindingChannel bChannel = (BindingChannel) channel;
        FederatedLocalProvider sp = bChannel.getProvider();

        FederationChannel idpChannel = sp.getChannel();
        for (FederationChannel fChannel : sp.getChannels()) {

            FederatedProvider idp = fChannel.getTargetProvider();
            for (CircleOfTrustMemberDescriptor member : idp.getMembers()) {
                if (member.getAlias().equals(idpDescriptor.getAlias())) {

                    if (logger.isDebugEnabled())
                        logger.debug("Selected IdP channel " + fChannel.getName() + " for provider " + idp.getName());
                    idpChannel = fChannel;
                    break;
                }

            }

        }

        return idpChannel;

    }

    protected void destroySPSecurityContext(CamelMediationExchange exchange,
                                            SPSecurityContext secCtx) throws SSOException {

        CircleOfTrustMemberDescriptor idp = getCotManager().lookupMemberByAlias(secCtx.getIdpAlias());
        IdPChannel idpChannel = (IdPChannel) resolveIdpChannel(idp);
        SSOSessionManager ssoSessionManager = idpChannel.getSessionManager();
        secCtx.clear();

        try {
            ssoSessionManager.invalidate(secCtx.getSessionIndex());
            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
            in.getMessage().getState().removeRemoteVariable(getProvider().getName().toUpperCase() + "_SECURITY_CTX");
        } catch (NoSuchSessionException e) {
            logger.debug("SSO Session already invalidated " + secCtx.getSessionIndex());
        } catch (Exception e) {
            throw new SSOException(e);
        }

    }

    
}
