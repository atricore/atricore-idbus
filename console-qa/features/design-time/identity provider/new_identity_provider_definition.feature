Feature: New Identity Provider Definition
  In order to support identity provider behavior in my identity appliance
  As an identity architect
  I want to create an identity provider definition

  Scenario: Create identity provider with basic federated SSO capabilities
    Given I am on the modeler screen
    And I am seeing the identity provider definition creation form
    When I enter 'Sample IdP' as the identity provider name
    And I enter 'A Sample IdP' as the identity provider description
    And I enter 'http://localhost' as the identity provider location
    And I enter 8081 as the identity provider port
    And I enable SSO SAML profile
    And I enable SLO SAML profile
    And I select the default user information lookup mechanism
    And I select the default authentication contract
    And I select the default authentication mechanism
    And I select the default authentication assertion emission policy
    When I confirm the creation submission
    Then I should see the identity provider element in the current diagram labeled as 'Sample IdP'
