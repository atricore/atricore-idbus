Feature: New Websphere CE Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define the target websphere ce execution environment element

  Scenario: New local websphere ce execution environment
    Given I am on the modeler screen
    When I click on the Websphere CE execution environment item from the palette
    And I see the new websphere ce execution environment form
    And I enter 'Sample Websphere CE' as the identifier of the execution environment
    And I enter 'A Sample Websphere CE Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the websphere ce installation
    And I confirm the websphere ce execution environment creation
    Then I should see the 'Sample Websphere CE' websphere ce execution environment element in the diagram



