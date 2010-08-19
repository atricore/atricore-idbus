Feature: New Tomcat 6 Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define to define the target tomcat 6 execution environment element

  Scenario: New local tomcat 6 execution environment
    Given I am on the modeler screen
    When I click on the Tomcat 6 execution environment item from the palette
    And I see the new tomcat 6 execution environment form
    And I enter 'Sample Tomcat 6' as the identifier of the execution environment
    And I enter 'A Sample Tomcat 6 Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the tomcat 6 installation
    And I confirm the tomcat 6 execution environment creation
    Then I should see the 'Sample Tomcat 6' tomcat 6 execution environment element in the diagram



