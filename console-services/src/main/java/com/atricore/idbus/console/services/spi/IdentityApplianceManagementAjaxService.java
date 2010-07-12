/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;

/**
 * Author: Dejan Maric
 */
public interface IdentityApplianceManagementAjaxService {

	DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException;

	UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException;

    ImportIdentityApplianceResponse importIdentityAppliance(ImportIdentityApplianceRequest request) throws IdentityServerException;

    ExportIdentityApplianceResponse ExportIdentityAppliance(ExportIdentityApplianceRequest request) throws IdentityServerException;

    ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException;
    
    // D -> DEPLOYED -> STARTED -> STOPPED -> UNDEPLOYED(D)

    CreateSimpleSsoResponse createSimpleSso(CreateSimpleSsoRequest req) throws IdentityServerException;

    AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException;

    UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest request) throws IdentityServerException;    

    LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException;

    ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest request) throws IdentityServerException;
    
    RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException;

}
