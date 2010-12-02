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

package org.atricore.idbus.capabilities.samlr2.main.sp.producers;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.ManageNameIDRequestType;
import oasis.names.tc.saml._2_0.protocol.StatusCodeType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.common.producers.SamlR2Producer;
import org.atricore.idbus.capabilities.samlr2.main.sp.SamlR2SPMediator;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.core.*;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.SamlR2Encrypter;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.SamlR2EncrypterException;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2SignatureException;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2SignatureValidationException;
import org.atricore.idbus.capabilities.samlr2.support.core.signature.SamlR2Signer;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.kernel.main.federation.AccountLink;
import org.atricore.idbus.kernel.main.federation.AccountLinkLifecycle;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.metadata.*;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.security.auth.Subject;
import java.util.Date;

public class SPNameIDManagementProducer extends SamlR2Producer {

    private static final Log logger = LogFactory.getLog(SPNameIDManagementProducer.class);

    private static final String SAML2_VERSION = "2.0";

    public SPNameIDManagementProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
//		try{
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        ManageNameIDRequestType manageNameID = (ManageNameIDRequestType) in.getMessage().getContent();

        StatusType status = new StatusType();
        StatusCodeType statusCode = new StatusCodeType();
        statusCode.setValue(StatusCode.TOP_SUCCESS.getValue());
        status.setStatusCode(statusCode);

        boolean validated = true;
        StringBuffer secondaryErrorCode = new StringBuffer();

        try {
            manageNameID = validateManageNameID(manageNameID, secondaryErrorCode);
        } catch (SamlR2RequestException e1) {
            logger.error("Error validating ManageNameIDRequest", e1);
            validated = false;
        }

        if (validated) {
            if (manageNameID.getTerminate() != null) {
                SubjectNameID subjectNameID = null;
                if (manageNameID.getNameID() != null) {
                    subjectNameID = new SubjectNameID(manageNameID.getNameID().getValue(), manageNameID.getNameID().getFormat());
                    subjectNameID.setLocalName(manageNameID.getNameID().getSPProvidedID());
                } else {
                    NameIDType decryptedNameID = null;
                    SamlR2Encrypter encrypter = ((SamlR2SPMediator) channel.getIdentityMediator()).getEncrypter();
                    try {
                        decryptedNameID = encrypter.decryptNameID(manageNameID.getEncryptedID());
                    } catch (SamlR2EncrypterException e) {
                        //TODO should we throw RuntimeException?
                        throw new SamlR2Exception("NameID cannot be decrypted.", e);
                    }
                    subjectNameID = new SubjectNameID(decryptedNameID.getValue(), decryptedNameID.getFormat());
                    subjectNameID.setLocalName(decryptedNameID.getSPProvidedID());
                }
                Subject idpSubject = new Subject();
                idpSubject.getPrincipals().add(subjectNameID);

                // check if there is an existing session for the user
                FederationChannel fChannel = (FederationChannel) channel;

                // if not, check if channel is federation-capable
                if (fChannel.getAccountLinkLifecycle() == null) {

                    // cannot map subject to local account, terminate
                    logger.error("No Account Lifecycle configured for Channel [" + fChannel.getName() + "] " +
                            " ManageNameID [" + manageNameID.getID() + "]");
                    throw new SamlR2Exception("No Account Lifecycle configured for Channel [" + fChannel.getName() + "] " +
                            " ManageNameID [" + manageNameID.getID() + "]");
                }

                AccountLinkLifecycle accountLinkLifecycle = fChannel.getAccountLinkLifecycle();
                AccountLink accountLink = accountLinkLifecycle.findByIDPAccount(idpSubject);
                if (accountLink == null) {
                    logger.error("No Account Link available for Principal [" + subjectNameID.getName() + "]");
                    throw new SamlR2Exception("No Account Link available for Principal [" + subjectNameID.getName() + "]");
                }
                accountLinkLifecycle.dispose(accountLink);
            }
        }

        // ---------------------------------------------------
        // Send ManageNameIDResponse
        // ---------------------------------------------------

        CircleOfTrustMemberDescriptor idp = this.resolveIdp();
        logger.debug("Using IDP " + idp.getAlias());

        // Select endpoint, must be a ManageNameIDService endpoint
        EndpointType idpSsoEndpoint = resolveIdpMNIDEndpoint(idp);
        EndpointDescriptor destination = new EndpointDescriptorImpl(
                "IDPMNIEndpoint",
                "ManageNameIDService",
                idpSsoEndpoint.getBinding(),
                idpSsoEndpoint.getLocation(),
                idpSsoEndpoint.getResponseLocation());

