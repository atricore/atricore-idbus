Feature: New JBoss 4 Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define the target jboss 4 execution environment element

  Scenario: New local jboss 4 execution environment
    Given I am on the modeler screen
    When I click on the JBoss 4 execution environment item from the palette
    And I see the new jboss 4 execution environment form
    And I enter 'Sample JBoss 4' as the identifier of the execution environment
    And I enter 'A Sample JBoss 4 Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the jboss 4 installation
    And I confirm the jboss 4 execution environment creation
    Then I should see the 'Sample JBoss 4' jboss 4 execution environment element in the diagram



