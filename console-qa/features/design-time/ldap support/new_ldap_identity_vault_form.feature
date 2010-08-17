 Feature: New Ldap Identity Vault Form
   In order to be able to determine the attributes of my identity vault element
   As an identity architect
   I want to display the ldap identity vault creation form

   Scenario: Create ldap identity vault
     Given I am on the modeler screen
     When I drag the ldap identity vault element from the palette to the diagram
     Then I should see the ldap identity vault creation form
