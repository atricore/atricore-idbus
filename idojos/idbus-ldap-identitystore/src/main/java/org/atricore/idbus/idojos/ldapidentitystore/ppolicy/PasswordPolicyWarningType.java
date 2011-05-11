package org.atricore.idbus.idojos.ldapidentitystore.ppolicy;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum PasswordPolicyWarningType
{
  /**
   * Indicates the number of seconds before a password will expire.
   */
  TIME_BEFORE_EXPIRATION(0, "timeBeforeExpiration"),

  /**
   * Indicates the remaining number of times a user will be allowed to
   * authenticate with an expired password.
   */
  GRACE_AUTHNS_REMAINING(1, "graceAuthNsRemaining");

  private final int intValue;

  private final String name;



  private PasswordPolicyWarningType(final int intValue, final String name)
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
   * Returns the integer value for this password policy warning type.
   *
   * @return The integer value for this password policy warning type.
   */
  int intValue()
  {
    return intValue;
  }
}
