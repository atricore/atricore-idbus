Feature: Establish a Federated Connection between a local identity provider and a non-SAML2 service provider
  In order to connect two providers so that one trusts the other
  As an identity architect
  I want to connect the source local identity provider with the target non-SAML service provider

  Scenario: Connect a local identity provider with a an external OpenID service provider
    Given I am on the modeler screen
    And my diagram contains an OpenID service provider 'Sample OpenID SP'
    And my diagram contains a local identity provider 'Sample Local IdP'
    When I click on the Federated Connection item from the palette
    And I select the source 'Sample Local IdP' identity provider element
    And I select the target 'Sample OpenID SP' service provider element
    And I see the new federated connection form
    And I enter 'Sample Connection Name' as the identifier of the connection
    And I enter 'Sample Connection Description' as the description of the connection
    And I select the Identity Provider Channel tab
    And I choose to use the inherited identity provider settings
    And I see SAML2-specific identity provider's preferences not being able to uncheck it
    And I select the Service Provider Channel tab
    And I choose to use the inherited service provider settings
    And I see the OpenID-specific service provider's preferences not being able to change it
    Then I should see the federated connection between the Local identity provider 'Local IdP' and 'Sample OpenID SP'
