Feature: New OpenID Identity Provider Definition
  In order to enable inbound OpenID federation support for an identity provider to my identity appliance
  As an identity architect
  I want to create an OpenID identity provider definition

  Scenario: Create an external OpenID identity provider
    Given I am on the modeler screen
    And I drag the 'OpenID identity provider' item from the 'OpenID' drawer onto the diagram
    When I see the element creation form
    And I enter 'sample-openid-idp' as the identity provider name
    And I enter 'A Sample OpenID IdP' as the identity provider description
    When I confirm the creation submission
    Then I should see the OpenID identity provider element in the current diagram labeled as 'Sample IdP'

