Feature: New External Non-SAML Identity Provider Definition
  In order to support non-SAML external identity provider behavior in my identity appliance
  As an identity architect
  I want to create an non-SAML external identity provider definition

Scenario: Create an OpenID external identity provider
  Given I am on the modeler screen
  And I am seeing the external identity provider definition creation form
  When I enter 'openid-idp' as the identity provider name
  When I confirm the creation submission
  Then I should see the external OpenID identity provider element in the current diagram labeled as 'openid-idp'
