Feature: New Weblogic 10 Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define the target weblogic 10 execution environment element

  Scenario: New local weblogic 10 execution environment
    Given I am on the modeler screen
    When I click on the Weblogic 10 execution environment item from the palette
    And I see the new weblogic 10 execution environment form
    And I enter 'Sample Weblogic 10' as the identifier of the execution environment
    And I enter 'A Sample Weblogic 10 Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the weblogic 10 installation
    And I confirm the weblogic 10 execution environment creation
    Then I should see the 'Sample Weblogic 10' weblogic 10 execution environment element in the diagram



