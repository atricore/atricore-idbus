Feature: Establish a Federated Connection between a local service provider and a non-SAML2 identity provider
  In order to connect two providers so that one trusts the other
  As an identity architect
  I want to connect the source local service provider with the target non-SAML identity provider

  Scenario: Connect a local service provider with a an external OpenID identity provider
    Given I am on the modeler screen
    And my diagram contains an OpenID identity provider 'Sample OpenID IdP'
    And my diagram contains a local service provider 'Sample Local SP'
    When I click on the Federated Connection item from the palette
    And I select the source 'Sample OpenID IdP' identity provider element
    And I select the target 'Sample Local SP' service provider element
    And I see the new federated connection form
    And I enter 'Sample Connection Name' as the identifier of the connection
    And I enter 'Sample Connection Description' as the description of the connection
    And I select the Identity Provider Channel tab
    And I choose to use the inherited identity provider settings
    And I see OpenID-specific identity provider's preferences not being able to uncheck it
    And I select the Service Provider Channel tab
    And I choose to use the inherited service provider settings
    And I see the service provider's location value not being able to change it
    And I see the service provider's user information lookup mechanism not being able to change it
    And I see the service provider's authentication contract not being able to change it
    And I see the service provider's authentication mechanism not being able to change it
    And I see the service provider's account linkage policy not being able to change it
    And I confirm the federation connection creation submission
    Then I should see the federated connection between the OpenID identity provider 'Sample OpenID IdP' and 'Sample Local SP'
