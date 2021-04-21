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

package org.atricore.idbus.capabilities.sso.main.common.plans.actions;

import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.ChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.sp.IDPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SignRequestAction extends AbstractSSOAction {

    private static final Log logger = LogFactory.getLog(SignRequestAction.class);

    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception {

        RequestAbstractType request = (RequestAbstractType) out.getContent();

        Channel channel = (Channel) executionContext.getContextInstance().getVariable(VAR_CHANNEL);

        // Requets are normally issued from a binding channel, so we use the response channel to get the configuration
        Channel responseChannel = (Channel) executionContext.getContextInstance().getVariable(VAR_RESPONSE_CHANNEL);
        AbstractSSOMediator mediator = (AbstractSSOMediator) channel.getIdentityMediator();
        ChannelConfiguration cfg = responseChannel != null ?
                mediator.getChannelConfig(responseChannel.getName()) :
                mediator.getChannelConfig(channel.getName());

        String digest = null;
        if (cfg instanceof SPChannelConfiguration) {
            digest = ((SPChannelConfiguration) cfg).getSignatureHash();
        } else if (cfg instanceof IDPChannelConfiguration) {
            digest = ((IDPChannelConfiguration) cfg).getSignatureHash();
        } else {
            digest = "SHA256";
        }

        // TODO : Support signing some requests: i.e. when IdP requires authn. requests to be signed
        boolean signRequest = mediator.isSignRequests();

        // SAML MD overrides default
        if (request instanceof AuthnRequestType) {
            CircleOfTrustMemberDescriptor idp = (CircleOfTrustMemberDescriptor) executionContext.getContextInstance().getVariable( VAR_DESTINATION_COT_MEMBER );
            if (idp != null) {
                signRequest = wantAuthnRequestsSigned(idp);
            }

        }

        if (!signRequest) {
            if (logger.isDebugEnabled())
                logger.debug("Signature disabled for " + channel.getName());
            return ;
        }

        SamlR2Signer signer = mediator.getSigner();
        if (logger.isDebugEnabled())
            logger.debug("Signing SAMLR2 Request : " + request.getID() + " in channel " + channel.getName());

        if (signer == null) {
            throw new IllegalStateException("No Signer found in mediatior " + mediator);
        }

        out.replaceContent(signer.sign(request, digest));
    }



    protected boolean wantAuthnRequestsSigned(CircleOfTrustMemberDescriptor idp) {

        MetadataEntry idpMd = idp.getMetadata();

        if (idpMd.getEntry() instanceof EntityDescriptorType) {

            EntityDescriptorType md = (EntityDescriptorType) idpMd.getEntry();

            for (RoleDescriptorType role : md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (role instanceof IDPSSODescriptorType) {
                    IDPSSODescriptorType idpSsoRole = (IDPSSODescriptorType) role;
                    Boolean wantAuthnRequestsSigned = idpSsoRole.getWantAuthnRequestsSigned();

                    if (logger.isDebugEnabled())
                        logger.debug(idp.getAlias() + ":WantAuthnRequestsSigned=" + wantAuthnRequestsSigned );

                    return wantAuthnRequestsSigned  != null ? wantAuthnRequestsSigned  : false;
                }

            }

            logger.error("Non-IdP Metadata found " + idpMd.getEntry() + ", SAML 2 IDP Role expected");

        } else {
            logger.error("Unsupported Metadata type " + idpMd.getEntry() + ", SAML 2 Metadata expected");
        }

        return false;


    }


}
