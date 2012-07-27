Feature: New External Service Provider Definition
  In order to enable outbound federation support for an external service provider to my identity appliance
  As an identity architect
  I want to create an External Identity Provider definition

  Scenario: Create an external Identity Provider
    Given I am on the modeler screen
    And I drag the 'External Service Provider' item from the 'Entities' drawer onto the diagram
    When I see the element creation form
    And I enter 'an-external-sp' as the external identity provider name
    And I enter 'A Sample External SP' as the identity provider description
    When I confirm the creation submission
    Then I should see the external service provider element in the current diagram labeled as 'an-external-sp'

