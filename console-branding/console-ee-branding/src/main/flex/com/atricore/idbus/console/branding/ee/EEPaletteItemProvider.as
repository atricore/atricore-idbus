package com.atricore.idbus.console.branding.ee {
import com.atricore.idbus.console.base.diagram.DiagramElementTypes;
import com.atricore.idbus.console.base.palette.PaletteItemProvider;
import com.atricore.idbus.console.base.palette.model.PaletteDrawer;
import com.atricore.idbus.console.base.palette.model.PaletteEntry;
import com.atricore.idbus.console.base.palette.model.PaletteRoot;

public class EEPaletteItemProvider implements PaletteItemProvider {

    public function EEPaletteItemProvider() {
    }

    public function getPalette():PaletteRoot {
        var pr:PaletteRoot  = new PaletteRoot("Identity Appliance Modeler Palette", null, null);

        // Entities drawer
        var providersPaletteDrawer:PaletteDrawer = new PaletteDrawer("Providers", null, null);

        providersPaletteDrawer.add(
                new PaletteEntry("Identity Provider", EmbeddedIcons.idpMiniIcon, "Identity Provider Entry", DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE)
        );
        providersPaletteDrawer.add(
                new PaletteEntry("SAML2 Service Provider", EmbeddedIcons.saml2SpMiniIcon, "Service Provider Entry", DiagramElementTypes.SAML_2_SERVICE_PROVIDER_ELEMENT_TYPE)
        );
        providersPaletteDrawer.add(
                new PaletteEntry("External SAML2 Identity Provider", EmbeddedIcons.externalSaml2IdpMiniIcon, "External Identity Provider Entry", DiagramElementTypes.EXTERNAL_IDENTITY_PROVIDER_ELEMENT_TYPE)
        );
        providersPaletteDrawer.add(
                new PaletteEntry("External SAML2 Service Provider", EmbeddedIcons.externalSaml2SpMiniIcon, "External Service Provider Entry", DiagramElementTypes.EXTERNAL_SERVICE_PROVIDER_ELEMENT_TYPE)
        );
        providersPaletteDrawer.add(
                new PaletteEntry("External OpenID Identity Provider", EmbeddedIcons.externalOpenidIdpMiniIcon, "External OpenID Identity Provider Entry", DiagramElementTypes.EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT_TYPE)
        );
        providersPaletteDrawer.add(
                new PaletteEntry("OAuth Service Provider", EmbeddedIcons.oauth2SpMiniIcon, "OAuth Service Provider Entry", DiagramElementTypes.OAUTH_2_SERVICE_PROVIDER_ELEMENT_TYPE)
        );
        providersPaletteDrawer.add(
                new PaletteEntry("External WS-Federation Service Provider", EmbeddedIcons.externalWsFedSpMiniIcon, "External WS-Federation Service Provider Entry", DiagramElementTypes.EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT_TYPE)
        );


        pr.add(providersPaletteDrawer);

        // Cloud Entities drawer
        var cloudPaletteDrawer:PaletteDrawer = new PaletteDrawer("Cloud Providers", null, null);

        cloudPaletteDrawer.add(
                new PaletteEntry("Salesforce", EmbeddedIcons.salesforceSpMiniIcon, "Salesforce Entry", DiagramElementTypes.SALESFORCE_ELEMENT_TYPE)
        );
        cloudPaletteDrawer.add(
                new PaletteEntry("Google Apps", EmbeddedIcons.googleSpMiniIcon, "Google Apps Entry", DiagramElementTypes.GOOGLE_APPS_ELEMENT_TYPE)
        );
        cloudPaletteDrawer.add(
                new PaletteEntry("SugarCRM", EmbeddedIcons.sugarCRMSpMiniIcon, "SugarCRM Entry", DiagramElementTypes.SUGAR_CRM_ELEMENT_TYPE)
        );

        pr.add(cloudPaletteDrawer);

        // Authentication drawer
        var authenticationPaletteDrawer:PaletteDrawer = new PaletteDrawer("Authentication", null, null);
        authenticationPaletteDrawer.add(
                new PaletteEntry("WiKID 2FA", EmbeddedIcons.wikidAuthenticationServiceMiniIcon, "WiKID 2FA Entry", DiagramElementTypes.WIKID_ELEMENT_TYPE)
        );
        authenticationPaletteDrawer.add(
                new PaletteEntry("Directory Service", EmbeddedIcons.directoryAuthenticationServiceMiniIcon, "Directory Service Entry", DiagramElementTypes.DIRECTORY_SERVICE_ELEMENT_TYPE)
        );
        authenticationPaletteDrawer.add(
                new PaletteEntry("Windows Domain", EmbeddedIcons.windowsAuthenticationServiceMiniIcon, "Windows Domain Entry", DiagramElementTypes.WINDOWS_INTEGRATED_AUTHN_ELEMENT_TYPE)
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
        resourcesPaletteDrawer.add(
                new PaletteEntry("Sharepoint", EmbeddedIcons.sharepointResourceMiniIcon, "Sharepoint 2010", DiagramElementTypes.SHAREPOINT_RESOURCE_ELEMENT_TYPE)
        );
        resourcesPaletteDrawer.add(
                new PaletteEntry("Microstrategy Web", EmbeddedIcons.microStrategyResourceMiniIcon, "Microstrategy Web Entry", DiagramElementTypes.MICROSTRATEGY_RESOURCE_ELEMENT_TYPE)
        );
        resourcesPaletteDrawer.add(
                new PaletteEntry("SAS", EmbeddedIcons.sasResourceMiniIcon, "SAS", DiagramElementTypes.SAS_RESOURCE_ELEMENT_TYPE)
        );
        resourcesPaletteDrawer.add(
                new PaletteEntry("Alfresco", EmbeddedIcons.alfrescoResourceMiniIcon, "Alfresco Environment Entry", DiagramElementTypes.ALFRESCO_RESOURCE_ELEMENT_TYPE)
        );
        resourcesPaletteDrawer.add(
                new PaletteEntry("JBoss Portal", EmbeddedIcons.jbossPortalEnvironmentMiniIcon, "JBoss Portal Environment Entry", DiagramElementTypes.JBOSS_PORTAL_RESOURCE_ELEMENT_TYPE)
        );
        resourcesPaletteDrawer.add(
                new PaletteEntry("Liferay Portal", EmbeddedIcons.liferayResourceMiniIcon, "Liferay Portal Environment Entry", DiagramElementTypes.LIFERAY_RESOURCE_ELEMENT_TYPE)
        );
        resourcesPaletteDrawer.add(
                new PaletteEntry("PhpBB", EmbeddedIcons.phpbbResourceMiniIcon, "PhpBB Environment Entry", DiagramElementTypes.PHPBB_RESOURCE_ELEMENT_TYPE)
        );

        pr.add(resourcesPaletteDrawer);

        // Execution Environments drawer
        var environmentsPaletteDrawer:PaletteDrawer = new PaletteDrawer("Execution Environments", null, null);

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
                new PaletteEntry("PHP", EmbeddedIcons.phpEnvironmentMiniIcon, "PHP Environment Entry", DiagramElementTypes.PHP_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)
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
                new PaletteEntry("Federated Connection", EmbeddedIcons.federatedConnectionMiniIcon, "Federated Connection Entry", DiagramElementTypes.FEDERATED_CONNECTION_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Service Connection", EmbeddedIcons.serviceConnectionMiniIcon, "Service Connection Entry", DiagramElementTypes.SERVICE_CONNECTION_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Activation", EmbeddedIcons.activationMiniIcon, "Activation Entry", DiagramElementTypes.ACTIVATION_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Identity Lookup", EmbeddedIcons.identityLookupMiniIcon , "Identity Lookup Entry", DiagramElementTypes.IDENTITY_LOOKUP_ELEMENT_TYPE)
        );

        connectionPaletteDrawer.add(
                new PaletteEntry("Identity Verification", EmbeddedIcons.identityVerificationMiniIcon , "Identity Verification Entry", DiagramElementTypes.DELEGATED_AUTHENTICATION_ELEMENT_TYPE)
        );

        pr.add(connectionPaletteDrawer);

        return pr;
    }
}
}