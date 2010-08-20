Feature: Dispose an Identity Appliance
  In order to dispose the identity services defined in my identity appliance
  As an identity architect
  I want to dispose an identity appliance

  Scenario: Successful disposal of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance' in the undeployed state
    When I select the dispose option on the selected identity appliance 'Sample Appliance'
    Then the selected appliance 'Sample Appliance' is disposed with no errors
    And the identity appliance 'Sample Appliance' transitions to the disposed state

  Scenario: Unsuccessful disposal of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance' in the undeployed state
    When I select the dispose option on the selected identity appliance 'Sample Appliance'
    Then a disposal error occurs
    And the disposal error is reported to the user

