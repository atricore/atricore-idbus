package org.atricore.idbus.capabilities.spmlr2.main.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SpmlR2Binding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractSpmlR2Mediator extends AbstractCamelMediator {

    private static final Log logger = LogFactory.getLog(AbstractSpmlR2Mediator.class);

    /**
     * @return
     * @org.apache.xbean.Property alias="log-messages"
     */
    @Override
    public boolean isLogMessages() {
        return super.isLogMessages();
    }

    @Override
    public void setLogMessages(boolean logMessages) {
        super.setLogMessages(logMessages);
    }

    public EndpointDescriptor resolveEndpoint(Channel channel, IdentityMediationEndpoint endpoint) throws IdentityMediationException {

        if (channel instanceof PsPChannel) {

            String type = null;
            String location;
            String responseLocation;

            SpmlR2Binding binding = null;

            logger.debug("Creating Endpoint Descriptor for : " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint binding
            // ---------------------------------------------
            if (endpoint.getBinding() != null)
                binding = SpmlR2Binding.asEnum(endpoint.getBinding());
            else
                logger.warn("No SPMLR2 Binding found in endpoint " + endpoint.getName());

            // ---------------------------------------------
            // Resolve Endpoint location
            // ---------------------------------------------
            location = endpoint.getLocation();
            if (location.startsWith("/"))
                location = channel.getLocation() + location;

            // ---------------------------------------------
            // Resolve Endpoint response location
            // ---------------------------------------------
            responseLocation = endpoint.getResponseLocation();
            if (responseLocation != null && responseLocation.startsWith("/"))
                responseLocation = channel.getLocation() + responseLocation;

            // ---------------------------------------------
            // Resolve Endpoint type
            // ---------------------------------------------
            // If no ':' is present, lastIndexOf should resturn -1 and the entire type is used.
            type = endpoint.getType().substring(endpoint.getType().lastIndexOf("}") + 1);

            return new EndpointDescriptorImpl(endpoint.getName(),
                    type,
                    binding.getValue(),
                    location,
                    responseLocation);


        } else {
            throw new IdentityMediationException("Unsupported channel type " +
                    channel.getName() + " " + channel.getClass().getName());
        }
    }
}

