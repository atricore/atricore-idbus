Feature: Federate SAML2 Service Provider with external OpenID Identity Provider
  In order for having a a service provider rely upon claims pushed by an external OpenID identity provider
  As an identity architect
  I want to establish a federated connection between the SAML2 service provider and the external identity provider

  Scenario: Federate a SAML2 SP with an External OpenID IdP
    Given I am on the modeler screen
    And I'm seeing an OpenID identity provider
    And I'm seeing an external identity provider
    And I'm seeing an internal SAML2 service provider
    When I drag a 'Federated Connection' item from the 'Connections' drawer of the palette
    And I link the SAML2 service provider with the external identity provider
    Then I should see a federated connection between the SAML2 service provider and external identity provider
    Then the SAML2 tab should get enabled when I access the details of the target external identity provider
