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

package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.notifications.EMailNotificationScheme;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;
import com.atricore.idbus.console.services.dto.*;
import com.atricore.idbus.console.services.spi.LiveUpdateAjaxService;
import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.RequiredFeatureType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Author: Dusan Fisic
 * Mail: dfisic@atricore.org
 * Date: 1/13/11 - 2:50 PM
 */

public class LiveUpdateAjaxServiceImpl implements LiveUpdateAjaxService {

    private static Log logger = LogFactory.getLog(LiveUpdateAjaxServiceImpl.class);

    private LiveUpdateManager updateManager;

    public GetRepositoriesResponse getRepositories() throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getRepositories");

            Collection<Repository> repositories = updateManager.getRepositories();

            GetRepositoriesResponse resp = new GetRepositoriesResponse();
            resp.setRepositoryCollection(repositories);

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting repositories" + e.getMessage(), e);
        }
    }

    public GetRepositoryUpdatesResponse getRepositoryUpdates(GetRepositoryUpdatesRequest getRepositoriesUpdatesRequest)
            throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getRepositoriesUpdates");

            UpdatesIndexType updates = updateManager.getRepositoryUpdates(getRepositoriesUpdatesRequest.getRepositortyId());

            GetRepositoryUpdatesResponse resp = new GetRepositoryUpdatesResponse();

            List<UpdateDescriptorTypeDTO> updatesDesc = toUpdateDescriptorTypeDtoCollection(updates.getUpdateDescriptor());

            UpdatesIndexTypeDTO updatesDto = new UpdatesIndexTypeDTO();
            updatesDto.setId(updates.getID());
            updatesDto.setUpdateDescriptor(updatesDesc);

            resp.setUpdatesIndex(updatesDto);

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting repositories" + e.getMessage(), e);
        }
    }

    public GetAvailableUpdatesResponse getAvailableUpdates()
            throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getAvailableUpdates");

            Collection<UpdateDescriptorType> updates = updateManager.getAvailableUpdates();
            GetAvailableUpdatesResponse resp = new GetAvailableUpdatesResponse();

            resp.setUpdateDescriptors(toUpdateDescriptorTypeDtoCollection(updates));

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting updates" + e.getMessage(), e);
        }
    }

    public GetAvailableUpdatesResponse getAvailableUpdates(GetAvailableUpdatesRequest getAvailableUpdatesRequest)
            throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getAvailableUpdates [grp: "+ getAvailableUpdatesRequest.getGroup() +
                        " name: " + getAvailableUpdatesRequest.getName() +
                        " version: " + getAvailableUpdatesRequest.getVersion()+ "]");

            Collection<UpdateDescriptorType> updates = updateManager.getAvailableUpdates(
                    getAvailableUpdatesRequest.getGroup(),
                    getAvailableUpdatesRequest.getName(),
                    getAvailableUpdatesRequest.getVersion());

            GetAvailableUpdatesResponse resp = new GetAvailableUpdatesResponse();

            resp.setUpdateDescriptors(toUpdateDescriptorTypeDtoCollection(updates));

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting updates" + e.getMessage(), e);
        }
    }

    public CheckForUpdatesResponse checkForUpdates() throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing checkForUpdates");

            Collection<UpdateDescriptorType> updates = updateManager.checkForUpdates();

            CheckForUpdatesResponse resp = new CheckForUpdatesResponse();

            resp.setUpdateDescriptors(toUpdateDescriptorTypeDtoCollection(updates));

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting updates" + e.getMessage(), e);
        }
    }

    public CheckForUpdatesResponse checkForUpdates(CheckForUpdatesRequest checkForUpdatesRequest)
            throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getAvailableUpdates [grp: "+ checkForUpdatesRequest.getGroup() +
                        " name: " + checkForUpdatesRequest.getName() +
                        " version: " + checkForUpdatesRequest.getVersion()+ "]");

            Collection<UpdateDescriptorType> updates = updateManager.checkForUpdates(
                    checkForUpdatesRequest.getGroup(),
                    checkForUpdatesRequest.getName(),
                    checkForUpdatesRequest.getVersion());

            CheckForUpdatesResponse resp = new CheckForUpdatesResponse();

            resp.setUpdateDescriptors(toUpdateDescriptorTypeDtoCollection(updates));

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting updates" + e.getMessage(), e);
        }
    }

    public ApplyUpdatesResponse applyUpdate(ApplyUpdateRequest applyUpdateRequest)
            throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getAvailableUpdates [grp: "+ applyUpdateRequest.getGroup() +
                        " name: " + applyUpdateRequest.getName() +
                        " version: " + applyUpdateRequest.getVersion()+
                        " isOffline: " + applyUpdateRequest.isOffline() + "]");

            updateManager.applyUpdate(
                    applyUpdateRequest.getGroup(),
                    applyUpdateRequest.getName(),
                    applyUpdateRequest.getVersion(),
                    applyUpdateRequest.isOffline());

            ApplyUpdatesResponse resp = new ApplyUpdatesResponse();

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting updates" + e.getMessage(), e);
        }
    }

    public GetUpdateProfileResponse getUpdateProfile() throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getUpdateProfile");

            ProfileType profile = updateManager.getUpdateProfile();

            GetUpdateProfileResponse resp = new GetUpdateProfileResponse();
            resp.setProfile(toProfileTypeDTO(profile));

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting update profile" + e.getMessage(), e);
        }
    }

    public GetUpdateProfileResponse getUpdateProfile(GetUpdateProfileRequest getUpdateProfileRequest)
            throws LiveUpdateException {
        try{
            if (logger.isTraceEnabled())
                logger.trace("Processing getUpdateProfile [grp: "+ getUpdateProfileRequest.getGroup() +
                        " name: " + getUpdateProfileRequest.getName() +
                        " version: " + getUpdateProfileRequest.getVersion()+ "]");

            ProfileType profile = updateManager.getUpdateProfile(
                    getUpdateProfileRequest.getGroup(),
                    getUpdateProfileRequest.getName(),
                    getUpdateProfileRequest.getVersion());

            GetUpdateProfileResponse resp = new GetUpdateProfileResponse();
            resp.setProfile(toProfileTypeDTO(profile));

            return resp;
        } catch (Exception e) {
            // Log the error ant throw an exception to the Ajax layer.
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error getting update profile" + e.getMessage(), e);
        }
    }

    public UpdateNotificationSchemeResponse loadUpdateNotificationScheme(UpdateNotificationSchemeRequest updateNotificationSchemeRequest) throws LiveUpdateException {
        try{
            String schemeName = updateNotificationSchemeRequest.getNotificationScheme().getName();
            if (logger.isTraceEnabled())
                logger.trace("Processing loadUpdateNotificationScheme [schemeName: "+ schemeName);

            EMailNotificationScheme scheme = (EMailNotificationScheme) updateManager.getNotificationScheme(schemeName);

            UpdateNotificationSchemeResponse resp = new UpdateNotificationSchemeResponse();
            
            if (scheme != null) {
                NotificationSchemeDTO notifDTO = new NotificationSchemeDTO();
                notifDTO.setName(scheme.getName());
                notifDTO.setEnabled(scheme.isEnabled());
                notifDTO.setThreshold(scheme.getThreshold());
                notifDTO.setEmailAddresses(Arrays.asList(scheme.getAddresses()));
                notifDTO.setSmtpServer(scheme.getSmtpHost());
                notifDTO.setSmtpPort(scheme.getSmtpPort());
                notifDTO.setSmtpUsername(scheme.getSmtpUsername());
                notifDTO.setSmtpPassword(scheme.getSmtpPassword());

                resp.setNotificationScheme(notifDTO);
            }

            return resp;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error loading update notification scheme " + e.getMessage(), e);
        }

    }

    public UpdateNotificationSchemeResponse saveUpdateNotificationScheme(UpdateNotificationSchemeRequest updateNotificationSchemeRequest) throws LiveUpdateException {
        try{
            String schemeName = updateNotificationSchemeRequest.getNotificationScheme().getName();
            if (logger.isTraceEnabled())
                logger.trace("Processing saveUpdateNotificationScheme [schemeName: "+ schemeName);

            EMailNotificationScheme notifSch = new EMailNotificationScheme();
            NotificationSchemeDTO notifReq = updateNotificationSchemeRequest.getNotificationScheme();

            notifSch.setName(notifReq.getName());
            notifSch.setEnabled(notifReq.isEnabled());
            notifSch.setThreshold(notifReq.getThreshold());
            notifSch.setAddresses(notifReq.getEmailAddresses().toArray(new String[]{}));
            notifSch.setSmtpHost(notifReq.getSmtpServer());
            notifSch.setSmtpPort(notifReq.getSmtpPort());
            notifSch.setSmtpUsername(notifReq.getSmtpUsername());
            notifSch.setSmtpPassword(notifReq.getSmtpPassword());

            updateManager.saveNotificationScheme(notifSch);

            UpdateNotificationSchemeResponse resp = new UpdateNotificationSchemeResponse();
            return resp;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new LiveUpdateException("Error saving update notification scheme" + e.getMessage(), e);
        }
    }

    public void setUpdateManager(LiveUpdateManager updateManager) {
        this.updateManager = updateManager;
    }

    private List<RequiredFeatureTypeDTO> toRequiredFeatureTypeDTOCollection(Collection<RequiredFeatureType> coll) {
        List<RequiredFeatureTypeDTO> retList = new ArrayList<RequiredFeatureTypeDTO>();

        for (RequiredFeatureType reqType : coll) {
            RequiredFeatureTypeDTO dtoObj = new RequiredFeatureTypeDTO();
            dtoObj.setGroup(reqType.getGroup());
            dtoObj.setName(reqType.getName());
            dtoObj.setVersionExpresion(reqType.getVersionRange().getExpression());
            retList.add(dtoObj);
        }

        return retList;
    }

    private List<UpdateDescriptorTypeDTO> toUpdateDescriptorTypeDtoCollection(Collection<UpdateDescriptorType> coll) {
        List<UpdateDescriptorTypeDTO> retList = new ArrayList<UpdateDescriptorTypeDTO>();

        for (UpdateDescriptorType descType : coll) {
            UpdateDescriptorTypeDTO dtoObj = new UpdateDescriptorTypeDTO();
            dtoObj.setId(descType.getID());
            dtoObj.setDescription(descType.getDescription());
            dtoObj.setGroup(descType.getInstallableUnit().getGroup());
            dtoObj.setName(descType.getInstallableUnit().getName());
            dtoObj.setVersion(descType.getInstallableUnit().getVersion());
            dtoObj.setUpdateNature(descType.getInstallableUnit().getUpdateNature().toString());
            dtoObj.setIssueInstant(descType.getIssueInstant().toGregorianCalendar().getTime());
            dtoObj.setRequirements(toRequiredFeatureTypeDTOCollection(descType.getInstallableUnit().getRequirement()));
            retList.add(dtoObj);
        }

        return retList;
    }

    private ProfileTypeDTO toProfileTypeDTO(ProfileType profile) {
        ProfileTypeDTO retProfile = new ProfileTypeDTO();
        retProfile.setId(profile.getID());
        retProfile.setName(profile.getName());
        Collection<UpdateDescriptorTypeDTO> instUnits = new ArrayList<UpdateDescriptorTypeDTO>();
        for (InstallableUnitType iUnit : profile.getInstallableUnit()) {
            UpdateDescriptorTypeDTO updDesc = new UpdateDescriptorTypeDTO();
            updDesc.setId(iUnit.getID());
            updDesc.setName(iUnit.getName());
            updDesc.setGroup(iUnit.getGroup());
            updDesc.setVersion(iUnit.getVersion());
            updDesc.setDescription(iUnit.getDescription());
            updDesc.setUpdateNature(iUnit.getUpdateNature().toString());
            updDesc.setRequirements(toRequiredFeatureTypeDTOCollection(iUnit.getRequirement()));
            instUnits.add(updDesc);
        }
        retProfile.setInstallableUnits(instUnits);

        return retProfile;
    }

}
