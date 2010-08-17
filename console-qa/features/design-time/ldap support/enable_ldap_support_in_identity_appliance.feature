Feature: Enable user and entitlement record lookup from an LDAP directory
  In order to build on an LDAP Directory for looking up user and entitlement records
  As an identity architect
  I want to associate an identity provider with an ldap identity vault element

  Scenario: Associate an identity provider with an ldap identity vault element
    Given I am on the modeler screen
    And an identity provider 'Sample IdP' exists in my diagram
    And an identity vault 'Sample Ldap' exists in my diagram
    When I drag the identity lookup item from the palette to the diagram
    And I set the 'Sample IdP' identity provider element as the source
    And I set the 'Sample Ldap' ldap identity vault element as the target
    Then I should see an edge connecting the 'Sample IdP' element with the 'Sample Ldap' element