        StatusResponseType mnidResponse = buildMNIDResponse(exchange, idp, idpSsoEndpoint, validated, secondaryErrorCode.toString());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(mnidResponse.getID(),
                mnidResponse, "ManageNameIDResponse", null, destination, in.getMessage().getState()));

        exchange.setOut(out);
    }

    //Initial version of buildMNIDResponse method, not using plans
    private StatusResponseType buildMNIDResponse(CamelMediationExchange exchange,
                                                 CircleOfTrustMemberDescriptor idp, EndpointType idpSsoEndpoint, boolean validated, String secondaryErrorCode) {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        ManageNameIDRequestType manageNameID = (ManageNameIDRequestType) in.getMessage().getContent();

        StatusResponseType response = new StatusResponseType();
        UUIDGenerator uuidGenerator = new UUIDGenerator();

        response.setID(uuidGenerator.generateId());
        response.setInResponseTo(manageNameID.getID());

        StatusType status = new StatusType();
        StatusCodeType statusCode = new StatusCodeType();
        if (validated) {
            statusCode.setValue("urn:oasis:names:tc:SAML:2.0:status:Success");
        } else {
            if (secondaryErrorCode.equals(StatusDetails.INVALID_VERSION)) {
                statusCode.setValue("urn:oasis:names:tc:SAML:2.0:status:VersionMismatch");
            } else {
                statusCode.setValue("urn:oasis:names:tc:SAML:2.0:status:Requester");
            }
            status.setStatusMessage(secondaryErrorCode);
        }
        status.setStatusCode(statusCode);
        response.setStatus(status);

        if (idpSsoEndpoint.getResponseLocation() != null) {
            response.setDestination(idpSsoEndpoint.getResponseLocation());
        } else {
            response.setDestination(idpSsoEndpoint.getLocation());
        }

        response.setVersion(SAMLR2Constants.SAML_VERSION);
        response.setIssueInstant(DateUtils.toXMLGregorianCalendar(new Date()));
        if (idp != null) {
            NameIDType issuer = new NameIDType();
            issuer.setFormat(NameIDFormat.ENTITY.getValue());
            issuer.setValue(idp.getAlias());
            response.setIssuer(issuer);
        }

        return response;
    }

    private EndpointType resolveIdpMNIDEndpoint(CircleOfTrustMemberDescriptor idp) throws SamlR2Exception {

        SamlR2SPMediator mediator = (SamlR2SPMediator) ((IdPChannel) channel).getIdentityMediator();
        SamlR2Binding preferredBinding = mediator.getPreferredIdpSSOBindingValue();
        MetadataEntry idpMd = idp.getMetadata();

        if (idpMd == null || idpMd.getEntry() == null)
            throw new SamlR2Exception("No metadata descriptor found for IDP " + idp);

        if (idpMd.getEntry() instanceof EntityDescriptorType) {
            EntityDescriptorType md = (EntityDescriptorType) idpMd.getEntry();

            for (RoleDescriptorType role : md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (role instanceof IDPSSODescriptorType) {

                    IDPSSODescriptorType idpSsoRole = (IDPSSODescriptorType) role;

                    EndpointType defaultEndpoint = null;
                    EndpointType postEndpoint = null;
                    EndpointType artEndpoint = null;

                    for (EndpointType idpMnidEndpoint : idpSsoRole.getManageNameIDService()) {

                        SamlR2Binding b = SamlR2Binding.asEnum(idpMnidEndpoint.getBinding());
                        if (b.equals(preferredBinding))
                            return idpMnidEndpoint;

                        if (b.equals(SamlR2Binding.SAMLR2_ARTIFACT))
                            artEndpoint = idpMnidEndpoint;

                        if (b.equals(SamlR2Binding.SAMLR2_POST))
                            postEndpoint = idpMnidEndpoint;

                        if (defaultEndpoint == null)
                            defaultEndpoint = idpMnidEndpoint;
                    }

                    if (artEndpoint != null)
                        defaultEndpoint = artEndpoint;
                    else if (postEndpoint != null)
                        defaultEndpoint = postEndpoint;

                    return defaultEndpoint;
                }
            }
        } else {
            throw new SamlR2Exception("Unknown metadata descriptor type " + idpMd.getEntry().getClass().getName());
        }

        logger.debug("No IDP Endpoint supporting binding : " + preferredBinding);
        throw new SamlR2Exception("IDP does not support preferred binding " + preferredBinding);

    }

    protected CircleOfTrustMemberDescriptor resolveIdp() throws SamlR2Exception {
        SamlR2SPMediator mediator = (SamlR2SPMediator) ((IdPChannel) channel).getIdentityMediator();

        String idpAlias = mediator.getPreferredIdpAlias();
        if (idpAlias == null) {
            throw new SamlR2Exception("No IDP available");
        }

        CircleOfTrustMemberDescriptor idp = this.getCotManager().loolkupMemberByAlias(idpAlias);
        if (idp == null) {
            throw new SamlR2Exception("No IDP Member descriptor available for " + idpAlias);
        }

        return idp;

    }

    protected ManageNameIDRequestType validateManageNameID(ManageNameIDRequestType manageNameID, StringBuffer secondaryErrorCode) throws SamlR2RequestException {
        EndpointDescriptor epointDesc;
        try {

            epointDesc = channel.getIdentityMediator().resolveEndpoint(channel, endpoint);

        } catch (IdentityMediationException e1) {

            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    null,
                    StatusDetails.INVALID_DESTINATION,
                    "Cannot resolve endpoint descriptor",
                    e1);
        }

        String idpAlias = null;

        IDPSSODescriptorType idpMd = null;

        try {
            idpAlias = manageNameID.getIssuer().getValue();
            MetadataEntry md = getCotManager().findEntityMetadata(idpAlias);
            EntityDescriptorType saml2Md = (EntityDescriptorType) md.getEntry();
            boolean found = false;
            for (RoleDescriptorType roleMd : saml2Md.getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor()) {

                if (roleMd instanceof IDPSSODescriptorType) {
                    idpMd = (IDPSSODescriptorType) roleMd;
                }
            }

        } catch (CircleOfTrustManagerException e) {
            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.NO_SUPPORTED_IDP,
                    null,
                    manageNameID.getIssuer().getValue(),
                    e);
        }

        //ID
        if (manageNameID.getID() == null) {
            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.INVALID_DESTINATION,
                    "No 'ID' attribute in ManageNameIDRequest.");
        }

        //destination
        if (manageNameID.getDestination() == null && (epointDesc.getBinding().equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST") ||
                epointDesc.getBinding().equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect"))) {

            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.INVALID_DESTINATION,
                    "No Destination attribute in ManageNameIDRequest.");
        }

        //issue instant
        if (manageNameID.getIssueInstant() == null) {
            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.NO_ISSUE_INSTANT);

        }

        //nameID or encryptedNameID required
        if (manageNameID.getNameID() == null && manageNameID.getEncryptedID() == null) {
            secondaryErrorCode.append(StatusDetails.NO_NAMEID_ENCRYPTEDID.toString());

            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.NO_NAMEID_ENCRYPTEDID);
        }

        //Issuer
        if (manageNameID.getIssuer() == null) {
            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.NO_ISSUER,
                    "No Issuer element.");
        } else {
            //Format attrib. must be omitted or have value of: urn:oasis:names:tc:SAML:2.0:nameid-format:entity
            if (manageNameID.getIssuer().getFormat() != null && !manageNameID.getIssuer().getFormat().equals("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")) {
                throw new SamlR2RequestException(manageNameID,
                        StatusCode.TOP_RESPONDER,
                        StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                        StatusDetails.INVALID_ISSUER_FORMAT);
            }
        }

        // Version, saml2 core, section 3.2.2
        if (manageNameID.getVersion() == null || !manageNameID.getVersion().equals(SAML2_VERSION)) {
            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.REQUEST_VERSION_TOO_LOW,
                    StatusDetails.INVALID_VERSION);
        }


        //signature must exist for http post and http redirect bindings
        SamlR2Signer signer = ((SamlR2SPMediator) channel.getIdentityMediator()).getSigner();
        if (manageNameID.getSignature() != null) {
            try {
                signer.validate(idpMd, manageNameID);
            } catch (SamlR2SignatureValidationException e) {
                secondaryErrorCode.append(StatusDetails.INVALID_REQUEST_SIGNATURE.toString());
                throw new SamlR2RequestException(manageNameID,
                        StatusCode.TOP_RESPONDER,
                        StatusCode.REQUEST_DENIED,
                        StatusDetails.INVALID_REQUEST_SIGNATURE);
            } catch (SamlR2SignatureException e) {
                throw new SamlR2RequestException(manageNameID,
                        StatusCode.TOP_RESPONDER,
                        StatusCode.REQUEST_DENIED,
                        StatusDetails.INVALID_REQUEST_SIGNATURE, e);
            }
        } else if (epointDesc.getBinding().equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST") ||
                epointDesc.getBinding().equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect")) {

            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.REQUEST_DENIED,
                    StatusDetails.INVALID_REQUEST_SIGNATURE,
                    "No Signature for ManageNameIDRequest for HTTP-POST or HTTP-Redirect binding.");
        }

        //if one of the following exists: NewID, NewEncryptedID,Terminate
        if (manageNameID.getNewID() == null && manageNameID.getNewEncryptedID() == null && manageNameID.getTerminate() == null) {
            throw new SamlR2RequestException(manageNameID,
                    StatusCode.TOP_RESPONDER,
                    StatusCode.INVALID_ATTR_NAME_OR_VALUE,
                    StatusDetails.NO_NEWID_NEWENCRYPTEDID_TERMINATE);
        }

        return manageNameID;
    }

}
