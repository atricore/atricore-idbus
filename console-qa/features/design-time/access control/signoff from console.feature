Feature: Sign-off from Administration Console
  In order close my existing session with the administration console
  As an identity architect
  I need to sign-off

  Scenario: Sign-off with an existing session
    Given I have an existing session with the administration console
    When I click on the sign-off button
    Then I should be redirected to the splash screen 
    And I should see the login form

  Scenario: Sign-off with a stale session
    Given I have a stale session with the administration console
    When I attempt to perform any action
    Then I should be redirected to the splash screen
    And I should see the login form
