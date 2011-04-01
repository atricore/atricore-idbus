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

package org.atricore.idbus.capabilities.samlr2.main.common.producers;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.plans.SamlR2PlanningConstants;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2MessagingConstants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.samlr2.support.core.StatusCode;
import org.atricore.idbus.capabilities.samlr2.support.core.util.ProtocolUtils;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SamlR2Service;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.planning.IdentityPlan;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchangeImpl;
import org.hibernate.tuple.entity.EntityMetamodel;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Producer.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public abstract class SamlR2Producer extends AbstractCamelProducer<CamelMediationExchange>
        implements SAMLR2Constants, SAMLR2MessagingConstants, SamlR2PlanningConstants {

    private static final Log logger = LogFactory.getLog(SamlR2Producer.class);

    protected SamlR2Producer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
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

    protected IdentityPlan findIdentityPlanOfType(Class planClass) throws SamlR2Exception {

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

    protected Collection<IdentityPlan> findIdentityPlansOfType(Class planClass) throws SamlR2Exception {

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

        return getCotManager().loolkupMemberByAlias(issuer.getValue());
    }

    protected EndpointDescriptor resolveSpSloEndpoint(String spAlias,
                                                      SamlR2Binding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SamlR2Exception {

        try {

            CircleOfTrustManager cotMgr = getProvider().getCotManager();

            MetadataEntry md = cotMgr.findEntityRoleMetadata(spAlias,
                    "urn:oasis:names:tc:SAML:2.0:metadata:SPSSODescriptor");

            SPSSODescriptorType samlr2sp = (SPSSODescriptorType) md.getEntry();

            EndpointDescriptor ed = resolveEndpoint(samlr2sp.getSingleLogoutService(),
                    preferredBindings, SamlR2Service.SingleLogoutService, true);
            if (ed == null)
                throw new SamlR2Exception("No SLO Endpoint found for SP " + spAlias);

            return ed;

        } catch (CircleOfTrustManagerException e) {
            throw new SamlR2Exception(e);
        }

    }

    protected EndpointDescriptor resolveSpSloEndpoint(NameIDType spId,
                                                      SamlR2Binding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SamlR2Exception {

        CircleOfTrustMemberDescriptor sp = resolveProviderDescriptor(spId);
        return resolveSpSloEndpoint(sp.getAlias(), preferredBindings, onlyPreferredBinding);
    }

    protected EndpointDescriptor resolveIdPSloEndpoint(NameIDType idpId,
                                                      SamlR2Binding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SamlR2Exception {

        CircleOfTrustMemberDescriptor idp = resolveProviderDescriptor(idpId);
        return resolveIdPSloEndpoint(idp.getAlias(), preferredBindings, onlyPreferredBinding);
    }


    protected EndpointDescriptor resolveIdPSloEndpoint(String idpAlias,
                                                      SamlR2Binding[] preferredBindings,
                                                      boolean onlyPreferredBinding)
            throws SamlR2Exception {

        try {

            CircleOfTrustManager cotMgr = getProvider().getCotManager();

            MetadataEntry md = cotMgr.findEntityRoleMetadata(idpAlias,
                    "urn:oasis:names:tc:SAML:2.0:metadata:IDPSSODescriptor");

            IDPSSODescriptorType samlr2idp = (IDPSSODescriptorType) md.getEntry();

            EndpointDescriptor ed = resolveEndpoint(samlr2idp.getSingleLogoutService(),
                    preferredBindings, SamlR2Service.SingleLogoutService, true);

            if (ed == null)
                throw new SamlR2Exception("No SLO Endpoint found for IDP " + idpAlias);

            return ed;

        } catch (CircleOfTrustManagerException e) {
            throw new SamlR2Exception(e);
        }

    }


    protected EndpointDescriptor resolveEndpoint(List<EndpointType> endpointTypes,
                                                 SamlR2Binding[] preferredBindings,
                                                 SamlR2Service service,
                                                 boolean onlyPreferredBinding) {

        EndpointType endpointType = null;
        EndpointType preferredEndpointType = null;

        // Preferred bindings are in preference order
        for (SamlR2Binding preferredBinding : preferredBindings) {

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
    
}
