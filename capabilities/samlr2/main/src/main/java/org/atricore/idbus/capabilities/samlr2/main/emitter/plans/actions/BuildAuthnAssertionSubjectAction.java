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

package org.atricore.idbus.capabilities.samlr2.main.emitter.plans.actions;

import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.emitter.SamlR2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.capabilities.samlr2.support.core.util.DateUtils;
import org.atricore.idbus.capabilities.samlr2.support.profiles.SubjectConfirmationMethod;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.jbpm.graph.exe.ExecutionContext;

import javax.security.auth.Subject;
import java.util.Date;
import java.util.Set;

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
public class BuildAuthnAssertionSubjectAction extends AbstractSAMLR2AssertionAction {

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

        Set<SSOUser> ssoUsers = s.getPrincipals(SSOUser.class);
        if (ssoUsers == null || ssoUsers.size() != 1)
            throw new RuntimeException("Subject must contain a SSOUser principal");
        SSOUser ssoUser = ssoUsers.iterator().next();

        oasis.names.tc.saml._2_0.assertion.ObjectFactory samlObjectFactory;
        samlObjectFactory = new oasis.names.tc.saml._2_0.assertion.ObjectFactory();

        // Subject Confirmation Data

        SubjectConfirmationDataType subjectConfirmationData = samlObjectFactory.createSubjectConfirmationDataType();

        SamlR2SecurityTokenEmissionContext ctx =
                (SamlR2SecurityTokenEmissionContext) executionContext.getContextInstance().getVariable(RST_CTX);

        if (ctx != null && ctx.getRequest() != null) {
            subjectConfirmationData.setRecipient(
                ((AuthnRequestType)ctx.getRequest()).getAssertionConsumerServiceURL()
            );

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
        // TODO : Check AuthnRequest to see if a Subject element is present, check also SP springmetadata ?
        SubjectType subject = samlObjectFactory.createSubjectType();

        // Subject Name Identifier
        // TODO : Check AuthnRequest to see if NameIDPolicy is present, check also SP springmetadata ?
        NameIDType subjectNameID = new NameIDType();
        subjectNameID.setFormat(NameIDFormat.UNSPECIFIED.getValue());
        subjectNameID.setValue(ssoUser.getName());
        // TODO : Set SPNameQualifier
        //subjectNameID.setSPNameQualifier("google.com/a/atricore.com");

        // Previously built parts
        subject.getContent().add(samlObjectFactory.createNameID(subjectNameID));
        subject.getContent().add(samlObjectFactory.createSubjectConfirmation(subjectConfirmation));

        // Add the subject to the assertion
        assertion.setSubject(subject);

        logger.debug("ending-action");

    }
}
