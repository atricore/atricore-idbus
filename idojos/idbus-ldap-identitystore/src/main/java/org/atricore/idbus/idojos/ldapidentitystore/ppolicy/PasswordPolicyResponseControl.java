package org.atricore.idbus.idojos.ldapidentitystore.ppolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.ldap.Control;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyResponseControl {

    private static final Log logger = LogFactory.getLog(PasswordPolicyResponseControl.class);

    public static final String OID = "1.3.6.1.4.1.42.2.27.8.5.1";

    private PasswordPolicyErrorType errorType;

    private PasswordPolicyWarningType warningType;

    private int warningValue ;

    public PasswordPolicyErrorType getErrorType() {
        return errorType;
    }

    public PasswordPolicyWarningType getWarningType() {
        return warningType;
    }

    public int getWarningValue() {
        return warningValue;
    }

    protected PasswordPolicyResponseControl(PasswordPolicyErrorType errorType,
                                            PasswordPolicyWarningType warningType,
                                            int warningValue) {
        this.errorType = errorType;
        this.warningType = warningType;
        this.warningValue = warningValue;
    }

    public static PasswordPolicyResponseControl decode(Control[] controls) {

        for (Control control : controls) {

            if (!control.getID().equals(OID)) {
                continue;
            }

            if (control.getEncodedValue() == null || control.getEncodedValue().length == 0)
                return null;

            // TODO : Deconde ASN.1 BER Value and create control instance !!!
            return new PasswordPolicyResponseControl(null, null, -1);
        }
        logger.warn("No LDAP Response Control with OID " + OID);
        return null;
    }
}
