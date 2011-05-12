package org.atricore.idbus.kernel.main.authn;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum PasswordPolicyWarningType implements Serializable {

    /**
     * Indicates the number of seconds before a password will expire.
     */
    TIME_BEFORE_EXPIRATION("timeBeforeExpiration"),

    /**
     * Indicates the remaining number of times a user will be allowed to
     * authenticate with an expired password.
     */
    GRACE_AUTHNS_REMAINING("graceAuthNsRemaining");

    private final String name;

    private PasswordPolicyWarningType(final String name)
    {
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
