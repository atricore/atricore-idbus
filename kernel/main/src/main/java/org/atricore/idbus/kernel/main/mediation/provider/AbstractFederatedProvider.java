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
    private ProviderService defaultProviderService;

    // Alternative channel configurations, including the possibility of overriding the setup.
    private Map<String, ProviderService> providerServices = new HashMap<String, ProviderService>();

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
        return defaultProviderService.getChannel();
    }

    public void setChannel(FederationChannel channel) {

        if (this.defaultProviderService == null) {
            this.defaultProviderService = new ProviderService(channel);
        } else {
            this.defaultProviderService.setChannel(channel);
        }
    }

    public Set<FederationChannel> getChannels() {
        return this.defaultProviderService.getOverrideChannels();
    }

    public FederationChannel getChannel(String configurationKey) {
        ProviderService cc = providerServices.get(configurationKey);
        if (cc != null)
            return cc.getChannel();

        return null;
    }

    public Set<FederationChannel> getChannels(String configurationKey) {
        ProviderService cc = providerServices.get(configurationKey);
        if (cc != null)
            return cc.getOverrideChannels();

        return null;
    }

    public ProviderService getDefaultProviderService() {
        return defaultProviderService;
    }

    public void setDefaultProviderService(ProviderService defaultProviderService) {
        this.defaultProviderService = defaultProviderService;
    }

    public Map<String, ProviderService> getProviderServices() {
        return providerServices;
    }

    public void setProviderServices(Map<String, ProviderService> providerServices) {
        this.providerServices = providerServices;
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
        if (defaultProviderService == null)
            return members;

        for (FederationChannel channel : defaultProviderService.getOverrideChannels()) {
            members.add(channel.getMember());
        }

        // Add also the default channel's member
        if (defaultProviderService.getChannel() != null)
            members.add(defaultProviderService.getChannel().getMember());

        return members;

    }

    public List<CircleOfTrustMemberDescriptor> getAllMembers() {

        List<CircleOfTrustMemberDescriptor> members = new ArrayList<CircleOfTrustMemberDescriptor>();
        if (defaultProviderService == null)
            return members;

        for (FederationChannel channel : defaultProviderService.getOverrideChannels()) {
            members.add(channel.getMember());
        }

        // Add also the default channel's member
        if (defaultProviderService.getChannel() != null)
            members.add(defaultProviderService.getChannel().getMember());

        // Add non-default services too
        for (ProviderService svc : providerServices.values()) {
            members.add(svc.getChannel().getMember());
            for (FederationChannel fc : svc.getOverrideChannels()) {
                members.add(fc.getMember());
            }
        }


        return members;
    }


    public List<CircleOfTrustMemberDescriptor> getMembers(String configurationKey) {

        List<CircleOfTrustMemberDescriptor> members = new ArrayList<CircleOfTrustMemberDescriptor>();
        ProviderService cc = providerServices.get(configurationKey);
        if (cc == null)
            return members;

        for (FederationChannel channel : cc.getOverrideChannels()) {
            members.add(channel.getMember());
        }

        // Add also the default channel's member
        if (cc.getChannel() != null)
            members.add(cc.getChannel().getMember());

        return members;
    }


}
