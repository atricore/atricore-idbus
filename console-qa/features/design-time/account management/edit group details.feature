Feature: Edit Group Details
  In order to edit a group
  As a system administrator
  I need to change it's details

  Scenario: Update Group Details
    Given I am on the account management screen
    When I lookup a group 'Group One'
    And I update the name to 'Updated Group One'
    Then the group should be identified as 'Updated Group One'