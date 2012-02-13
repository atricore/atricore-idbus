Feature: Establish a Service Connection between a local service provider and a resource
  In order to connect a service provider with a resource so that one trusts the other
  As an identity architect
  I want to connect the source local service provider with the target resource

  Scenario: Connect a local service provider with a resource
    Given I am on the modeler screen
    And my diagram contains a local service provider 'Sample Local SP'
    And my diagram contains a resource 'Sample Resource'
    When I click on the Service Connection item from the 'Connections" drawer
    And I select the source 'Sample SP' service provider element
    And I select the target 'Sample Resource' resource element
    And I see the new service connection form
    And I enter 'Sample Connection Name' as the identifier of the connection
    And I enter 'Sample Connection Description' as the description of the connection
    And I confirm the service connection creation submission
    Then I should see the federated connection between the service provider 'Sample Local SP' and 'Sample Resource'
