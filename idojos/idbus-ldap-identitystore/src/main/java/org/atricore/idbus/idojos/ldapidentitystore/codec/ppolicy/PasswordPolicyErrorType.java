package org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum PasswordPolicyErrorType {

    /**
     * Indicates that the password has expired and must be reset.
     */
    PASSWORD_EXPIRED(0, "passwordExpired"),

    /**
     * Indicates that the user's account has been locked.
     */
    ACCOUNT_LOCKED(1, "accountLocked"),

    /**
     * Indicates that the password must be changed before the user will be allowed
     * to perform any operation other than bind and modify.
     */
    CHANGE_AFTER_RESET(2, "changeAfterReset"),

    /**
     * Indicates that a user is restricted from changing her password.
     */
    PASSWORD_MOD_NOT_ALLOWED(3, "passwordModNotAllowed"),

    /**
     * Indicates that the old password must be supplied in order to modify the
     * password.
     */
    MUST_SUPPLY_OLD_PASSWORD(4, "mustSupplyOldPassword"),

    /**
     * Indicates that a password doesn't pass quality checking.
     */
    INSUFFICIENT_PASSWORD_QUALITY(5, "insufficientPasswordQuality"),

    /**
     * Indicates that a password is not long enough.
     */
    PASSWORD_TOO_SHORT(6, "passwordTooShort"),

    /**
     * Indicates that the age of the password to be modified is not yet old
     * enough.
     */
    PASSWORD_TOO_YOUNG(7, "passwordTooYoung"),

    /**
     * Indicates that a password has already been used and the user must choose a
     * different one.
     */
    PASSWORD_IN_HISTORY(8, "passwordInHistory");

    private final int intValue;

    private final String name;

    private PasswordPolicyErrorType(final int intValue, final String name)
    {
      this.intValue = intValue;
      this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
      return name;
    }



    /**
     * Returns the integer value for this password policy error type.
     *
     * @return The integer value for this password policy error type.
     */
    public int intValue()
    {
      return intValue;
    }

    public static PasswordPolicyErrorType getErrorType(int errorTypeInt) {
        for (PasswordPolicyErrorType errorType : PasswordPolicyErrorType .values()) {
            if (errorType.intValue() == errorTypeInt)
                return errorType;
        }
        throw new RuntimeException("Invalid error type : " + errorTypeInt);

    }
}
