package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalProvider extends Provider {

    private ProviderConfig config;

    /**
     * The default channel where requests from other providers are received
     */
    private Channel defaultChannel;

    /**
     * Channels that override the default channel (optional), they must refer to a target! 
     */
    private Set<Channel> channels =  new HashSet<Channel>();
    private static final long serialVersionUID = 2967662484748634148L;

    public ProviderConfig getConfig() {
        return config;
    }

    public void setConfig(ProviderConfig config) {
        this.config = config;
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Set<Channel> channels) {
        this.channels = channels;
    }
}