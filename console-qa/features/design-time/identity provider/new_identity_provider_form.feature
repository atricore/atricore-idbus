Feature: New Identity Provider Form
  In order to be able to determine the attributes of my identity provider elemenet
  As an identity architect
  I want to display the identity provider creation form

  Scenario: Create identity provider
    Given I am on the modeler screen
    And I my diagram contains an identity appliance element
    When I select the identity appliance element from the diagram
    And I click on the identity provider element from the palette
    Then I should see the identity provider definition creation form

  Scenario: Create identity provider without selecting host identity appliance
    Given I am on the modeler screen
    And I my diagram contains an identity appliance element
    When I click on the identity appliance element in the diagram
    Then I should see an alert showing that a I should select the identity appliance I wish to use as the host
