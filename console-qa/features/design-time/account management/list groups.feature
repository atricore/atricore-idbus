Feature: List Groups
  In order to see what are the available entitlements for assigning to users
  As a system administrator
  I need to list the available groups

  Scenario: List Groups
    Given I am on the account management screen
    When I click on the list groups option
    Then I see the list of all groups