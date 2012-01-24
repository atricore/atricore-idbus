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

package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions;

import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.SubjectNameIDBuilder;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.sso.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.sso.support.profiles.SubjectConfirmationMethod;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.Date;

/**
 * <h2>Standards Reference</h2>
 * <p>This action will populate the SAML2 Assertion Subject as described in:
 * <ul>
 * <li>SAML 2.0 Core, Processing Rules (section 3.4.1.4)</li>
 * <li>SAML 2.0 Web Browser SSO Profile, &lt;Response&gt; usage (section 4.1.4.2)</li>
 * <li>WS-Trust 1.3 Interoperability Profile: SAML 2.0 Token Profile, SAML 2 Token Creation (section 2.3)</li>
 * </ul>
 * </p>
 * <h3>SAML 2.0 Core, Processing Rules (section 3.4.1.4)</h3>
 * <p>
 * If the &lt;saml:Subject&gt; element in the request is present, then the resulting assertions'
 * &lt;saml:Subject&gt; MUST strongly match the request &lt;saml:Subject&gt;, as described in Section 3.3.4,
 * except that the identifier MAY be in a different format if specified by &lt;NameIDPolicy&gt;. In such a case,
 * the identifier's physical content MAY be different, but it MUST refer to the same principal.
 * <br>
 * All of the content defined specifically within <AuthnRequest> is optional, although some may be required
 * by certain profiles. In the absence of any specific content at all, the following behavior is implied:
 * <ul>
 * <li>The assertion(s) returned MUST contain a &lt;saml:Subject&gt; element that represents the presenter.
 * The identifier type and format are determined by the identity provider. At least one
 * statement in at least one assertion MUST be a <saml:AuthnStatement> that describes the
 * authentication performed by the responder or authentication service associated with it.
 * </li>
 * <li>The request presenter should, to the extent possible, be the only attesting entity able to satisfy the
 * &lt;saml:SubjectConfirmation&gt; of the assertion(s). In the case of weaker confirmation
 * methods, binding-specific or other mechanisms will be used to help satisfy this requirement.
 * </li>
 * </ul>
 * </p>
 * <h3>SAML 2.0 Web Browser SSO Profile, &lt;Response&gt; usage (section 4.1.4.2)</h3>
 * <p>TBD</p>
 * <h3>WS-Trust 1.3 Interoperability Profile: SAML 2.0 Token Profile, SAML 2 Token Creation (section 2.3)</h3>
 * <p>
 * When a token service issues a SAML 2 token it MUST produce a token with the following criteria:
 * <ul>
 * <li>The assertion MUST contain a &lt;saml2:Subject&gt;.</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: BuildAuthnAssertionSubjectAction.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class BuildAuthnAssertionSubjectAction extends AbstractSSOAssertionAction {

    private static final Log logger = LogFactory.getLog(BuildAuthnAssertionSubjectAction.class);

    /**
     * <h3>SAML 2.0 Web Browser SSO Profile, &lt;AuthnRequest&gt; usage (section 4.1.4.1) </h3>
     * <p>
     * Note that if the &lt;AuthnRequest&gt; is not authenticated and/or integrity protected, the information in it
     * MUST NOT be trusted except as advisory. Whether the request is signed or not, the identity provider
     * MUST ensure that any &lt;AssertionConsumerServiceURL&gt; or
     * &lt;AssertionConsumerServiceIndex&gt; elements in the request are verified as belonging to the service
     * provider to whom the response will be sent. Failure to do so can result in a man-in-the-middle attack.
     * </p>
     * <h3>SAML 2.0 Web Browser SSO Profile, Response Usage (section 4.1.4.2)
     * <p>
     * At least one assertion containing an &lt;AuthnStatement&gt; MUST contain a &lt;Subject&gt; element with
     * at least one &lt;SubjectConfirmation&gt; element containing a Method of
     * urn:oasis:names:tc:SAML:2.0:cm:bearer. If the identity provider supports the Single Logout
     * profile, defined in Section 4.4, any such authentication statements MUST include a SessionIndex
     * attribute to enable per-session logout requests by the service provider.
     * </p>
     * <p>
     * The bearer &lt;SubjectConfirmation&gt; element described above MUST contain a
     * &lt;SubjectConfirmationData&gt; element that contains a Recipient attribute containing the service
     * provider's assertion consumer service URL and a NotOnOrAfter attribute that limits the window
     * during which the assertion can be delivered. It MAY contain an Address attribute limiting the client
     * address from which the assertion can be delivered. It MUST NOT contain a NotBefore attribute. If
     * the containing message is in response to an &lt;AuthnRequest&gt;, then the InResponseTo attribute
     * MUST match the request's ID.
     * </p>
     *
     * @param executionContext
     */
    @Override
    protected void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) {

        logger.debug("starting action");


        if (!(out.getContent() instanceof AssertionType))
            throw new IllegalArgumentException("Output Identity Artifact MUST be contain SAMLR2 Assertion");

        AssertionType assertion = (AssertionType) out.getContent();

        // Do we have a SSOUser ?
        Subject s = (Subject) executionContext.getContextInstance().getVariable(WSTConstants.SUBJECT_PROP);
        if (s == null)
            throw new IllegalArgumentException("Subject not found as process variable : " + WSTConstants.SUBJECT_PROP);

        oasis.names.tc.saml._2_0.assertion.ObjectFactory samlObjectFactory;
        samlObjectFactory = new oasis.names.tc.saml._2_0.assertion.ObjectFactory();

        // Subject Confirmation Data

        SubjectConfirmationDataType subjectConfirmationData = samlObjectFactory.createSubjectConfirmationDataType();

        SamlR2SecurityTokenEmissionContext ctx =
                (SamlR2SecurityTokenEmissionContext) executionContext.getContextInstance().getVariable(RST_CTX);

        if (ctx != null && ctx.getRequest() != null) {
            AuthnRequestType authnReq = (AuthnRequestType)ctx.getRequest();
            subjectConfirmationData.setInResponseTo(authnReq.getID());
            subjectConfirmationData.setRecipient(authnReq.getAssertionConsumerServiceURL());

        }

        // Subject Confirmation : NotOnOrAfter (required)
        Date dateNow = new java.util.Date();
        subjectConfirmationData.setNotOnOrAfter(DateUtils.toXMLGregorianCalendar(dateNow.getTime() + (1000L * 60L * 5)));
        // TODO : Check when we need to set SubjectConfirmation notBefore
        // subjectConfirmationData.setNotBefore(DateUtils.toXMLGregorianCalendar(dateNow.getTime() - (1000L * 60L * 5)));

        // Subject Confirmation In Response To : If we have an authn request, set the ID here!
        if (ctx.getAuthnState() != null) {
            // TODO : Check when we need to sent SubjectConfirmation inResponseTo
            AuthnRequestType req = ctx.getAuthnState().getAuthnRequest();
//            if (req != null)
//                subjectConfirmationData.setInResponseTo(req.getID());
        }

        // Subject Confirmation, Confirmation Method Bearer is required.
        SubjectConfirmationType subjectConfirmation = samlObjectFactory.createSubjectConfirmationType();
        subjectConfirmation.setMethod(SubjectConfirmationMethod.BEARER.getValue());
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        // Subject
        // TODO : Check AuthnRequest to see if a Subject element is present, check also SP SAML2 Metadata ?
        SubjectType subject = samlObjectFactory.createSubjectType();

        // TODO : Set SPNameQualifier
        //subjectNameID.setSPNameQualifier("google.com/a/atricore.com");



        NameIDType subjectNameID = null;
        NameIDPolicyType nameIDPolicy = resolveNameIDPolicy(ctx);

        SubjectNameIDBuilder nameIDBuilder = resolveNameIDBuiler(executionContext, nameIDPolicy);
        subjectNameID = nameIDBuilder.buildNameID(nameIDPolicy, s);
        if (subjectNameID == null)
            throw new RuntimeException("No NameID builder found for " + nameIDPolicy.getFormat());

        // Previously built parts
        subject.getContent().add(samlObjectFactory.createNameID(subjectNameID));
        subject.getContent().add(samlObjectFactory.createSubjectConfirmation(subjectConfirmation));

        // Add the subject to the assertion
        assertion.setSubject(subject);

        logger.debug("ending-action");

    }


    protected NameIDPolicyType resolveNameIDPolicy(SamlR2SecurityTokenEmissionContext ctx) {

        // Take NameID policy from request
        NameIDPolicyType nameIDPolicy = null;
        if (ctx.getRequest() != null) {
            if (ctx.getRequest()  instanceof AuthnRequestType) {
                AuthnRequestType authnRequest = (AuthnRequestType) ctx.getRequest();
                nameIDPolicy = authnRequest.getNameIDPolicy();
            }
        }

        if (nameIDPolicy == null) {
            // TODO : Take NameIDFormat from Provider Metadata
            // TODO : Consider SAML R2 Metadata, it can also specify the required Name ID policy as <md:NameIDFormat> in SSO Descriptor!
        }

        if (nameIDPolicy == null) {

            if (logger.isDebugEnabled())
                logger.debug("Using default NameIDPolicy");

            // Default name id policy : unspecified
            nameIDPolicy = new NameIDPolicyType();
            nameIDPolicy.setFormat(NameIDFormat.UNSPECIFIED.getValue());
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Using request NameIDPolicy " + nameIDPolicy.getFormat());

        }

        return nameIDPolicy;
    }

    protected SubjectNameIDBuilder resolveNameIDBuiler(ExecutionContext executionContext, NameIDPolicyType nameIDPolicy) {

        Boolean ignoreRequestedNameIDPolicy = (Boolean) executionContext.getContextInstance().getTransientVariable(VAR_IGNORE_REQUESTED_NAMEID_POLICY);

        SubjectNameIDBuilder defaultNameIDBuilder = (SubjectNameIDBuilder) executionContext.getContextInstance().getTransientVariable(VAR_DEFAULT_NAMEID_BUILDER);
        if (ignoreRequestedNameIDPolicy != null && ignoreRequestedNameIDPolicy) {
            if (logger.isDebugEnabled())
                logger.debug("Ignoring requested NameIDPolicy, using DefaultNameIDBuilder : " + defaultNameIDBuilder);
            return defaultNameIDBuilder;
        }

        Collection<SubjectNameIDBuilder> nameIdBuilders =
                (Collection<SubjectNameIDBuilder>) executionContext.getContextInstance().getTransientVariable(VAR_NAMEID_BUILDERS);

        if (nameIdBuilders == null || nameIdBuilders.size() == 0)
            throw new RuntimeException("No NameIDBuilders configured for plan!");

        for (SubjectNameIDBuilder nameIDBuilder : nameIdBuilders) {

            if (nameIDBuilder.supportsPolicy(nameIDPolicy)) {
                if (logger.isDebugEnabled())
                    logger.debug("Using NameIDBuilder : " + nameIDBuilder);
                return nameIDBuilder;

            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Using DefaultNameIDBuilder : " + defaultNameIDBuilder);

        return defaultNameIDBuilder;

    }
}
