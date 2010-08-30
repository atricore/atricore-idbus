package org.atricore.idbus.capabilities.spmlr2.main.common.producers;

import oasis.names.tc.dsml._2._0.core.ModifyRequest;
import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Exception;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SPMLR2MessagingConstants;
import org.atricore.idbus.capabilities.spmlr2.main.common.DateUtils;
import org.atricore.idbus.capabilities.spmlr2.main.common.plans.SPMLR2PlanningConstants;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.UpdateUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AddUserResponse;
import org.atricore.idbus.kernel.main.store.exceptions.IdentityProvisioningException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class SpmlR2Producer extends AbstractCamelProducer<CamelMediationExchange>
        implements SPMLR2Constants, SPMLR2MessagingConstants, SPMLR2PlanningConstants {

    private static final Log logger = LogFactory.getLog(SpmlR2Producer.class);

    protected UUIDGenerator idGen = new UUIDGenerator();

    private Converter dateConverter = new Converter() {

        public Object convert(Class aClass, Object o) {
            if (aClass.isAssignableFrom(XMLGregorianCalendar.class)) {
                return DateUtils.toXMLGregorianCalendar((Date) o);
            } else if (aClass.isAssignableFrom(java.util.Date.class)) {
                return DateUtils.toXMLGregorianCalendar((Date) o);
            }

            return null;
        }
    };

    protected SpmlR2Producer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);

        ConvertUtils.register(dateConverter, XMLGregorianCalendar.class);
        ConvertUtils.register(dateConverter, Date.class);
    }

    @Override
    protected void doProcess(CamelMediationExchange e) throws Exception {
        // DO Nothing!
    }

    protected ProvisioningTarget lookupTarget(String targetId) {

        if (targetId == null)
            throw new NullPointerException("Target ID cannot be null");

        for (ProvisioningTarget target : ((PsPChannel)channel).getProvider().getProvisioningTargets()) {
            if (target.getName().equals(targetId)) {
                return target;
            }
        }
        throw new IllegalArgumentException("Target ID not found : " + targetId + " in SPML Mediator for " + channel.getName());
    }
    
    // ------------------------------< Utilities >

    // TODO : Use Dozer / Planning

    // SPML Req to Kernel Req

    // SPML User to Kernel User

    // Kernel Res to SPML Res

    // Kernel User to SPML User

    protected AddUserRequest toAddUserRequest(ProvisioningTarget target, AddUserRequest req, AddRequestType spmlRequest) throws SpmlR2Exception {
        try {
            UserType spmlUser = (UserType) spmlRequest.getData();
            BeanUtils.copyProperties(req, spmlUser);
            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected UpdateUserRequest toUpdateUserRequest(ProvisioningTarget target, UpdateUserRequest req , ModifyRequestType spmlRequest) throws SpmlR2Exception {
        try {
            ModificationType spmlMod = spmlRequest.getModification().get(0);
            UserType spmlUser = (UserType) spmlMod.getData();
            BeanUtils.copyProperties(req , spmlUser);
            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
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

    protected PSOType toSpmlUser(ProvisioningTarget target, User user) throws
            IdentityProvisioningException {

        try {

            UserType spmlUser = new UserType();

            BeanUtils.copyProperties(spmlUser, user);
            // TODO : Groups

            PSOIdentifierType psoGroupId = new PSOIdentifierType ();
            psoGroupId.setTargetID(target.getName());
            psoGroupId.setID(user.getId() + "");

            PSOType psoGroup = new PSOType();
            psoGroup.setData(spmlUser);
            psoGroup.setPsoID(psoGroupId);

            return psoGroup;
        } catch (Exception e) {
            throw new IdentityProvisioningException("Can't convert User to SPML User: " + e.getMessage(), e);
        }

    }
    

    
}
