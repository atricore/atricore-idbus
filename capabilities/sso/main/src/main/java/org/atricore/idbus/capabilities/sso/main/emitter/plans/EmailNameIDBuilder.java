package org.atricore.idbus.capabilities.sso.main.emitter.plans;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.kernel.main.authn.SSOUser;

import javax.security.auth.Subject;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EmailNameIDBuilder extends AbstractSubjectNameIDBuilder {

    private static final Log logger = LogFactory.getLog(UnspecifiedNameIDBuiler.class);

    private String[] emailPropNames = {"email", "mail", "mailAddress", "emailAddress"};

    private String ssoUserProperty = null;

    public boolean supportsPolicy(NameIDPolicyType nameIDPolicy) {
        return nameIDPolicy.getFormat().equalsIgnoreCase(NameIDFormat.EMAIL.getValue());
    }

    public NameIDType buildNameID(NameIDPolicyType nameIDPolicy, Subject s) {
        // Subject Name Identifier
        SSOUser ssoUser = getSsoUser(s);

        // Try with several values ...
        String nameId = getEmail(ssoUser, emailPropNames);
        if (nameId == null) {
            logger.error("SSO User does not have an email property, check your identity source configuration!");
            throw new RuntimeException("SSO User does not have an email property, check your identity source configuration!");
        }

        NameIDType subjectNameID = new NameIDType();
        subjectNameID.setValue(nameId);
        subjectNameID.setFormat(NameIDFormat.EMAIL.getValue());

        return subjectNameID;

    }

    protected String getEmail(SSOUser ssoUser, String[] emailPropNames) {
        for (int i = 0; i < emailPropNames.length; i++) {
            String emailPropName = emailPropNames[i];
            String email = getPropertyValue(ssoUser, emailPropName);
            if (email != null)
                return email;

            if (StringUtils.isNotBlank(ssoUserProperty))
                email = getPropertyValue(ssoUser, ssoUserProperty);

            return email;

        }

        return null;
    }

    public String getSsoUserProperty() {
        return ssoUserProperty;
    }

    public void setSsoUserProperty(String ssoUserProperty) {
        this.ssoUserProperty = ssoUserProperty;
    }
}
