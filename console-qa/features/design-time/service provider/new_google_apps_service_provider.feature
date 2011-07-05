Feature: New Google Apps SAML Service Provider Definition
  In order to support cloud based service provider behavior in my identity appliance
  As an identity architect
  I want to create a cloud service provider definition

Scenario: Create Google Apps SAML service
  Given I am on the modeler screen
  And I am seeing the Google Apps service provider definition creation form
  When I enter 'google-apps' as the identity provider name
  And I enter my Google Apps domain
  When I confirm the creation submission
  Then I should see the Google Apps service provider element in the current diagram labeled as 'google-apps'

