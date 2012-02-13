Feature: New JOSSO1 Resource Definition
  In order to define a legacy JOSSO1 candidate application for Federated SSO
  As an identity architect
  I want to create an JOSSO1 Resource definition

  Scenario: Create a JOSSO1 Resource
    Given I am on the modeler screen
    And I drag the 'JOSSO1 Resource' item from the 'Resources' drawer onto the diagram
    When I see the element creation form
    And I enter 'a-josso1-resource' as the josso resource name
    And I enter 'A Sample JOSSO1 Resource' as the JOSSO1 resource description
    And I confirm the creation submission
    Then I should see the JOSSO1 resource element in the current diagram labeled as 'a-josso1-resource'

