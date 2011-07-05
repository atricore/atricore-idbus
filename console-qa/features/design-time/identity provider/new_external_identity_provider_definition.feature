Feature: New External Identity Provider Definition
  In order to support external identity provider behavior in my identity appliance
  As an identity architect
  I want to create an external identity provider definition

Scenario: Create external identity provider with basic federated SSO capabilities
  Given I am on the modeler screen
  And I am seeing the external identity provider definition creation form
  When I enter 'idp1-external' as the identity provider name
  And I upload the External IdP SAML 2.0 metadata file
  When I confirm the creation submission
  Then I should see the external identity provider element in the current diagram labeled as 'idp1-external'
