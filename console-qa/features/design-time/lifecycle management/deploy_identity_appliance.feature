Feature: Deploy an Identity Appliance
  In order to run the identity services defined in my identity appliance
  As an identity architect
  I want to deploy an identity appliance

  Scenario: Successful deployment of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance' in the built state
    When I select the deploy option on the selected identity appliance 'Sample Appliance'
    Then the selected appliance 'Sample Appliance' is deployed with no errors
    And the identity appliance 'Sample Appliance' transitions to the deployed state

  Scenario: Unsuccessful deployment of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance'
    When I select the deploy option on the selected identity appliance 'Sample Appliance'
    Then a deployment error occurs
    And the deployment error is reported to the user

