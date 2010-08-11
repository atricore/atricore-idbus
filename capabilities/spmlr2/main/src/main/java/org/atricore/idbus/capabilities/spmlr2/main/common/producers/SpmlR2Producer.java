package org.atricore.idbus.capabilities.spmlr2.main.common.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SPMLR2MessagingConstants;
import org.atricore.idbus.capabilities.spmlr2.main.common.plans.SPMLR2PlanningConstants;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class SpmlR2Producer extends AbstractCamelProducer<CamelMediationExchange>
        implements SPMLR2Constants, SPMLR2MessagingConstants, SPMLR2PlanningConstants {

    private static final Log logger = LogFactory.getLog(SpmlR2Producer.class);

    protected UUIDGenerator idGen = new UUIDGenerator();

    protected SpmlR2Producer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange e) throws Exception {
        // DO Nothing!
    }
}
