package org.atricore.idbus.kernel.main.store.exceptions;

/**
 *
 */
public class CredentialsPolicyVerificationException extends SSOIdentityException {

    public static final String PASSWORD_EXPIRED = "passwordExpired";

    public static final String ACCOIUNT_LOCKED = "accountLocked";

    public static final String CHANGE_AFTER_RESET = "changeAfterReset";

    public static final String PASSWORD_MOD_NOT_ALLOWED = "passwordModNotAllowed";

    public static final String MUST_SUPPLY_OLD_PASSWORD = "mustSupplyOldPassword";

    public static final String INVALID_PASSWORD_SYNTAX = "invalidPasswordSyntax";

    public static final String PASSWORD_TOO_SHORT = "passwordTooShort";

    public static final String PASSWORD_TOO_YOUNG = "passwordTooYoung";

    public static final String PASSWORD_IN_HISTORY = "passwordInHistory";

    public static final String PASSWORD_CONTAINS_USERNAME = "passwordContainsUsername";

    public static final String INVALID_PASSWORD = "invalidPassword";

    private String[] errorCodes;


    public CredentialsPolicyVerificationException(String[] codes) {
        super();
        this.errorCodes = codes;
    }

    public CredentialsPolicyVerificationException(String[] codes, String message) {
        super(message);
        this.errorCodes = codes;
    }

    public CredentialsPolicyVerificationException(String[] codes, Throwable cause) {
        super(cause);
        this.errorCodes = codes;
    }

    public CredentialsPolicyVerificationException(String[] codes, String message, Throwable cause) {
        super(message, cause);
        this.errorCodes = codes;
    }

    public String[] getErrorCodes() {
        return errorCodes;
    }
}
