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

package com.atricore.idbus.console.main.view.util {

/*
 * Atricore IDBus
 *
 * Copyright 2009, Atricore Inc.
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

    public class EmbeddedIcons {

        public function EmbeddedIcons() {
        }

        [Bindable]
        [Embed(source="/assets/icons/ui/a3c_logo.jpeg")]
        static public var a3cLogo:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/zoom-in.png")]
        static public var zoominIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/zoom-original.png")]
        static public var zoomOriginalIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/zoom-out.png")]
        static public var zoomoutIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/dialog-warning.png")]
        static public var warningIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/dialog-error.png")]
        static public var errorIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/dialog-close.png")]
        static public var closeIcon:Class;        

        [Bindable]
        [Embed(source="/assets/icons/ui/dialog-information.png")]
        static public var infoIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/user-group-new.png")]
        static public var addGroupIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/user-group-delete.png")]
        static public var deleteGroupIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/user-group-properties.png")]
        static public var editGroupIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/list-add-user.png")]
        static public var addUserIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/list-remove-user.png")]
        static public var removeUserIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/user-properties.png")]
        static public var editUserIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/edit-find.png")]
        static public var searchUserIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/arrow-left-double.png")]
        static public var backIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/system-shutdown.png")]
        static public var logoutIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/go-up.png")]
        static public var goUpIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/go-down.png")]
        static public var goDownIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/go-next.png")]
        static public var goNextIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/start.png")]
        static public var startIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/stop.png")]
        static public var stopIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/restart.png")]
        static public var restartIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/uninstall.png")]
        static public var uninstallIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/back.jpg")]
        static public var homeIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/myProfile.png")]
        static public var myProfilelIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/ui/world_network_22x22.png")]
        static public var worldIcon:Class;
        
        [Bindable]
        [Embed(source="/assets/icons/ui/users_tree_22x22.png")]
        static public var usersIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/48x48/icon_identity_bus.png")]
        static public var busIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/48x48/icon_idp.png")]
        public static var idpIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/48x48/icon_sp.png")]
        public static var spIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/48x48/icon_bp.png")]
        public static var bpIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/48x48/icon_idp_channel.png")]
        public static var idpChannelIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/48x48/icon_sp_channel.png")]
        public static var spChannelIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/48x48/icon_identity_vault.png")]
        public static var vaultIcon:Class;        

        [Bindable]
        [Embed(source="/assets/icons/nodes/22x22/icon_identity_bus.png")]
        static public var busMiniIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/22x22/icon_idp.png")]
        public static var idpMiniIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/22x22/icon_sp.png")]
        public static var spMiniIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/22x22/icon_bp.png")]
        public static var bpMiniIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/22x22/icon_idp_channel.png")]
        public static var idpChannelMiniIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/22x22/icon_sp_channel.png")]
        public static var spChannelMiniIcon:Class;

        [Bindable]
        [Embed(source="/assets/icons/nodes/22x22/icon_identity_vault.png")]
        public static var vaultMiniIcon:Class;        

        [Bindable]
        [Embed(source="/style/yflexskin.swf",symbol="Tree_folderOpenIcon")]
        public static var folderOpenIcon:Class;

        [Bindable]
        [Embed(source="/style/yflexskin.swf",symbol="Tree_folderClosedIcon")]
        public static var folderClosedIcon:Class;
    }
}