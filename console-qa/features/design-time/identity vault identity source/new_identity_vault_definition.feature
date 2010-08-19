Feature: New Identity Vault Definition
  In order to setup an identity vault
  As an identity architect
  I want to create an identity vault definition

  Scenario: Create identity vault definition
    Given I am on the modeler screen
    And I drag the identity vault element from the palette to the diagram
    And I see the identity vault creation form
    When I enter 'Sample Identity Vault' as the identity vault name
    And I enter 'A Sample Identity Vault' as the identity vault description
    And I see the value of the selected identity vault instance as 'Default' without being able to change it
    And I confirm the identity vault creation submission
    Then I should see the identity vault element in the current diagram labeled as 'Sample Identity Vault'

