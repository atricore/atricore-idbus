package org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.shared.ldap.codec.controls.AbstractControl;


/**
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyResponseControl extends AbstractControl  {

    private static final Log logger = LogFactory.getLog(PasswordPolicyResponseControl.class);

    public static final String CONTROL_OID = "1.3.6.1.4.1.42.2.27.8.5.1";

    private LDAPPasswordPolicyErrorType errorType;

    private LDAPPasswordPolicyWarningType warningType;

    private int warningValue ;

    private byte[] encodedValue;

    private boolean critical;

    private javax.naming.ldap.Control ldapControl;

    public PasswordPolicyResponseControl() {
        super( CONTROL_OID );
        decoder = new PasswordPolicyControlDecoder();
    }


    public LDAPPasswordPolicyErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(LDAPPasswordPolicyErrorType errorType) {
        this.errorType = errorType;
    }

    public LDAPPasswordPolicyWarningType getWarningType() {
        return warningType;
    }

    public void setWarningType(LDAPPasswordPolicyWarningType warningType) {
        this.warningType = warningType;
    }

    public int getWarningValue() {
        return warningValue;
    }

    public void setWarningValue(int warningValue) {
        this.warningValue = warningValue;
    }

    public String getID() {
        return CONTROL_OID;
    }

    public boolean isCritical() {
        return critical;
    }

    public byte[] getEncodedValue() {
        return encodedValue;
    }
}
