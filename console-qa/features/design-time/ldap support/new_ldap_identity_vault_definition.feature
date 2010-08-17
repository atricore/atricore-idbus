Feature: New Ldap Identity Vault Definition
  In order to determine the mechanism for looking up user and entitlement records
  As an identity architect
  I want to create an ldap identity vault definition

  Scenario: Create ldap identity vault definition
    Given I am on the modeler screen
    And I am seeing the ldap identity vault definition creation form
    When I enter 'Sample Ldap' as the ldap identity vault name
    And I enter 'A Sample Ldap' as the ldap identity vault description
    And I enter 'http://localhost' as the identity vault host
    And I enter 389 as the identity vault port
    And I enter 'dc=foobar, dc=com' as the base DN
    When I confirm the ldap identity vault creation submission
    Then I should see the ldap identity vault element in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity vault definition with SSL support
    Given I am on the modeler screen
    And I am seeing the ldap identity vault definition creation form
    When I enter 'Sample Ldap' as the ldap identity vault name
    And I enter 'A Sample Ldap' as the ldap identity vault description
    And I enter 'http://localhost' as the identity vault host
    And I enter 389 as the identity vault port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select the supported encryption method checkbox
    When I confirm the ldap identity vault creation submission
    Then I should see the ldap identity vault element supporting encryption on in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity vault definition using anonymous authentication
    Given I am on the modeler screen
    And I am seeing the ldap identity vault definition creation form
    When I enter 'Sample Ldap' as the ldap identity vault name
    And I enter 'A Sample Ldap' as the ldap identity vault description
    And I enter 'http://localhost' as the identity vault host
    And I enter 389 as the identity vault port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select none as the authentication mechanism
    When I confirm the ldap identity vault creation submission
    Then I should see the ldap identity vault element supporting anonymous authentication in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity vault definition using the simple authentication mechanism
    Given I am on the modeler screen
    And I am seeing the ldap identity vault definition creation form
    When I enter 'Sample Ldap' as the ldap identity vault name
    And I enter 'A Sample Ldap' as the ldap identity vault description
    And I enter 'http://localhost' as the identity vault host
    And I enter 389 as the identity vault port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select simple as the authentication mechanism
    And I enter 'user1' as the security principal DN
    And I enter 'user1pwd' as the security principal password
    When I confirm the ldap identity vault creation submission
    Then I should see the ldap identity vault element supporting simple authentication in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity vault definition using the strong authentication mechanism
    Given I am on the modeler screen
    And I am seeing the ldap identity vault definition creation form
    When I enter 'Sample Ldap' as the ldap identity vault name
    And I enter 'A Sample Ldap' as the ldap identity vault description
    And I enter 'http://localhost' as the identity vault host
    And I enter 389 as the identity vault port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select strong as the authentication mechanism
    And I upload the x509 certificate to be used for authenticating against the ldap directory
    When I confirm the ldap identity vault creation submission
    Then I should see the ldap identity vault element supporting strong authentication in the current diagram labeled as 'Sample Ldap'
