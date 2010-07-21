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

    public class Constants {

        public static const IDENTITY_BUS_DEEP:uint=0;

        public static const IDENTITY_BUS_UNIT_DEEP:uint=1;

        public static const PROVIDER_DEEP:uint=1;

        public static const CHANNEL_DEEP:uint=2;

        public static const IDENTITY_VAULT_DEEP:uint=2;
        
        public static const IDENTITY_VAULT_CHANNEL_DEEP:uint=3;

		//dashboard parameters
		public static const MANAGER_PARAM_APPLIANCE_ID:String = "applianceId";
        public static const MANAGER_PARAM_STARTED_ONLY:String = "startedOnly";
        public static const MANAGER_PARAM_SHOW_APPLIANCE_UNITS:String = "showApplianceUnits";
        public static const MODELER_PARAM_APPLIANCE_ID:String = "applianceId";

        //provisioning parameters
        public static const PROVISIONING_PARAM_SECTION:String = "provisioningSection";
    }
}