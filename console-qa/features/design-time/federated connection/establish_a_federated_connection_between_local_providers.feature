Feature: Establish a Federated Connection between a local identity provider and a local service provider
  In order to connect two providers so that one trusts the other
  As an identity architect
  I want to connect the source local service provider with the target local identity provider

  Scenario: Connect a local identity provider with a local service provider using inherited settings
    Given I am on the modeler screen
    And my diagram contains a local identity provider 'Sample Local IdP'
    And my diagram contains a local service provider 'Sample Local SP'
    When I click on the Federated Connection item from the palette
    And I select the source 'Sample Local IdP' identity provider element
    And I select the target 'Sample Local SP' service provider element
    And I see the new federated connection form
    And I enter 'Sample Connection Name' as the identifier of the connection
    And I enter 'Sample Connection Description' as the description of the connection
    And I select the Identity Provider Channel tab
    And I choose to use the inherited identity provider settings
    And I see the identity provider's location value not being able to change it
    And I see the enable SSO SAML profile option checked not being able to uncheck it
    And I see the enable SLO SAML profile option checked not being able to uncheck it
    And I see the identity provider's user information lookup mechanism not being able to change it
    And I see the identity provider's authentication contract not being able to change it
    And I see the identity provider's authentication mechanism not being able to change it
    And I see the identity provider's authentication assertion emission policy not being able to change it
    And I select the Service Provider Channel tab
    And I choose to use the inherited service provider settings
    And I see the service provider's location value not being able to change it
    And I see the service provider's user information lookup mechanism not being able to change it
    And I see the service provider's authentication contract not being able to change it
    And I see the service provider's authentication mechanism not being able to change it
    And I see the service provider's account linkage policy not being able to change it
    And I confirm the federation connection creation submission
    Then I should see the federated connection between the identity provider 'Sample Local IdP' and 'Sample Local SP'
