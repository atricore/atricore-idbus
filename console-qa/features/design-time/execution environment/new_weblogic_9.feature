Feature: New Weblogic 9 Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define to define the target weblogic 9 execution environment element

  Scenario: New local weblogic 9 execution environment
    Given I am on the modeler screen
    When I click on the Weblogic 9 execution environment item from the palette
    And I see the new weblogic 9 execution environment form
    And I enter 'Sample Weblogic 9' as the identifier of the execution environment
    And I enter 'A Sample Weblogic 9 Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the weblogic 9 installation
    And I confirm the weblogic 9 execution environment creation
    Then I should see the 'Sample Weblogic 9' weblogic 9 execution environment element in the diagram



