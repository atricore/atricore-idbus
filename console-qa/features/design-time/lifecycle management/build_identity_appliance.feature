Feature: Build an Identity Appliance
  In order to be able to run the identity services defined in my identity appliance
  As an identity architect
  I want to build an identity appliance

  Scenario: Successful build of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance'
    When I select the build option on the selected identity appliance 'Sample Appliance'
    Then the selected appliance 'Sample Appliance' is built with no errors
    And the identity appliance 'Sample Appliance' transitions to the built state

  Scenario: Unsuccessful build of an identity appliance
    Given I am on the lifecycle screen
    And I have one selected identity appliance 'Sample Appliance' which is not error-free
    When I select the build option on the selected identity appliance 'Sample Appliance'
    Then a build error occurs
    And the build error is reported to the user

