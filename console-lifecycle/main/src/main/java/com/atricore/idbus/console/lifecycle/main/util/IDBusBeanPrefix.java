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

package com.atricore.idbus.console.lifecycle.main.util;

public enum IDBusBeanPrefix {
	IDP_CHANNEL_ENDPOINT_BEAN_PREFIX ("idp-channel-endpoint"),
	IDENTITY_BUS_BEAN_PREFIX ("identity-bus"),
	IDP_BEAN_PREFIX ("idp"),
	SP_BEAN_PREFIX ("sp"),
	IDP_CHANNEL_BEAN_PREFIX ("idp-channel"),
	SP_CHANNEL_BEAN_PREFIX ("sp-channel"),
	COT_MEMBER_BEAN_PREFIX ("cot-member"),
	METADATA_SUFFIX ("-metadata.xml"),
	COT_MGR_BEAN_PREFIX ("cot-mgr"),
	COT_BEAN_PREFIX ("cot");
	
	private String prefix;
	
	private IDBusBeanPrefix(String prefix){
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public String toString() {
		return prefix;
	}
	
	
}
