Feature: New External Identity Provider Definition
  In order to enable inbound federation support for an external identity provider to my identity appliance
  As an identity architect
  I want to create an External Identity Provider definition

  Scenario: Create an external Identity Provider
    Given I am on the modeler screen
    And I drag the 'External Identity Provider' item from the 'Entities' drawer onto the diagram
    When I see the element creation form
    And I enter 'an-external-idp' as the external identity provider name
    And I enter 'A Sample External IdP' as the identity provider description
    When I confirm the creation submission
    Then I should see the external identity provider element in the current diagram labeled as 'an-external-idp'

