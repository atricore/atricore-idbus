 Feature: New Ldap Identity Source Form
   In order to be able to determine the attributes of my identity source element
   As an identity architect
   I want to display the ldap identity source creation form

   Scenario: Create ldap identity source
     Given I am on the modeler screen
     When I drag the ldap identity source element from the palette to the diagram
     Then I should see the ldap identity source creation form
