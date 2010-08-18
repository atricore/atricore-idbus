Feature: Edit Identity Provider Form
  In order to be able to change the attributes of my identity provider element
  As an identity architect
  I want to display the identity provider editing form

  Scenario: Edit identity provider
    Given I am on the modeler screen
    And I my diagram contains an identity provider element
    When I select the identity identity provider element from the diagram
    Then I should see the identity provider editing form

