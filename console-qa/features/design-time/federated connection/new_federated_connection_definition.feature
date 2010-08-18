Feature: Establish a Federated Connection between an identity provider and a service provider
  In order to connect two providers so that one trusts the other
  As an identity architect
  I want to connect the source service provider with the target identity provider

  Scenario: Connect an identity provider with a service provider
    Given I am on the modeler screen
    And my diagram contains an identity provider 'Sample IdP'
    And my diagram contains a service provider 'Sample SP'
    When I click on the Federated Connection item from the palette
    And I enter 'Sample Connection Name' as the identifier of the connection
    And I enter 'Sample Connection Description' as the description of the connection
    And I select the Identity Provider End tab
    And I check the Honor SSO option
    And I check the Honor SLO option
    And I check the Http Post Binding option
    And I select the Service Provider End tab
    And I check the Rely SSO
    And I check the Rely SLO
    And I confirm the creation submission
    Then I should see the federated connection between the identity provider 'Sample IdP' and 'Sample SP'

