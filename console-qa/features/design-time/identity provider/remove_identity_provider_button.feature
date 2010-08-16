Feature: Remove Identity Provider
  In order to be able to disable identity provider-specific behaviour
  As an identity architect
  I want to be remove an identity provider element

  Scenario: Remove Identity provider
    Given I am on the modeler screen
    And I my diagram contains an identity provider element identified as 'My IdP'
    And I see the element removal button over the identity provider element I wish to remove
    When I click on the removal button of the identity provider element  
    Then the identity provider 'My IdP' should be disposed from the diagram

