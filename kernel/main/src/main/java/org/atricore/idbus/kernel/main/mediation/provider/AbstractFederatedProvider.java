/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.kernel.main.mediation.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractFederatedProvider implements FederatedProvider {

    private static final Log logger = LogFactory.getLog(FederatedProvider.class);

    private String name;

    private String description;

    private String role;

    // Main channel and specializations ...
    private FederationService defaultFederationService;

    // Alternative channel configurations, including the possibility of overriding the setup.
    private Set<FederationService> federationServices = new HashSet<FederationService>();

    private CircleOfTrust circleOfTrust;

    // TODO : Is this part of the channel configuration ?!
    private String skin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public FederationChannel getChannel() {
        // We need to support this ..
        if (defaultFederationService == null)
            return null;

        return defaultFederationService.getChannel();
    }

    /**
     * Use #setDefaultFederationService instead
     * @param channel
     */
    @Deprecated
    public void setChannel(FederationChannel channel) {

        if (this.defaultFederationService == null) {
            this.defaultFederationService = new FederationServiceImpl(channel);
        } else {
            ((FederationServiceImpl)this.defaultFederationService).setChannel(channel);
        }
    }

    public Set<FederationChannel> getChannels() {
        if (defaultFederationService == null) {
            return null;
        }
        return this.defaultFederationService.getOverrideChannels();
    }

    public FederationChannel getChannel(String configurationKey) {
        for (FederationService fc : federationServices) {
            if (fc.getName().equals(configurationKey))
                return fc.getChannel();
        }
        return null;
    }

    public Set<FederationChannel> getChannels(String configurationKey) {
        for (FederationService fc : federationServices) {
            if (fc.getName().equals(configurationKey))
                return fc.getOverrideChannels();
        }
        return null;
    }

    public FederationService getDefaultFederationService() {
        return defaultFederationService;
    }

    public void setDefaultFederationService(FederationService defaultFederationService) {

        this.defaultFederationService = defaultFederationService;
    }

    public Set<FederationService> getFederationServices() {
        return federationServices;
    }

    public void setFederationServices(Set<FederationService> federationServices) {
        this.federationServices = federationServices;
    }

    public CircleOfTrust getCircleOfTrust() {
        return circleOfTrust;
    }

    public void setCircleOfTrust(CircleOfTrust circleOfTrust) {
        this.circleOfTrust = circleOfTrust;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    /**
     * This only works for the default channel configuration
     */
    public List<CircleOfTrustMemberDescriptor> getMembers() {
        List<CircleOfTrustMemberDescriptor> members = new ArrayList<CircleOfTrustMemberDescriptor>();
        if (defaultFederationService == null)
            return members;

        for (FederationChannel channel : defaultFederationService.getOverrideChannels()) {
            members.add(channel.getMember());
        }

        // Add also the default channel's member
        if (defaultFederationService.getChannel() != null)
            members.add(defaultFederationService.getChannel().getMember());

        return members;

    }

    public List<CircleOfTrustMemberDescriptor> getAllMembers() {

        List<CircleOfTrustMemberDescriptor> members = new ArrayList<CircleOfTrustMemberDescriptor>();
        if (defaultFederationService == null)
            return members;

        for (FederationChannel channel : defaultFederationService.getOverrideChannels()) {
            members.add(channel.getMember());
        }

        // Add also the default channel's member
        if (defaultFederationService.getChannel() != null)
            members.add(defaultFederationService.getChannel().getMember());

        // Add non-default services too
        for (FederationService svc : federationServices) {
            members.add(svc.getChannel().getMember());
            for (FederationChannel fc : svc.getOverrideChannels()) {
                members.add(fc.getMember());
            }
        }


        return members;
    }


    public List<CircleOfTrustMemberDescriptor> getMembers(String configurationKey) {

        List<CircleOfTrustMemberDescriptor> members = new ArrayList<CircleOfTrustMemberDescriptor>();

        FederationService federationSvc = null;
        for (FederationService fc : federationServices) {
            if (fc.getName().equals(configurationKey)) {
                federationSvc = fc;
                break;
            }
        }

        if (federationSvc == null)
            return members;

        for (FederationChannel channel : federationSvc.getOverrideChannels()) {
            members.add(channel.getMember());
        }

        // Add also the default channel's member
        if (federationSvc.getChannel() != null)
            members.add(federationSvc.getChannel().getMember());

        return members;
    }


}
