package org.atricore.idbus.capabilities.spmlr2.main.psp.producers;

import oasis.names.tc.spml._2._0.ListTargetsRequestType;
import org.atricore.idbus.capabilities.spmlr2.main.common.producers.SpmlR2Producer;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

/**
 * // TODO : Split this in several producers ...
 *
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class PSPProducer extends SpmlR2Producer {

    public PSPProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        if (content instanceof ListTargetsRequestType) {
            doProcessListTargetsReques(exchange, (ListTargetsRequestType) content);
        } else {

            // TODO : Send status=failure error= in response ! (use super producer or binding to build error

            // TODO : See SPMPL Section 3.1.2.2 Error (normative)

            throw new IdentityMediationFault("status='failure'",
                    null,
                    "error='unsupportedOperation'",
                    content.getClass().getName(),
                    null);
        }
    }

    protected void doProcessListTargetsReques(CamelMediationExchange exchange, ListTargetsRequestType request) {
        // TODO :

        request.getProfile(); 
    }
}
