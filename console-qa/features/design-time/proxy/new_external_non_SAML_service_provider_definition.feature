Feature: New External Non-SAML Service Provider Definition
  In order to support non-SAML external service provider behavior in my identity appliance
  As an identity architect
  I want to create an non-SAML external service provider definition

Scenario: Create an OpenID external service provider
  Given I am on the modeler screen
  And I am seeing the external service provider definition creation form
  When I enter 'openid-sp' as the identity provider name
  When I confirm the creation submission
  Then I should see the external OpenID service provider element in the current diagram labeled as 'openid-sp'
