Feature: New Apache Web Server Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define the target apache web server execution environment element

  Scenario: New local apache web server execution environment
    Given I am on the modeler screen
    When I click on the Apache Web Server execution environment item from the palette
    And I see the new apache web server execution environment form
    And I enter 'Sample Apache Web Server' as the identifier of the execution environment
    And I enter 'A Sample Apache Web Server Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the apache web server installation
    And I confirm the apache web server execution environment creation
    Then I should see the 'Sample Apache Web Server' apache web server execution environment element in the diagram



