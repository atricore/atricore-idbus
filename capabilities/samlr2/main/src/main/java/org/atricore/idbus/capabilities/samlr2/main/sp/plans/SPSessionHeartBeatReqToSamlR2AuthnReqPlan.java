package org.atricore.idbus.capabilities.samlr2.main.sp.plans;

import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SPSessionHeartBeatReqToSamlR2AuthnReqPlan  extends AbstractJbpmIdentityPlan {

    protected String getProcessDescriptorName() {
        return "spsso-sessionheartbeatreq-to-samlr2authnreq";
    }
}