Feature: Proxy OpenID Identity Provider
  In order to be able to connect internal service providers to an external OpenID identity provider
  As an identity architect
  I want to establish a proxy association between the service provider and the external identity provider

  Scenario: Create an external OpenID identity provider
    Given I am on the modeler screen
    And I'm seeing an OpenID identity provider
    And I'm seeing an external identity provider
    When I drag a 'Proxy' item from the 'Connections' drawer of the palette
    And I link the external identity provider with the OpenID identity provider
    Then I should see a proxy connection between the external identity provider and the OpenID identity provider
