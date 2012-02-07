Feature: New JOSSO2 Resource Definition
  In order to define a candidate application for Federated SSO
  As an identity architect
  I want to create an JOSSO2 Resource definition

  Scenario: Create a JOSSO2 Resource
    Given I am on the modeler screen
    And I drag the 'JOSSO2 Resource' item from the 'Resources' drawer onto the diagram
    When I see the element creation form
    And I enter 'a-josso2-resource' as the josso resource name
    And I enter 'A Sample JOSSO2 Resource' as the JOSSO2 resource description
    And I confirm the creation submission
    Then I should see the JOSSO2 resource element in the current diagram labeled as 'a-josso2-resource'

