Feature: Clear local repository data
    In Order reset the repositories information
    As a System Administrator
    I Want clear the local repository data

Scenario: Manually check for new updates
   Given I am on the LiveUpdate setup screen
   And I am seeing the 'Available Updates' list
   When I select 'Cleanup repositories cache'
   Then I should be able reload all repository content