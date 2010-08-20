Feature: Authenticated Access to Administration Console
  In order to manage my identity management deployments
  As an identity architect
  I must authenticate

  Scenario: Authenticate using the default credential set
    Given I access the administration console url
    When I enter 'admin' as the user name
    And I enter 'admin' as the password
    And I click on the sign-on button
    Then I should see the modeler screen of the console
