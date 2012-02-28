package com.atricore.idbus.console.branding.ce {
import com.atricore.idbus.console.base.diagram.DiagramElementTypes;
import com.atricore.idbus.console.base.palette.PaletteItemProvider;
import com.atricore.idbus.console.base.palette.model.PaletteDrawer;
import com.atricore.idbus.console.base.palette.model.PaletteEntry;
import com.atricore.idbus.console.base.palette.model.PaletteRoot;

public class CEPaletteItemProvider implements PaletteItemProvider {

    public function CEPaletteItemProvider() {
    }

    public function getPalette():PaletteRoot {
        var pr:PaletteRoot  = new PaletteRoot("Identity Appliance Modeler Palette", null, null);

        // Entities drawer
        var entitiesPaletteDrawer:PaletteDrawer = new PaletteDrawer("Entities", null, null);

        entitiesPaletteDrawer.add(
                new PaletteEntry("Identity Provider", EmbeddedIcons.idpMiniIcon, "Identity Provider Entry", DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE)
        );
        entitiesPaletteDrawer.add(
                new PaletteEntry("Service Provider", EmbeddedIcons.spMiniIcon, "Service Provider Entry", DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE)
        );

        pr.add(entitiesPaletteDrawer);

        // SAML 2.0 drawer
        var saml2PaletteDrawer:PaletteDrawer = new PaletteDrawer("SAML 2.0", null, null);

        saml2PaletteDrawer.add(
                new PaletteEntry("Saml 2.0 Identity Provider", EmbeddedIcons.saml2IdpMiniIcon, "Saml 2.0 IdP Entry", DiagramElementTypes.SAML_2_IDENTITY_PROVIDER_ELEMENT_TYPE)
        );
        saml2PaletteDrawer.add(
                new PaletteEntry("Saml 2.0 Service Provider", EmbeddedIcons.saml2SpMiniIcon, "Saml 2.0 SP Entry", DiagramElementTypes.SAML_2_SERVICE_PROVIDER_ELEMENT_TYPE)
        );

        pr.add(saml2PaletteDrawer);

        // OAuth2 drawer
        var oauth2PaletteDrawer:PaletteDrawer = new PaletteDrawer("OAuth 2.0", null, null);

        oauth2PaletteDrawer.add(
                new PaletteEntry("OAuth 2.0 Identity Provider", EmbeddedIcons.oauth2IdpMiniIcon, "OAuth 2.0 IdP Entry", DiagramElementTypes.OAUTH_2_IDENTITY_PROVIDER_ELEMENT_TYPE)
        );
        oauth2PaletteDrawer.add(
                new PaletteEntry("OAuth 2.0 Service Provider", EmbeddedIcons.oauth2SpMiniIcon, "OAuth 2.0 SP Entry", DiagramElementTypes.OAUTH_2_SERVICE_PROVIDER_ELEMENT_TYPE)
        );

        pr.add(oauth2PaletteDrawer);

        // OpenID drawer
        var openidPaletteDrawer:PaletteDrawer = new PaletteDrawer("OpenID 2.0", null, null);

        openidPaletteDrawer.add(
                new PaletteEntry("OpenID 2.0 Identity Provider", EmbeddedIcons.openidIdpMiniIcon, "OpenID 2.0 IdP Entry", DiagramElementTypes.OPENID_IDENTITY_PROVIDER_ELEMENT_TYPE)
        );
        openidPaletteDrawer.add(
                new PaletteEntry("OpenID 2.0 Service Provider", EmbeddedIcons.openidSpMiniIcon, "OpenID 2.0 SP Entry", DiagramElementTypes.OPENID_SERVICE_PROVIDER_ELEMENT_TYPE)
        );

        pr.add(openidPaletteDrawer);

        // Authentication drawer
        var authenticationPaletteDrawer:PaletteDrawer = new PaletteDrawer("Authentication", null, null);
        authenticationPaletteDrawer.add(
                new PaletteEntry("Directory Service", EmbeddedIcons.directoryServiceMiniIcon, "Directory Service Entry", DiagramElementTypes.DIRECTORY_SERVICE_ELEMENT_TYPE)
        );

        pr.add(authenticationPaletteDrawer);

        // Identity Sources drawer
        var identitySourcesPaletteDrawer:PaletteDrawer = new PaletteDrawer("Identity Sources", null, null);
        identitySourcesPaletteDrawer.add(
                new PaletteEntry("Identity Vault", EmbeddedIcons.vaultMiniIcon, "Identity Vault Entry", DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE)
        );
        identitySourcesPaletteDrawer.add(
                new PaletteEntry("DB Identity Source", EmbeddedIcons.dbIdentitySourceMiniIcon, "DB Identity Source Entry", DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE)
        );
        identitySourcesPaletteDrawer.add(
                new PaletteEntry("LDAP Identity Source", EmbeddedIcons.ldapIdentitySourceMiniIcon, "LDAP Identity Source Entry", DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE)
        );
        identitySourcesPaletteDrawer.add(
                new PaletteEntry("XML Identity Source", EmbeddedIcons.xmlIdentitySourceMiniIcon, "XML Identity Source Entry", DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE)
        );

        pr.add(identitySourcesPaletteDrawer);

        // Resources drawer
        var resourcesPaletteDrawer:PaletteDrawer = new PaletteDrawer("Resources", null, null);
        resourcesPaletteDrawer.add(
                new PaletteEntry("JOSSO1 Resource", EmbeddedIcons.josso1ResourceMiniIcon, "JOSSO1 Resource Entry", DiagramElementTypes.JOSSO1_RESOURCE_ELEMENT_TYPE)
        );
        resourcesPaletteDrawer.add(
                new PaletteEntry("JOSSO2 Resource", EmbeddedIcons.josso2ResourceMiniIcon, "JOSSO2 Resource Entry", DiagramElementTypes.JOSSO2_RESOURCE_ELEMENT_TYPE)
        );

        pr.add(resourcesPaletteDrawer);

        // Execution Environments drawer
        var environmentsPaletteDrawer:PaletteDrawer = new PaletteDrawer("Execution Environments", null, null);

        environmentsPaletteDrawer.add(
                new PaletteEntry("Alfresco", EmbeddedIcons.alfrescoEnvironmentMiniIcon, "Alfresco Environment Entry", DiagramElementTypes.ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Apache", EmbeddedIcons.apacheEnvironmentMiniIcon, "Apache Environment Entry", DiagramElementTypes.APACHE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Java EE", EmbeddedIcons.javaEnvironmentMiniIcon, "Java EE Environment Entry", DiagramElementTypes.JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("JBoss", EmbeddedIcons.jbossEnvironmentMiniIcon, "JBoss Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("JBoss Portal", EmbeddedIcons.jbossEnvironmentMiniIcon, "JBoss Portal Environment Entry", DiagramElementTypes.JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Liferay Portal", EmbeddedIcons.liferayEnvironmentMiniIcon, "Liferay Portal Environment Entry", DiagramElementTypes.LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("PHP", EmbeddedIcons.phpEnvironmentMiniIcon, "PHP Environment Entry", DiagramElementTypes.PHP_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("PhpBB", EmbeddedIcons.phpbbEnvironmentMiniIcon, "PhpBB Environment Entry", DiagramElementTypes.PHPBB_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Tomcat", EmbeddedIcons.tomcatEnvironmentMiniIcon, "Tomcat Environment Entry", DiagramElementTypes.TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Webserver", EmbeddedIcons.webEnvironmentMiniIcon, "Webserver Environment Entry", DiagramElementTypes.WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Weblogic", EmbeddedIcons.weblogicEnvironmentMiniIcon, "Weblogic Environment Entry", DiagramElementTypes.WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Websphere CE", EmbeddedIcons.websphereEnvironmentMiniIcon, "Websphere CE Environment Entry", DiagramElementTypes.WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        environmentsPaletteDrawer.add(
                new PaletteEntry("Windows IIS", EmbeddedIcons.windowsEnvironmentMiniIcon, "Windows IIS Environment Entry", DiagramElementTypes.WINDOWS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
        );

        pr.add(environmentsPaletteDrawer);

        // Connections drawer
        var connectionPaletteDrawer:PaletteDrawer = new PaletteDrawer("Connections", null, null);
        connectionPaletteDrawer.add(
                new PaletteEntry("Federated Connection", EmbeddedIcons.connectionFederatedMiniIcon, "Federated Connection Entry", DiagramElementTypes.FEDERATED_CONNECTION_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Service Connection", EmbeddedIcons.connectionServiceMiniIcon, "Service Connection Entry", DiagramElementTypes.SERVICE_CONNECTION_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Activation", EmbeddedIcons.connectionActivationMiniIcon, "Activation Entry", DiagramElementTypes.ACTIVATION_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Identity Lookup", EmbeddedIcons.connectionIdentityLookupMiniIcon , "Identity Lookup Entry", DiagramElementTypes.IDENTITY_LOOKUP_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Identity Verification", EmbeddedIcons.connectionDelegatedAuthnMiniIcon , "Identity Verification Entry", DiagramElementTypes.DELEGATED_AUTHENTICATION_ELEMENT_TYPE)
        );
        
        pr.add(connectionPaletteDrawer);

        return pr;
    }
}
}