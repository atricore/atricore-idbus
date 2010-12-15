Feature: Upgrade EE Product License
    In Order to update my product's license
    As a System Administrator
    I Want to activate a new license

Scenario: Upgrade JOSSO EE Distribution license
   Given I am on the settings screen of EE product
   And I am seeing the 'Activate JOSSO EE' form
   When I upload the new license file
   And I confirm the Activation process
   Then I should be able to see the updated licese information