Feature: New Windows IIS Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define to define the target windows IIS execution environment element

  Scenario: New local windows IIS execution environment
    Given I am on the modeler screen
    When I click on the Windows IIS execution environment item from the palette
    And I see the new windows IIS execution environment form
    And I enter 'Sample Windows IIS' as the identifier of the execution environment
    And I enter 'A Sample Windows IIS Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the windows IIS installation
    And I confirm the windows IIS execution environment creation
    Then I should see the 'Sample Windows IIS' windows IIS execution environment element in the diagram



