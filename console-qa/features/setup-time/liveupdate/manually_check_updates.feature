Feature: Manually Check for New Updates
    In Order refresh the list of available updates
    As a System Administrator
    I Want to check for new updates available in configured repositories

Scenario: Successfull check for new updates
   Given I am on the LiveUpdate setup screen
   And I am seeing the 'Available Updates' list
   When I select 'Check for Updates'
   Then I should be able to see the new updates in the list

Scenario: Unsuccessfull check for new updates
   Given I am on the LiveUpdate setup screen
   And I am seeing the 'Available Updates' list
   When I select 'Check for Updates'
   Then I should be able to see an error report with the update failure information
