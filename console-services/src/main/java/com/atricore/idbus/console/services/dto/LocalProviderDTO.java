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

package com.atricore.idbus.console.services.dto;


import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalProviderDTO extends ProviderDTO {

    private ProviderConfigDTO config;

    /**
     * The default channel where requests from other providers are received
     */
    private ChannelDTO defaultChannel;

    /**
     * Channels that override the default channel (optional), they must refer to a target! 
     */
    private Set<ChannelDTO> channels =  new HashSet<ChannelDTO>();
    private static final long serialVersionUID = 2967662484748634148L;

    public ProviderConfigDTO getConfig() {
        return config;
    }

    public void setConfig(ProviderConfigDTO config) {
        this.config = config;
    }

    public ChannelDTO getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(ChannelDTO defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public Set<ChannelDTO> getChannels() {
        return channels;
    }

    public void setChannels(Set<ChannelDTO> channels) {
        this.channels = channels;
    }
}