package com.atricore.idbus.console.modeling.diagram.view.util {

import com.atricore.idbus.console.base.diagram.DiagramElementTypes;
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.OAuth2IdentityProvider;
import com.atricore.idbus.console.services.dto.OAuth2ServiceProvider;
import com.atricore.idbus.console.services.dto.OpenIDIdentityProvider;
import com.atricore.idbus.console.services.dto.OpenIDServiceProvider;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.Saml2IdentityProvider;
import com.atricore.idbus.console.services.dto.Saml2ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;

import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class DiagramUtil {

    public function DiagramUtil() {
    }

    public static function nodesCanBeLinkedWithFederatedConnection(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id && !nodeLinkExists(node1.node, node2.node) && !nodeLinkExists(node2.node, node1.node)) {
            // TODO: finish this
            if ((node1.data is ServiceProvider && (node2.data is IdentityProvider || node2.data is Saml2IdentityProvider
                            || node2.data is OpenIDIdentityProvider || node2.data is OAuth2IdentityProvider))
                    || (node1.data is IdentityProvider && (node2.data is ServiceProvider || node2.data is Saml2ServiceProvider
                            || node2.data is OpenIDServiceProvider || node2.data is OAuth2ServiceProvider))
                    || ((node1.data is Saml2ServiceProvider || node1.data is OpenIDServiceProvider || node1.data is OAuth2ServiceProvider)
                            && node2.data is IdentityProvider)
                    || ((node1.data is Saml2IdentityProvider || node1.data is OpenIDIdentityProvider || node1.data is OAuth2IdentityProvider)
                            && node2.data is ServiceProvider)) {
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithServiceConnection(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            if (node1.data is ServiceProvider && node2.data is ServiceResource){
                var sp1:ServiceProvider = node1.data as ServiceProvider;
                if(sp1.serviceConnection == null){
                    canBeLinked = true;
                }
            } else if (node1.data is ServiceResource && node2.data is ServiceProvider) {
                var sp2:ServiceProvider = node2.data as ServiceProvider;
                if(sp2.serviceConnection == null){
                    canBeLinked = true;
                }
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithActivation(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            if (node1.data is ServiceResource && node2.data is ExecutionEnvironment){
                var sr1:ServiceResource = node1.data as ServiceResource;
                if(sr1.activation == null){
                    canBeLinked = true;
                }
            } else if (node1.data is ExecutionEnvironment && node2.data is ServiceResource) {
                var sr2:ServiceResource = node2.data as ServiceResource;
                if(sr2.activation == null){
                    canBeLinked = true;
                }
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithIdentityLookup(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            if (node1.data is Provider && node2.data is IdentitySource){
                var prov1:Provider = node1.data as Provider;
                if(prov1.identityLookup == null){
                    canBeLinked = true;
                }
            } else if (node1.data is IdentitySource && node2.data is Provider) {
                var prov2:Provider = node2.data as Provider;
                if(prov2.identityLookup == null){
                    canBeLinked = true;
                }
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithDelegatedAuthentication(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id && !nodeLinkExists(node1.node, node2.node) && !nodeLinkExists(node2.node, node1.node)) {
            if (node1.data is IdentityProvider && node2.data is AuthenticationService) {
                canBeLinked = true;
            } else if (node1.data is AuthenticationService && node2.data is IdentityProvider) {
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }

    public static function nodeLinkExists(node1:INode, node2:INode):Boolean {
        if (node1 != null && node2 != null && node1.successors.indexOf(node2) != -1) {
            return true;
        }
        return false;
    }

    public static function getIconForElementType(elementType:int):Class {
        switch (elementType) {
            case DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.idpMiniIcon;
            case DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.spMiniIcon;
            case DiagramElementTypes.EXTERNAL_IDENTITY_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.externalIdpMiniIcon;
            case DiagramElementTypes.EXTERNAL_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.externalSpMiniIcon;
            case DiagramElementTypes.SAML_2_IDENTITY_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.saml2IdpMiniIcon;
            case DiagramElementTypes.SAML_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.saml2SpMiniIcon;
            case DiagramElementTypes.OPENID_IDENTITY_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.openidIdpMiniIcon;
            case DiagramElementTypes.OPENID_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.openidSpMiniIcon;
            case DiagramElementTypes.OAUTH_2_IDENTITY_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.oauth2IdpMiniIcon;
            case DiagramElementTypes.OAUTH_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.oauth2SpMiniIcon;
            case DiagramElementTypes.SALESFORCE_ELEMENT_TYPE:
                return EmbeddedIcons.salesforceMiniIcon;
            case DiagramElementTypes.GOOGLE_APPS_ELEMENT_TYPE:
                return EmbeddedIcons.googleAppsMiniIcon;
            case DiagramElementTypes.SUGAR_CRM_ELEMENT_TYPE:
                return EmbeddedIcons.sugarCRMMiniIcon;
            case DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE:
                return EmbeddedIcons.vaultMiniIcon;
            case DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.dbIdentitySourceMiniIcon;
            case DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.ldapIdentitySourceMiniIcon;
            case DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.xmlIdentitySourceMiniIcon;
            case DiagramElementTypes.JOSSO1_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.josso1ResourceMiniIcon;
            case DiagramElementTypes.JOSSO2_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.josso2ResourceMiniIcon;
            case DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.jbossEnvironmentMiniIcon;
            case DiagramElementTypes.WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.weblogicEnvironmentMiniIcon;
            case DiagramElementTypes.TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.tomcatEnvironmentMiniIcon;
            case DiagramElementTypes.JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.jbossEnvironmentMiniIcon;
            case DiagramElementTypes.LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.liferayEnvironmentMiniIcon;
            case DiagramElementTypes.WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.websphereEnvironmentMiniIcon;
            case DiagramElementTypes.APACHE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.apacheEnvironmentMiniIcon;
            case DiagramElementTypes.WINDOWS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.windowsEnvironmentMiniIcon;
            case DiagramElementTypes.ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.alfrescoEnvironmentMiniIcon;
            case DiagramElementTypes.JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.javaEnvironmentMiniIcon;
            case DiagramElementTypes.PHP_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.phpEnvironmentMiniIcon;
            case DiagramElementTypes.PHPBB_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.phpbbEnvironmentMiniIcon;
            case DiagramElementTypes.WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.webEnvironmentMiniIcon;
            case DiagramElementTypes.SHAREPOINT2010_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.sharepoint2010EnvironmentMiniIcon;
            case DiagramElementTypes.COLDFUSION_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.coldfusionEnvironmentMiniIcon;
            case DiagramElementTypes.WIKID_ELEMENT_TYPE:
                return EmbeddedIcons.wikidMiniIcon;
            case DiagramElementTypes.DIRECTORY_SERVICE_ELEMENT_TYPE:
                return EmbeddedIcons.directoryServiceMiniIcon;
            case DiagramElementTypes.WINDOWS_INTEGRATED_AUTHN_ELEMENT_TYPE:
                return EmbeddedIcons.windowsIntegratedAuthnMiniIcon;
        }
        return null;
    }
}
}