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

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 1/13/11 - 2:50 PM
 */

public interface LiveUpdateAjaxService {

    public GetRepositoriesResponse getRepositories() throws LiveUpdateException;
    public GetRepositoryUpdatesResponse getRepositoryUpdates(GetRepositoryUpdatesRequest getRepositoriesUpdatesRequest) throws LiveUpdateException;
    public GetAvailableUpdatesResponse getAvailableUpdates() throws LiveUpdateException;
    public GetAvailableUpdatesResponse getAvailableUpdates(GetAvailableUpdatesRequest getAvailableUpdatesRequest) throws LiveUpdateException;
    public CheckForUpdatesResponse checkForUpdates() throws LiveUpdateException;
    public CheckForUpdatesResponse checkForUpdates(CheckForUpdatesRequest checkForUpdatesRequest) throws LiveUpdateException;
    public ApplyUpdatesResponse applyUpdate(ApplyUpdateRequest applyUpdateRequest) throws LiveUpdateException;
    public GetUpdateProfileResponse getUpdateProfile() throws LiveUpdateException;
    public GetUpdateProfileResponse getUpdateProfile(GetUpdateProfileRequest getUpdateProfileRequest) throws LiveUpdateException;

}
