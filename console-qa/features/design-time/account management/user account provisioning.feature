Feature: Provision User Account
  In order to allow authorized users to access the service providers of my organization's circle of trust
  As a system administrator
  I need to provision a user account

  Scenario: Successful provisioning of a user account
    Given I am on the account management screen
    When I click on the create user option
    And set the name of the user to 'User One'
    Then when a list my users, 'User One' is included
