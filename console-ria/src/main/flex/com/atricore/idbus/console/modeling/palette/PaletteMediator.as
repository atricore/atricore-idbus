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
import com.atricore.idbus.console.modeling.diagram.DiagramElementTypes;
import com.atricore.idbus.console.modeling.palette.event.PaletteEvent;
import com.atricore.idbus.console.modeling.palette.model.PaletteDrawer;
import com.atricore.idbus.console.modeling.palette.model.PaletteEntry;
import com.atricore.idbus.console.modeling.palette.model.PaletteRoot;

import mx.collections.IList;

import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.observer.Notification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.primitives.BitmapImage;

public class PaletteMediator extends IocMediator {
    private var selectedIndex:int;

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
        var saml2PaletteDrawer:PaletteDrawer = new PaletteDrawer("SAML 2", null, null);

        saml2PaletteDrawer.add(
                    new PaletteEntry("Identity Provider", EmbeddedIcons.idpMiniIcon, "Identity Provider Entry", DiagramElementTypes.IDENTITY_PROVIDER_ELEMENT_TYPE)

                );
        saml2PaletteDrawer.add(
                    new PaletteEntry("Service Provider", EmbeddedIcons.spMiniIcon, "Service Provider Entry", DiagramElementTypes.SERVICE_PROVIDER_ELEMENT_TYPE)

                );
        saml2PaletteDrawer.add(
                    new PaletteEntry("IDP Channel", EmbeddedIcons.idpChannelMiniIcon, "Identity Provider Channel Entry", DiagramElementTypes.IDP_CHANNEL_ELEMENT_TYPE)

                );
        saml2PaletteDrawer.add(
                    new PaletteEntry("SP Channel", EmbeddedIcons.spChannelMiniIcon, "Service Provider Channel Entry", DiagramElementTypes.SP_CHANNEL_ELEMENT_TYPE)

                );

        var pr:PaletteRoot  = new PaletteRoot("Identity Appliance Modeler Palette", null, null);
        pr.add(saml2PaletteDrawer);

        var environmentsPaletteDrawer:PaletteDrawer = new PaletteDrawer("Execution Environments", null, null);

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Alfresco", EmbeddedIcons.alfrescoEnvironmentMiniIcon, "Alfresco Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Apache", EmbeddedIcons.apacheEnvironmentMiniIcon, "Apache Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Java EE", EmbeddedIcons.javaEnvironmentMiniIcon, "Java EE Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("JBoss", EmbeddedIcons.jbossEnvironmentMiniIcon, "JBoss Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Liferay", EmbeddedIcons.liferayEnvironmentMiniIcon, "Liferay Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("PhpBB", EmbeddedIcons.phpbbEnvironmentMiniIcon, "PhpBB Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Tomcat", EmbeddedIcons.tomcatEnvironmentMiniIcon, "Tomcat Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Webserver", EmbeddedIcons.webEnvironmentMiniIcon, "Webserver Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Weblogic", EmbeddedIcons.weblogicEnvironmentMiniIcon, "Weblogic Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Websphere", EmbeddedIcons.websphereEnvironmentMiniIcon, "Websphere Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        environmentsPaletteDrawer.add(
                    new PaletteEntry("Windows", EmbeddedIcons.windowsEnvironmentMiniIcon, "Windows Environment Entry", DiagramElementTypes.JBOSS_EXECUTION_ENVIRONMENT_ELEMENT_TYPE)

                );

        pr.add(environmentsPaletteDrawer);

        var identitySourcesPaletteDrawer:PaletteDrawer = new PaletteDrawer("Identity Sources", null, null);
        identitySourcesPaletteDrawer.add(
                    new PaletteEntry("DB Identity Vault", EmbeddedIcons.dbIdentitySourceMiniIcon, "Database Identity Vault Entry", DiagramElementTypes.DB_IDENTITY_VAULT_ELEMENT_TYPE)

                );
        identitySourcesPaletteDrawer.add(
                    new PaletteEntry("LDAP Identity Source", EmbeddedIcons.ldapIdentitySourceMiniIcon, "LDAP Identity Source Entry", DiagramElementTypes.LDAP_IDENTITY_SOURCE_ELEMENT_TYPE)

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
       var notification:Notification;

       switch(event.action) {
          case PaletteEvent.ACTION_PALETTE_ITEM_CLICKED :
             var selectedPaletteEntry:PaletteEntry = event.data as PaletteEntry;
             sendNotification(ApplicationFacade.DRAG_ELEMENT_TO_DIAGRAM, selectedPaletteEntry.elementType);
             break;
       }

    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }

    public function get view():PaletteView {
        return viewComponent as PaletteView;
    }

}
}