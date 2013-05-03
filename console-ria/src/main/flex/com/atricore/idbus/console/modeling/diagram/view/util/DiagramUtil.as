package com.atricore.idbus.console.modeling.diagram.view.util {

import com.atricore.idbus.console.base.diagram.DiagramElementTypes;
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.services.dto.AlfrescoResource;
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.CaptiveExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExternalOpenIDIdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalSaml2IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JBossEPPResource;
import com.atricore.idbus.console.services.dto.JBossPortalResource;
import com.atricore.idbus.console.services.dto.JOSSO1Resource;
import com.atricore.idbus.console.services.dto.JOSSO2Resource;
import com.atricore.idbus.console.services.dto.JbossExecutionEnvironment;
import com.atricore.idbus.console.services.dto.LiferayResource;
import com.atricore.idbus.console.services.dto.MicroStrategyResource;
import com.atricore.idbus.console.services.dto.OAuth2IdentityProvider;
import com.atricore.idbus.console.services.dto.OAuth2ServiceProvider;
import com.atricore.idbus.console.services.dto.PhpBBResource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ExternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.InternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.SasResource;
import com.atricore.idbus.console.services.dto.ServiceResource;
import com.atricore.idbus.console.services.dto.SharepointResource;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WebserverExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WindowsIISExecutionEnvironment;

import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class DiagramUtil {

    public function DiagramUtil() {
    }

    public static function nodesCanBeLinkedWithFederatedConnection(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id && !nodeLinkExists(node1.node, node2.node) && !nodeLinkExists(node2.node, node1.node)) {
            // TODO: finish this
            if ((node1.data is InternalSaml2ServiceProvider && (node2.data is IdentityProvider || node2.data is ExternalSaml2IdentityProvider
                            || node2.data is ExternalOpenIDIdentityProvider || node2.data is OAuth2IdentityProvider))
                    || (node1.data is IdentityProvider && (node2.data is InternalSaml2ServiceProvider || node2.data is ExternalSaml2ServiceProvider
                            || node2.data is OAuth2ServiceProvider))
                    || ((node1.data is ExternalSaml2ServiceProvider ||  node1.data is OAuth2ServiceProvider)
                            && node2.data is IdentityProvider)
                    || ((node1.data is ExternalSaml2IdentityProvider || node1.data is ExternalOpenIDIdentityProvider || node1.data is OAuth2IdentityProvider)
                            && node2.data is InternalSaml2ServiceProvider)) {
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithServiceConnection(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            if (node1.data is InternalSaml2ServiceProvider && node2.data is ServiceResource){
                var sp1:InternalSaml2ServiceProvider = node1.data as InternalSaml2ServiceProvider;
                if(sp1.serviceConnection == null){
                    canBeLinked = true;
                }
            } else if (node1.data is ServiceResource && node2.data is InternalSaml2ServiceProvider) {
                var sp2:InternalSaml2ServiceProvider = node2.data as InternalSaml2ServiceProvider;
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

            var resource:ServiceResource = null;
            var execEnv:ExecutionEnvironment = null;

            // ----------------------------------------------------
            // Make sure that we have an exec. env. and a resource:
            // ----------------------------------------------------

            if (node1.data is ServiceResource) {
                resource = ServiceResource(node1.data);
            } else if (node2.data is ServiceResource) {
                resource = ServiceResource(node2.data);
            } else {
                return false;
            }

            if (node1.data is ExecutionEnvironment) {
                execEnv = ExecutionEnvironment(node1.data);
            } else if (node2.data is ExecutionEnvironment) {
                execEnv = ExecutionEnvironment(node2.data);
            } else {
                return false;
            }

            // ---------------------------------------------------
            // Now, check valid resource/exec.env. combinations
            // ---------------------------------------------------

            // avoid associating an additional execution environment to a resource which already owns one with no
            // representation
            if (resource.activation != null && resource.activation.executionEnv is CaptiveExecutionEnvironment) {
                return false;
            }

            // JOSSO 2 Resources cannot be linked to execution environments (for now)
            if (resource is JOSSO2Resource) {
                return false;
            }

            // Sharepoint resources can only be linked to IIS
            if (resource is SharepointResource) {
                return false;
            }

            // Microstrategy only supports Tomcat
            if (resource is MicroStrategyResource) {
                return execEnv is TomcatExecutionEnvironment;
            }

            // SAS runs on ?????
            if (resource is SasResource) {
                return false;
            }

            // Alfresco runs in Tomcat/ JBoss ?!
            if (resource is AlfrescoResource) {
                return execEnv is TomcatExecutionEnvironment || execEnv is JbossExecutionEnvironment;
            }

            // JBoss portal runs only in JBoss
            if (resource is JBossPortalResource) {
                return execEnv is JbossExecutionEnvironment;
            }

            // PHP BB runs in generic web containers
            if (resource is PhpBBResource) {
                return execEnv is WebserverExecutionEnvironment;
            }

            // Liferay runs only in JBoss
            if (resource is LiferayResource) {
                return false;
            }

            if (resource is MicroStrategyResource) {
                return execEnv is TomcatExecutionEnvironment;
            }

            // JOSSO 1 Resources can be linked to any execution environment
            // (This should be the last on the list since some of the others extend it)
            if (resource is JOSSO1Resource) {
                return true;
            }



            // unknown resource type ?!?!?
            return false;

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
            case DiagramElementTypes.SAML_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.saml2SpMiniIcon;
            case DiagramElementTypes.EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.externalSaml2IdpMiniIcon;
            case DiagramElementTypes.EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.externalSaml2SpMiniIcon;
            case DiagramElementTypes.EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.externalOpenidIdpMiniIcon;
            case DiagramElementTypes.OAUTH_2_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.oauth2SpMiniIcon;
            case DiagramElementTypes.EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT_TYPE:
                return EmbeddedIcons.externalWsFedSpMiniIcon;
            case DiagramElementTypes.SALESFORCE_ELEMENT_TYPE:
                return EmbeddedIcons.salesforceSpMiniIcon;
            case DiagramElementTypes.GOOGLE_APPS_ELEMENT_TYPE:
                return EmbeddedIcons.googleSpMiniIcon;
            case DiagramElementTypes.SUGAR_CRM_ELEMENT_TYPE:
                return EmbeddedIcons.sugarCRMSpMiniIcon;
            case DiagramElementTypes.WIKID_ELEMENT_TYPE:
                return EmbeddedIcons.wikidAuthenticationServiceMiniIcon;
            case DiagramElementTypes.DIRECTORY_SERVICE_ELEMENT_TYPE:
                return EmbeddedIcons.directoryAuthenticationServiceMiniIcon;
            case DiagramElementTypes.WINDOWS_INTEGRATED_AUTHN_ELEMENT_TYPE:
                return EmbeddedIcons.windowsAuthenticationServiceMiniIcon;
            case DiagramElementTypes.DOMINO_ELEMENT_TYPE:
                return EmbeddedIcons.dominoAuthenticationServiceMiniIcon;
            case DiagramElementTypes.CLIENTCERT_ELEMENT_TYPE:
                return EmbeddedIcons.clientAuthenticationServiceMiniCertIcon;
            case DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE:
                return EmbeddedIcons.vaultMiniIcon;
            case DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.dbIdentitySourceMiniIcon;
            case DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.ldapIdentitySourceMiniIcon;
            case DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.xmlIdentitySourceMiniIcon;
            case DiagramElementTypes.JBOSSEPP_AUTHENTICATION_ELEMENT_TYPE:
                return EmbeddedIcons.jbosseppAuthenticationMiniIcon;
            case DiagramElementTypes.JOSSO1_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.josso1ResourceMiniIcon;
            case DiagramElementTypes.JOSSO2_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.josso2ResourceMiniIcon;
            case DiagramElementTypes.MICROSTRATEGY_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.microStrategyResourceMiniIcon;
            case DiagramElementTypes.SAS_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.sasResourceMiniIcon;
            case DiagramElementTypes.SHAREPOINT_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.sharepointResourceMiniIcon;
            case DiagramElementTypes.COLDFUSION_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.coldfusionResourceMiniIcon;
            case DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.jbossEnvironmentMiniIcon;
            case DiagramElementTypes.WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.weblogicEnvironmentMiniIcon;
            case DiagramElementTypes.TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.tomcatEnvironmentMiniIcon;
            case DiagramElementTypes.JBOSS_PORTAL_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.jbossPortalResourceMiniIcon;
            case DiagramElementTypes.LIFERAY_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.liferayResourceMiniIcon;
            case DiagramElementTypes.JBOSSEPP_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.jbosseppResourceMiniIcon;
            case DiagramElementTypes.SELFSERVICES_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.selfServicesResourceMiniIcon;
            case DiagramElementTypes.DOMINO_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.dominoResourceMiniIcon;
            case DiagramElementTypes.WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.websphereEnvironmentMiniIcon;
            case DiagramElementTypes.APACHE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.apacheEnvironmentMiniIcon;
            case DiagramElementTypes.WINDOWS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.windowsEnvironmentMiniIcon;
            case DiagramElementTypes.ALFRESCO_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.alfrescoResourceMiniIcon;
            case DiagramElementTypes.JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.javaEnvironmentMiniIcon;
            case DiagramElementTypes.PHP_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.phpEnvironmentMiniIcon;
            case DiagramElementTypes.PHPBB_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.phpbbResourceMiniIcon;
            case DiagramElementTypes.WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT_TYPE:
                return EmbeddedIcons.webEnvironmentMiniIcon;
            case DiagramElementTypes.SHAREPOINT_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.sharepointResourceMiniIcon;
            case DiagramElementTypes.COLDFUSION_RESOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.coldfusionResourceMiniIcon;
        }
        return null;
    }
}
}