Feature: Deprovision user
  In order to avoid unauthorized access to the service providers of my circle of trust
  As a system administrator
  I want to deprovision a user

  Scenario: Deprovision user
    Given I a on the account management screen
    When I click on the search users option
    And I select the user 'User One' which I want to deprovision
    And I click on the deprovision button
    Then when listing my users, 'User One' is not included