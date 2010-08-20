Feature: Undeploy an Identity Appliance
  In order to dispose the identity services defined in my identity appliance
  As an identity architect
  I want to undeploy an identity appliance

  Scenario: Successful undeployment of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance' in the deployed state
    When I select the undeploy option on the selected identity appliance 'Sample Appliance'
    Then the selected appliance 'Sample Appliance' is undeployed with no errors
    And the identity appliance 'Sample Appliance' transitions to the undeployed state

  Scenario: Unsuccessful undeployment of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance' in the deployed state
    When I select the undeploy option on the selected identity appliance 'Sample Appliance'
    Then a undeployment error occurs
    And the undeployment error is reported to the user

