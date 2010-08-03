package org.atricore.idbus.capabilities.samlr2.management.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.management.CircleOfTrustMBean;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustImpl;

import javax.management.openmbean.TabularData;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class CircleOfTrustMBeanImpl implements CircleOfTrustMBean {

    private static final Log logger = LogFactory.getLog(CircleOfTrustMBeanImpl.class);

    // TODO : Use an interface
    private CircleOfTrustImpl circleOfTrust;

    public CircleOfTrustImpl getCircleOfTrust() {
        return circleOfTrust;
    }

    public void setCircleOfTrust(CircleOfTrustImpl circleOfTrust) {
        this.circleOfTrust = circleOfTrust;
    }

    public TabularData listProviders() {
        try {
            // TODO : Implement me!
            circleOfTrust.getProviders();
        } catch (Exception e) {
            logger.error("Can't list providers for COT " + circleOfTrust.getName() + ", error: " + e.getMessage(), e);
        }

        return null;
    }
}
