Feature: List User Accounts
  In order to know who is granted access to my system
  As a system administrator
  I want to list all the users

  Scenario: List User Accounts
    Given I am on the account management screen
    When I click on the list user accounts option
    Then I see the list of all users
    