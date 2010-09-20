/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.modeling.palette {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.diagram.DiagramElementTypes;
import com.atricore.idbus.console.modeling.palette.event.PaletteEvent;
import com.atricore.idbus.console.modeling.palette.model.PaletteDrawer;
import com.atricore.idbus.console.modeling.palette.model.PaletteEntry;
import com.atricore.idbus.console.modeling.palette.model.PaletteRoot;

import mx.core.UIComponent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.supportClasses.ItemRenderer;

public class PaletteMediator extends IocMediator {
    private var selectedIndex:int;
    private var selectedItem:Object;
    public static const DESELECT_PALETTE_ELEMENT:String = "deselectPaletteElement";

    private var _projectProxy:ProjectProxy;

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }
    
    public function PaletteMediator(name : String = null, viewComp:PaletteView = null) {
        super(name, viewComp);


    }

    override public function setViewComponent(viewComponent:Object):void {

        if (getViewComponent() != null) {
            view.removeEventListener(PaletteEvent.CLICK, handlePaletteClick);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {

        // bind view to palette model
        var saml2PaletteDrawer:PaletteDrawer = new PaletteDrawer("Internet SSO (SAML2)", null, null);

        saml2PaletteDrawer.add(
                    new PaletteEntry("Identity Provider", EmbeddedIcons.idpMiniIcon, "Identity Provider Entry", DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE)

                );
        saml2PaletteDrawer.add(
                    new PaletteEntry("Service Provider", EmbeddedIcons.spMiniIcon, "Service Provider Entry", DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE)

                );

        var pr:PaletteRoot  = new PaletteRoot("Identity Appliance Modeler Palette", null, null);
        pr.add(saml2PaletteDrawer);

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
                    new PaletteEntry("Windows", EmbeddedIcons.windowsEnvironmentMiniIcon, "Windows Environment Entry", DiagramElementTypes.WINDOWS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        pr.add(environmentsPaletteDrawer);

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

        var connectionPaletteDrawer:PaletteDrawer = new PaletteDrawer("Connections", null, null);
        connectionPaletteDrawer.add(
                    new PaletteEntry("Federated Connection", EmbeddedIcons.connectionMiniIcon, "Federated Connection Entry", DiagramElementTypes.FEDERATED_CONNECTION_ELEMENT_TYPE)

                );
        connectionPaletteDrawer.add(
                    new PaletteEntry("Activation", EmbeddedIcons.connectionMiniIcon, "Activation Entry", DiagramElementTypes.ACTIVATION_ELEMENT_TYPE)

                );
        connectionPaletteDrawer.add(
                    new PaletteEntry("Identity Lookup", EmbeddedIcons.connectionMiniIcon, "Identity Lookup Entry", DiagramElementTypes.IDENTITY_LOOKUP_ELEMENT_TYPE)

                );
        pr.add(connectionPaletteDrawer);

        view.rptPaletteRoot.dataProvider = pr;
        view.addEventListener(PaletteEvent.CLICK, handlePaletteClick);

    }


    public function handlePaletteClick(event : PaletteEvent) : void {
        selectedItem = event.target;
        switch(event.action) {
            case PaletteEvent.ACTION_PALETTE_ITEM_CLICKED :
                var uiComponentSel:ItemRenderer = selectedItem as ItemRenderer;
                uiComponentSel.selected = true;
                if (projectProxy.currentIdentityAppliance != null) {
                    var selectedPaletteEntry:PaletteEntry = event.data as PaletteEntry;
                    sendNotification(ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM, selectedPaletteEntry.elementType);
                }
             break;
        }
    }

    override public function listNotificationInterests():Array {
        return [super.listNotificationInterests(),
                DESELECT_PALETTE_ELEMENT
        ];
    }

    override public function handleNotification(notification:INotification):void {
//        super.handleNotification(notification);
        switch (notification.getName()) {
            case DESELECT_PALETTE_ELEMENT:
                if(selectedItem != null){
                    var uiComponentSel:ItemRenderer = selectedItem as ItemRenderer;
                    uiComponentSel.selected = false;
                    selectedItem = null;
                }
                break;
        }
    }

    public function get view():PaletteView {
        return viewComponent as PaletteView;
    }

}
}