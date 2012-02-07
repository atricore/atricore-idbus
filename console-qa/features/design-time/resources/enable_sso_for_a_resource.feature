Feature: Service a Resource with SSO
  In order to enable SSO support for an application
  As an identity architect
  I want to associate a resource with a service provider element

  Scenario: Service a Resource with SSO
    Given I am on the modeler screen
    And I see a Service Provider element
    And I see a Resource Element
    And I drag the 'Service' item from the 'Connections' drawer onto the diagram
    And I select the source resource element
    And I select the target service provider element
    Then I should see a service connection between the selected service proider and resource elements
