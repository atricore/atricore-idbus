Feature: New JBoss Portal Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define the target jboss portal execution environment element

  Scenario: New local jboss portal execution environment
    Given I am on the modeler screen
    When I click on the JBoss Portal execution environment item from the palette
    And I see the new jboss portal execution environment form
    And I enter 'Sample JBoss Portal' as the identifier of the execution environment
    And I enter 'A Sample JBoss Portal Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the jboss portal installation
    And I confirm the jboss portal execution environment creation
    Then I should see the 'Sample JBoss Portal' jboss portal execution environment element in the diagram



