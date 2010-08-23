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

package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.ServiceProviderDTO;

public class CreateExecutionEnvironmentElementRequest {
    private var _owner:ServiceProviderDTO;
    private var _notationalElementId:String;

    public function CreateExecutionEnvironmentElementRequest(owner:ServiceProviderDTO, notationalElementId:String) {
        _owner = owner;
        _notationalElementId = notationalElementId;
    }

    public function get owner():ServiceProviderDTO {
        return _owner;
    }

    public function get notationalElementId():String {
        return _notationalElementId;
    }

}
}