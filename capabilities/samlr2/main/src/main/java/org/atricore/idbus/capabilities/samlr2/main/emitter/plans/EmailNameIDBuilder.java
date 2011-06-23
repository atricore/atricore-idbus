package org.atricore.idbus.capabilities.samlr2.main.emitter.plans;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EmailNameIDBuilder extends AbstractSubjectNameIDBuilder {

    private static final Log logger = LogFactory.getLog(UnspecifiedNameIDBuiler.class);

    public boolean supportsPolicy(NameIDPolicyType nameIDPolicy) {
        return nameIDPolicy.getFormat().equalsIgnoreCase(NameIDFormat.EMAIL.getValue());
    }

    public NameIDType buildNameID(NameIDPolicyType nameIDPolicy, Subject s) {
        // Subject Name Identifier
        SSOUser ssoUser = getSsoUser(s);

        // Try with several values ...
        String nameId = getPropertyValue(ssoUser, "urn:org:atricore:idbus:user:property:mail");
        if (nameId == null) {
            nameId = getPropertyValue(ssoUser, "urn:org:atricore:idbus:user:property:email");
        }

        if (nameId == null) {
            nameId = getPropertyValue(ssoUser, "urn:org:atricore:idbus:user:property:emailAddress");
        }

        if (nameId == null) {
            logger.error("SSO User does not have an email property, check your identity source configuration!");
            throw new RuntimeException("SSO User does not have an email property, check your identity source configuration!");
        }

        NameIDType subjectNameID = new NameIDType();
        subjectNameID.setValue(nameId);
        subjectNameID.setFormat(NameIDFormat.EMAIL.getValue());

        return subjectNameID;

    }
}
