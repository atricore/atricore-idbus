Feature: Determine which identity source a provider must rely on
  In order to set the identity source for a local provider
  As an identity architect
  I want to connect a source local provider with a target identity source

  Scenario: Associate a local identity provider with an identity source
    Given I am on the modeler screen
    And a local identity provider 'Sample IdP' exists in my diagram
    And an identity source 'Sample Identity Source' exists in my diagram
    When I drag the identity lookup item from the palette to the diagram
    And I set the 'Sample IdP' identity provider element as the source
    And I set the 'Sample Identity Source' identity source element as the target
    Then I should see an edge connecting the 'Sample IdP' element with the 'Sample Identity Source' element

  Scenario: Associate a local service provider with an identity source
    Given I am on the modeler screen
    And a local service provider 'Sample SP' exists in my diagram
    And an identity source 'Sample Identity Source' exists in my diagram
    When I drag the identity lookup item from the palette to the diagram
    And I set the 'Sample IdP' identity provider element as the source
    And I set the 'Sample Identity Source' identity source element as the target
    Then I should see an edge connecting the 'Sample IdP' element with the 'Sample Identity Source' element
