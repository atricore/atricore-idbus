Feature: Edit Identity Provider Definition
  In order to alter identity provider behavior in my identity appliance
  As an identity architect
  I want to edit an identity provider definition

  Scenario: Change the identifier of the identity provider
    Given I am on the modeler screen
    And the name of the identity provider I wish to update is 'Sample IdP'
    And I am seeing the identity provider definition editing form
    When I enter 'Updated Sample IdP' as the identity provider name
    And I confirm the editing submission
    Then I should see the identity provider element in the current diagram labeled as 'Updated Sample IdP'

  Scenario: Enable SSO and SLO

  Scenario: Enable SSO and SLO

