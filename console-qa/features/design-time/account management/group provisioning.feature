Feature: Group Provisioning
  In order to set the entitlements for a user
  As a system administrator
  I need to create a group

  Scenario: Provision Group
    Given I am on the account management screen
    When I open the group creation screen
    And I enter 'Group One' as the identifier
    And I enter 'Group One Description' as the description
    Then when I list all the groups, group 'Group One' should be included
