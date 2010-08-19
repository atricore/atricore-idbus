Feature: New Ldap Identity Source Definition
  In order to setup an Ldap identity source
  As an identity architect
  I want to create an ldap identity source definition

  Scenario: Create ldap identity source definition
    Given I am on the modeler screen
    And I drag the ldap identity source element from the palette to the diagram
    And I should see the ldap identity source creation form
    When I enter 'Sample Ldap' as the ldap identity source name
    And I enter 'A Sample Ldap' as the ldap identity source description
    And I enter 'http://localhost' as the identity source host
    And I enter 389 as the identity source port
    And I enter 'dc=foobar, dc=com' as the base DN
    When I confirm the ldap identity source creation submission
    Then I should see the ldap identity source element in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity source definition with SSL support
    Given I am on the modeler screen
    And I drag the ldap identity source element from the palette to the diagram
    And I should see the ldap identity source creation form
    When I enter 'Sample Ldap' as the ldap identity source name
    And I enter 'A Sample Ldap' as the ldap identity source description
    And I enter 'http://localhost' as the identity source host
    And I enter 389 as the identity source port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select the supported encryption method checkbox
    And I confirm the ldap identity source creation submission
    Then I should see the ldap identity source element supporting encryption on in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity source definition using anonymous authentication
    Given I am on the modeler screen
    And I drag the ldap identity source element from the palette to the diagram
    And I should see the ldap identity source creation form
    When I enter 'Sample Ldap' as the ldap identity source name
    And I enter 'A Sample Ldap' as the ldap identity source description
    And I enter 'http://localhost' as the identity source host
    And I enter 389 as the identity source port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select none as the authentication mechanism
    And I confirm the ldap identity source creation submission
    Then I should see the ldap identity source element supporting anonymous authentication in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity source definition using the simple authentication mechanism
    Given I am on the modeler screen
    And I drag the ldap identity source element from the palette to the diagram
    And I should see the ldap identity source creation form
    When I enter 'Sample Ldap' as the ldap identity source name
    And I enter 'A Sample Ldap' as the ldap identity source description
    And I enter 'http://localhost' as the identity source host
    And I enter 389 as the identity source port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select simple as the authentication mechanism
    And I enter 'user1' as the security principal DN
    And I enter 'user1pwd' as the security principal password
    And I confirm the ldap identity source creation submission
    Then I should see the ldap identity source element supporting simple authentication in the current diagram labeled as 'Sample Ldap'

  Scenario: Create ldap identity source definition using the strong authentication mechanism
    Given I am on the modeler screen
    And I drag the ldap identity source element from the palette to the diagram
    And I should see the ldap identity source creation form
    When I enter 'Sample Ldap' as the ldap identity source name
    And I enter 'A Sample Ldap' as the ldap identity source description
    And I enter 'http://localhost' as the identity source host
    And I enter 389 as the identity source port
    And I enter 'dc=foobar, dc=com' as the base DN
    And I select strong as the authentication mechanism
    And I upload the x509 certificate to be used for authenticating against the ldap directory
    And I confirm the ldap identity source creation submission
    Then I should see the ldap identity source element supporting strong authentication in the current diagram labeled as 'Sample Ldap'
