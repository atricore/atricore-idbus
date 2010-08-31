Feature: Change password of account with entitlements for accessing Administration Console
  In order to avoid unauthorized access to sensitive identity and access management information
  As an administrator console user
  I want to be able to change my password

  Scenario: Successful Password Change
    Given I have an existing session with the administration console
    And my password is 'my old password'
    When I click on a drop down with my full name
    And I click on the change password item
    And I enter 'my old password' as the old password
    And I enter 'my new password' as the new password
    And I click on the confirm button
    Then I should see a confirmation dialog informing that the password has been changed
    And my password should now be 'my new password'

  Scenario: Password Change with wrong or invalid old password
    Given I have an existing session with the administration console
    And my password is 'my old password'
    When I click on a drop down with my full name
    And I click on the change password item
    And I enter 'my invalid password' as the old password
    And I enter 'my new password' as the new password
    And I click on the confirm button
    Then I should see al alert informing that the old password is invalid
    And my password should be 'my old password'
