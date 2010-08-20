Feature: Group Deprovisioning
  In order to disposed invalid or unreferenced entitlements
  As a system administrator
  I need to remove a group

  Scenario: Deprovision Group
    Given I am on the account management screen
    When I lookup a group 'Group One'
    And I remove group 'Group One'
    Then when I list all the groups, group 'Group One' should not be included