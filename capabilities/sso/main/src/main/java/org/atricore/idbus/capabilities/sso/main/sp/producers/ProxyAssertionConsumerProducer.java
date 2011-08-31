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

package org.atricore.idbus.capabilities.sso.main.sp.producers;

import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SamlR2Exception;
import org.atricore.idbus.capabilities.sso.main.common.Request;
import org.atricore.idbus.capabilities.sso.main.common.Response;
import org.atricore.idbus.capabilities.sso.main.common.ResponseImpl;
import org.atricore.idbus.capabilities.sso.main.common.producers.AbstractAssertionConsumerProducer;
import org.atricore.idbus.capabilities.sso.main.common.producers.SamlR2Producer;
import org.atricore.idbus.capabilities.sso.main.sp.SamlR2SPMediator;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.w3._2001._04.xmlenc_.EncryptedType;
import org.w3c.dom.Element;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class ProxyAssertionConsumerProducer extends AbstractAssertionConsumerProducer {

    private static final Log logger = LogFactory.getLog(ProxyAssertionConsumerProducer.class);

    public ProxyAssertionConsumerProducer(AbstractCamelEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected Response processResponse(CamelMediationExchange exchange, Request request) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SamlR2SPMediator mediator = ((SamlR2SPMediator) channel.getIdentityMediator());

        // TODO : Validate inReplyTo, destination, etc
        SPInitiatedAuthnRequestType req =
                (SPInitiatedAuthnRequestType) request.getMessage();

        SPAuthnResponseType response = (SPAuthnResponseType) in.getMessage().getContent();
        if (req == null) {
            // Process unsolicited response
            validateUnsolicitedAuthnResponse(exchange, response);
        } else {
            validateAuthnResponse(exchange, req, response);
        }

        return new ResponseImpl<SPAuthnResponseType>(response.getID(), response, false);

    }

    @Override
    protected Subject extractIdPSubject(Response response) throws Exception {
        return buildSubjectFromProxyResponse((SPAuthnResponseType)response.getMessage());
    }

    private Subject buildSubjectFromProxyResponse(SPAuthnResponseType response) {

        Subject outSubject = new Subject();

        List<AbstractPrincipalType> inPrincipals = response.getSubject().getAbstractPrincipal();

        for (int i = 0; i < inPrincipals.size(); i++) {
            AbstractPrincipalType inPrincipal = inPrincipals.get(i);

            if (inPrincipal instanceof SubjectNameIDType) {
                SubjectNameIDType nameId = (SubjectNameIDType) inPrincipal;

                outSubject.getPrincipals().add(
                        new SubjectNameID(nameId.getName(),
                                nameId.getFormat(),
                                nameId.getNameQualifier(),
                                nameId.getLocalNameQualifier()));

                if (logger.isDebugEnabled()) {
                    logger.debug("Adding NameID to IDP Subject {"+nameId.getLocalNameQualifier()+"}" + nameId.getName() +  ":" + nameId.getFormat());
                }

            }

            if (inPrincipal instanceof SubjectAttributeType) {
                SubjectAttributeType attr = (SubjectAttributeType) inPrincipal;

                outSubject.getPrincipals().add(
                        new SubjectAttribute(attr.getName(),
                                attr.getValue()));

                if (logger.isDebugEnabled()) {
                    logger.debug("Adding Attribute to IDP Subject {"+attr.getName()+"}" + attr.getValue());
                }

            }

        }


        if (outSubject != null && logger.isDebugEnabled()) {
            logger.debug("IDP Subject:" + outSubject) ;
        }

        return outSubject;
    }

    protected void validateAuthnResponse(CamelMediationExchange exchange, SPInitiatedAuthnRequestType request,
                                         SPAuthnResponseType response) throws SamlR2Exception {
        // Validate in-reply-to
        if (response  == null) {
            throw new SamlR2Exception("No response found!");
        }
    }

    protected void validateUnsolicitedAuthnResponse(CamelMediationExchange exchange, SPAuthnResponseType response)
            throws SamlR2Exception {
        // Validate other attributes ?
        if (response  == null) {
            throw new SamlR2Exception("No response found!");
        }
    }

}
