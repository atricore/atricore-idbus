package org.atricore.idbus.kernel.main.authn;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum PasswordPolicyErrorType implements Serializable {

    PASSWORD_EXPIRED("passwordExpired"),

    /**
     * Indicates that the user's account has been locked.
     */
    ACCOUNT_LOCKED("accountLocked"),

    /**
     * Indicates that the password must be changed before the user will be allowed
     * to perform any operation other than bind and modify.
     */
    CHANGE_PASSWORD_REQUIRED("changePasswordRequired"),;

    private final String name;

    private PasswordPolicyErrorType(final String name) {
      this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
      return name;
    }


}
