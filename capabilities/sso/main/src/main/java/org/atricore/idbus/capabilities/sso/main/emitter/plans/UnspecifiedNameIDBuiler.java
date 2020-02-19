package org.atricore.idbus.capabilities.sso.main.emitter.plans;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;

import javax.security.auth.Subject;
import java.security.Principal;

/**
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UnspecifiedNameIDBuiler extends  AbstractSubjectNameIDBuilder  {

    private static final Log logger = LogFactory.getLog(UnspecifiedNameIDBuiler.class);

    // The name of the property to be used as name ID
    private String ssoUserProperty;

    public boolean supportsPolicy(String nameIDPolicy) {
        return nameIDPolicy.equalsIgnoreCase(NameIDFormat.UNSPECIFIED.getValue());
    }

    public boolean supportsPolicy(NameIDPolicyType nameIDPolicy) {
        return nameIDPolicy.getFormat().equals(NameIDFormat.UNSPECIFIED.getValue());
    }

    public NameIDType buildNameID(NameIDPolicyType nameIDPolicy, Subject s) {

        // Subject Name Identifier

        String nameId = null;

        if (StringUtils.isNotBlank(ssoUserProperty)) {
            SSOUser ssoUser = getSsoUser(s);
            nameId = getPropertyValue(ssoUser, ssoUserProperty);
            if (nameId == null)
                logger.error("NameID: No value for user property ("+ssoUserProperty+"). User name will be used instead.");
            else if (logger.isDebugEnabled())
                logger.debug("NameID ("+ssoUserProperty+")" + nameId);
        }

        // Get username as principal
        if (nameId == null) {
            for (Principal p : s.getPrincipals()) {
                if (p instanceof SSOUser || p instanceof SimplePrincipal)
                    nameId = p.getName();
            }
        }

        NameIDType subjectNameID = new NameIDType();
        subjectNameID.setValue(nameId);
        subjectNameID.setFormat(NameIDFormat.UNSPECIFIED.getValue());

        return subjectNameID;
    }

    public String getSsoUserProperty() {
        return ssoUserProperty;
    }

    public void setSsoUserProperty(String ssoUserProperty) {
        this.ssoUserProperty = ssoUserProperty;
    }
}
