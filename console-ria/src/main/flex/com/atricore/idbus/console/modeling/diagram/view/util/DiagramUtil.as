package com.atricore.idbus.console.modeling.diagram.view.util {
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.base.diagram.DiagramElementTypes;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExternalIdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalServiceProvider;
import com.atricore.idbus.console.services.dto.IdentityProvider;

import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class DiagramUtil {

    public function DiagramUtil() {
    }

    public static function nodesCanBeLinkedWithFederatedConnection(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id && !nodeLinkExists(node1.node, node2.node) && !nodeLinkExists(node2.node, node1.node)) {
            // TODO: finish this
            if ((node1.data is ServiceProvider && (node2.data is IdentityProvider || node2.data is ExternalIdentityProvider))
                    || (node1.data is IdentityProvider && (node2.data is ServiceProvider || node2.data is ExternalServiceProvider))
                    || (node1.data is ExternalServiceProvider && node2.data is IdentityProvider)
                    || (node1.data is ExternalIdentityProvider && node2.data is ServiceProvider)) {
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithActivation(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            if (node1.data is ServiceProvider && node2.data is ExecutionEnvironment){
                var sp1:ServiceProvider = node1.data as ServiceProvider;
                if(sp1.activation == null){
                    canBeLinked = true;
                }
            } else if (node1.data is ExecutionEnvironment && node2.data is ServiceProvider) {
                var sp2:ServiceProvider = node2.data as ServiceProvider;
                if(sp2.activation == null){
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
            case DiagramElementTypes.SALESFORCE_ELEMENT_TYPE:
                return EmbeddedIcons.salesforceMiniIcon;
            case DiagramElementTypes.GOOGLE_APPS_ELEMENT_TYPE:
                return EmbeddedIcons.googleAppsMiniIcon;
            case DiagramElementTypes.IDENTITY_VAULT_ELEMENT_TYPE:
                return EmbeddedIcons.vaultMiniIcon;
            case DiagramElementTypes.DB_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.dbIdentitySourceMiniIcon;
            case DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.ldapIdentitySourceMiniIcon;
            case DiagramElementTypes.XML_IDENTITY_SOURCE_ELEMENT_TYPE:
                return EmbeddedIcons.xmlIdentitySourceMiniIcon;
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
        }
        return null;
    }
}
}