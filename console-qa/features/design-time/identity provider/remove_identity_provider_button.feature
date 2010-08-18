Feature: Remove Identity Provider Button
  In order to be able to disable identity provider-specific behaviour
  As an identity architect
  I want to be presented with the option to remove an identity provider element

  Scenario: Remove Identity provider with no incoming nor outgoing associations
    Given I am on the modeler screen
    And I my diagram contains an identity provider element
    When I roll over the identity identity provider element from the diagram
    Then I should see a button for removing the identity provider element

  Scenario: Remove Identity provider with incoming associations
    Given I am on the modeler screen
    And I my diagram contains an identity provider element
    And the identity provider element I wish to remove contains incoming associations
    When I roll over the identity identity provider element from the diagram
    Then I should not see a button for removing the identity provider element
