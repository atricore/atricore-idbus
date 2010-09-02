Feature: New Liferay Portal 5 Execution Environment
  In order to have a candidate execution environment available for activating it against a service provider
  As an identity architect
  I want to define the target liferay portal 5 execution environment element

  Scenario: New local liferay portal 5 execution environment
    Given I am on the modeler screen
    When I click on the Liferay Portal 5 execution environment item from the palette
    And I see the new liferay portal 5 execution environment form
    And I enter 'Sample Liferay Portal 5' as the identifier of the execution environment
    And I enter 'A Sample Liferay Portal 5 Execution Environment' as the description of the execution environment
    And I see the selected host as 'Local' without being able to change it
    And I enter the path for the home directory running of the liferay portal 5 installation
    And I confirm the liferay portal 5 execution environment creation
    Then I should see the 'Sample Liferay Portal 5' liferay portal 5 execution environment element in the diagram



