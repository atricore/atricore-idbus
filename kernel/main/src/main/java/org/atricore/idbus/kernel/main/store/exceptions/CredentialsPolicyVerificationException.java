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

    public static final String INVALID_PASSWORD = "invalidPassowrd";

    private String errorCode;


    public CredentialsPolicyVerificationException(String code) {
        super();
        this.errorCode = code;
    }

    public CredentialsPolicyVerificationException(String code, String message) {
        super(message);
        this.errorCode = code;
    }

    public CredentialsPolicyVerificationException(String code, Throwable cause) {
        super(cause);
        this.errorCode = code;
    }

    public CredentialsPolicyVerificationException(String code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
