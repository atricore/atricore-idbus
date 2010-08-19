Feature: Deactivate SSO support for Service Provider
  In order to deactivate JOSSO2-based support in the target execution environment
  As an identity architect
  I want to disconnect a local service provider from a local execution environment

  Scenario: disconnect a local service provider from a local execution environment
    Given I am on the modeler screen
    And the diagram contains a service provider element 'Sample SP'
    And the diagram contains an execution environment element 'Sample Execution Environment'
    And the 'Sample SP' element is connected with the 'Sample Execution Environment' through an activation edge
    When I drag over the activation edge
    And I click on the removal option
    Then the should no longer see the edge connecting the 'Sample SP' node with the 'Sample Execution Environment' node





