Feature: Enable SAML2 for an external identity provider facing an OpenID identity provider
  In order to enable SAML2 support for an external identity provider facing an OpenID identity provider
  As an identity architect
  I want to setup the SAML2 proxy of the external identity provider proxying the OpenID Identity Provider

  Scenario: Enable SAML2 for an external identity provider facing an OpenID identity provider
    Given I am on the modeler screen
    And I'm seeing an OpenID identity provider
    And I'm seeing an external identity provider
    And I'm seeing an internal SAML2 service provider
    When I select the external identity provider
    And I click on the 'SAML2' tab item
    Then I should see the form for setting up a SAML2 identity provider