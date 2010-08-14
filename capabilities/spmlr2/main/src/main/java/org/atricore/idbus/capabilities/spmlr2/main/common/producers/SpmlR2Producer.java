package org.atricore.idbus.capabilities.spmlr2.main.common.producers;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SPMLR2MessagingConstants;
import org.atricore.idbus.capabilities.spmlr2.main.common.plans.SPMLR2PlanningConstants;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddGroupRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddGroupResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddUserResponse;
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

    protected ProvisioningTarget lookupTarget(String targetId) {
        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) channel.getIdentityMediator();

        for (ProvisioningTarget target : mediator.getProvisioningTargets()) {
            if (target.getName().equals(targetId)) {
                return target;
            }
        }
        return null;
    }
    
    // ------------------------------< Utilities >

    // TODO : Use Dozer / Planning

    // SPML Req to Kernel Req

    // SPML User to Kernel User

    // Kernel Res to SPML Res

    // Kernel User to SPML User

    protected AddUserRequest toAddUserRequest(ProvisioningTarget target, AddRequestType spmlRequest) {
        UserType user = (UserType) spmlRequest.getData();
        AddUserRequest req = new AddUserRequest();
        return req;
    }

    protected ResponseType toSpmlResponse(ProvisioningTarget target, AddUserResponse response) {
        AddResponseType spmlResponse = null;
        // TODO :
        return spmlResponse;
    }

    protected PSOType toSpmlGroup(ProvisioningTarget target, Group group) {
        GroupType spmlGroup = new GroupType();

        spmlGroup.setId(group.getId());
        spmlGroup.setName(group.getName());
        spmlGroup.setDescription(group.getDescription());

        PSOIdentifierType psoGroupId = new PSOIdentifierType ();
        psoGroupId.setTargetID(target.getName());
        psoGroupId.setID(group.getId() + "");
        psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        PSOType psoGroup = new PSOType();
        psoGroup.setData(spmlGroup);
        psoGroup.setPsoID(psoGroupId);

        return psoGroup;

    }

    protected PSOType toSpmlUser(ProvisioningTarget target, User user) {
        UserType spmlUser = new UserType();

        spmlUser.setUserName(user.getUserName());

        PSOIdentifierType psoGroupId = new PSOIdentifierType ();
        psoGroupId.setTargetID(target.getName());
        psoGroupId.setID(user.getId() + "");

        PSOType psoGroup = new PSOType();
        psoGroup.setData(spmlUser);
        psoGroup.setPsoID(psoGroupId);

        return psoGroup;

    }
    

    
}
