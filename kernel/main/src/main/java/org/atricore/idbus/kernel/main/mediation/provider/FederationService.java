package org.atricore.idbus.kernel.main.mediation.provider;

import org.atricore.idbus.kernel.main.mediation.channel.AbstractFederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a set of cohesive operations, like a protocol.
 *
 * Services have a default channel, and a set of overrides or profiles for that channel.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface FederationService extends Serializable {

    public String getName();

    public String getServiceType();

    public String getProfile();

    public FederationChannel getChannel();

    public Set<FederationChannel> getOverrideChannels();

}
