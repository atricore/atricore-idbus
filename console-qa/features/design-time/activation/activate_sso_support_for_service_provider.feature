Feature: Activate SSO support for Service Provider
  In order to activate JOSSO2-based support in the target execution environment
  As an identity architect
  I want to connect a local service provider with a local execution environment

  Scenario: Activate a local service provider against a local execution environment with no previous activations
    Given I am on the modeler screen
    And the diagram contains a service provider element 'Sample SP'
    And the diagram contains an execution environment element 'Sample Execution Environment'
    When I click on the activation option from the palette
    And I drag it to the diagram
    And I select the service provider element
    And I select the execution environment element
    And I see the new activation for service provider form
    And I enter 'Sample Activation' as the identifier of the activation element
    And I enter 'A Sample Activation' as the description of the activation element
    And I click on the create activation button
    Then I should see a progress window informing that the target environment is being activated
    And I should see a report that the activation operation has been performed successfully
    And I should see an activation edge between the 'Sample SP' node and the 'Sample Execution Environment' node

  Scenario: Activate a local service provider against a local execution environment previously activated
    Given I am on the modeler screen
    And the diagram contains a service provider element 'Sample SP'
    And the diagram contains an execution environment element 'Sample Execution Environment'
    When I click on the activation option from the palette
    And I drag it to the diagram
    And I select the service provider element
    And I select the execution environment element
    And I see the new activation for service provider form
    And I enter 'Sample Activation' as the identifier of the activation element
    And I enter 'A Sample Activation' as the description of the activation element
    And I click on the create activation button
    Then I should see an activation edge between the 'Sample SP' node and the 'Sample Execution Environment' node




