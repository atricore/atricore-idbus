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

package org.atricore.idbus.capabilities.samlr2.main.common.plans.actions;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.samlr2.main.emitter.plans.actions.AbstractSAMLR2AssertionAction;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Date;

/**
 * <p>This action will initialize a SAML 2.0 Assertion</p>
 *
 * <p>For further reference:
 * <ul>
 * <li>SAML 2.0 Core Assertions (section 2.3)</li>
 * <li>SAML 2.0 Web Browser SSO Profile, &lt;Response&gt; usage (section 4.1.4.2)
 * </ul>
 * </p>
 *
 * TODO : This class could also be AuthnAssertionInitialization ?
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: InitializeAssertionAction.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class InitializeAssertionAction extends AbstractSAMLR2AssertionAction {

    public static final Log logger = LogFactory.getLog(InitializeAssertionAction.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    /**
     * <h3>SAML 2.0 Core Entity identifier, section 8.3.7</h3>
     * <p>
     * Indicates that the content of the element is the identifier of an entity that provides SAML-based services
     * (such as a SAML authority, requester, or responder) or is a participant in SAML profiles (such as a service
     * provider supporting the browser SSO profile). Such an identifier can be used in the &lt;Issuer&gt; element to
     * identify the issuer of a SAML request, response, or assertion, or within the &lt;NameID&gt; element to make
     * assertions about system entities that can issue SAML requests, responses, and assertions. It can also be
     * used in other elements and attributes whose purpose is to identify a system entity in various protocol
     * exchanges.
     *
     * The syntax of such an identifier is a URI of not more than 1024 characters in length. It is
     * RECOMMENDED that a system entity use a URL containing its own domain name to identify itself.
     * </p>
     * <h3>SAML 2.0 Web Browser SSO Profile, &lt;Response&gt; usage (section 4.1.4.2) </h3>
     * <p>
     * It MUST contain at least one &lt;Assertion&gt;. Each assertion's &lt;Issuer&gt; element MUST contain the
     * unique identifier of the issuing identity provider; the Format attribute MUST be omitted or have a value
     * of urn:oasis:names:tc:SAML:2.0:nameid-format:entity.
     * </p>
     * @param executionContext
     */
    @Override
    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) {

        logger.debug("starting action");

        if (!(out.getContent() instanceof AssertionType))
            throw new IllegalArgumentException("Output Identity Artifact content must be a SAMLR2 Assertion");
        
        AssertionType assertion = (AssertionType) out.getContent();

        // Requiered stuff (SAML Core, section 2.3.3)

        // Version
        assertion.setVersion(SAMLR2Constants.SAML_VERSION);

        // ID
        assertion.setID(uuidGenerator.generateId());

        // IssueInstant
        Date dateNow = new java.util.Date();
        assertion.setIssueInstant(DateUtils.toXMLGregorianCalendar(dateNow));

        // Issuer
        // TODO : Check when name qualifier and other attributes must be used


        SamlR2SecurityTokenEmissionContext ctx =
                (SamlR2SecurityTokenEmissionContext) executionContext.getContextInstance().getVariable(RST_CTX);


        if (ctx != null) {
            EntityDescriptorType idpMd = (EntityDescriptorType) ctx.getIssuerMetadata().getEntry();
            String idpEntityId = idpMd.getEntityID();

            NameIDType issuer = new NameIDType();
            issuer.setFormat(NameIDFormat.ENTITY.getValue());
            issuer.setValue(idpEntityId);
            assertion.setIssuer(issuer);

            // TODO : Check SAML 2.0 Entity identifier 8.3.x : persitente/transient formats)
            // issuer.setSPNameQualifier("sp_atricore");
            // issuer.setNameQualifier("idp_atricore");

        }

        logger.debug("ending action");
    }


    public UUIDGenerator getUuidGenerator() {
        return uuidGenerator;
    }

    public void setUuidGenerator(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }
}
