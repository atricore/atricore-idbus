 Feature: New Identity Vault Form
   In order to be able to determine the attributes of my identity vault element
   As an identity architect
   I want to display the identity vault creation form

   Scenario: Create identity vault
     Given I am on the modeler screen
     When I drag the identity vault element from the palette to the diagram
     Then I should see the identity vault creation form
