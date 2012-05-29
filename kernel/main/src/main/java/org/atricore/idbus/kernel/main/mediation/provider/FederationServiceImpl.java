package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.mediation.channel.AbstractFederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class FederationServiceImpl implements FederationService {

    private String name;
    private String profile;
    private String serviceType;
    private FederationChannel channel;
    private Set<FederationChannel> overrideChannels = new HashSet<FederationChannel>();

    public FederationServiceImpl() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public FederationServiceImpl(FederationChannel channel) {
        this.channel = channel;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public FederationChannel getChannel() {
        return channel;
    }

    public void setChannel(FederationChannel channel) {
        this.channel = channel;
        ((AbstractFederationChannel)channel).setConfiguration(serviceType);
    }

    public Set<FederationChannel> getOverrideChannels()  {
        return overrideChannels;
    }

    public void setOverrideChannels(Set<FederationChannel> overrideChannels) {
        this.overrideChannels = overrideChannels;
    }
}
