Feature: New JBoss 5 Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define the target jboss 5 execution environment element

  Scenario: New local jboss 5 execution environment
    Given I am on the modeler screen
    When I click on the JBoss 5 execution environment item from the palette
    And I see the new jboss 5 execution environment form
    And I enter 'Sample JBoss 5' as the identifier of the execution environment
    And I enter 'A Sample JBoss 5 Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the jboss 5 installation
    And I confirm the jboss 5 execution environment creation
    Then I should see the 'Sample JBoss 5' jboss 5 execution environment element in the diagram



