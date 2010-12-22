Feature: Install New Updates
    In Order upgrade my product to a newer version
    As a System Administrator
    I Want install a new set of updates

Scenario: Successfull install of new updates
   Given I am on the LiveUpdate setup screen
   And I am seeing the 'Available Updates' list
   When I select 'Update Product' option
   Then the selected updates will start installing without errors

Scenario: Unuccessfull install of new updates
   Given I am on the LiveUpdate setup screen
   And I am seeing the 'Available Updates' list
   When I select 'Update Product' option
   Then the selected updates will fail to install
   And I should be able to see an error report