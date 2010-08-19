Feature: New XML Identity Source Definition
  In order to setup an XML identity source
  As an identity architect
  I want to create an XML identity source definition

  Scenario: Create an XML identity source definition
    Given I am on the modeler screen
    And I drag the xml identity source element from the palette to the diagram
    And I should see the xml identity source creation form
    When I enter 'Sample XML Source' as the XML identity source name
    And I enter 'A Sample XML Source' as the XML identity source description
    And I enter '/opt/idam/data/credentials.xml' as the credential document path
    And I enter '/opt/idam/data/users.xml' as the users document path
    When I confirm the XML identity source creation submission
    Then I should see the XML identity source element in the current diagram labeled as 'Sample XML Source'

