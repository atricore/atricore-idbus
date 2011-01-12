Feature: New Salesforce SAML Service Provider Definition
  In order to support cloud based service provider behavior in my identity appliance
  As an identity architect
  I want to create a Salesforce service provider definition

Scenario: Create Salesforce SAML service provider
  Given I am on the modeler screen
  And I am seeing the Salesforce service provider definition creation form
  When I enter 'salesforce' as the identity provider name
  When I confirm the creation submission
  Then I should see the Salesforce service provider element in the current diagram labeled as 'salesforce'
