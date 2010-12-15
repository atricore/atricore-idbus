package org.atricore.idbus.capabilities.spmlr2.main.common.producers;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.GroupType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import org.springframework.beans.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Exception;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SPMLR2MessagingConstants;
import org.atricore.idbus.capabilities.spmlr2.main.common.DateUtils;
import org.atricore.idbus.capabilities.spmlr2.main.common.plans.SPMLR2PlanningConstants;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.AddUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindGroupByNameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.FindUserByUsernameRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.request.UpdateUserRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindGroupByNameResponse;
import org.atricore.idbus.kernel.main.provisioning.spi.response.FindUserByUsernameResponse;
import org.atricore.idbus.kernel.main.store.exceptions.IdentityProvisioningException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.xml.datatype.XMLGregorianCalendar;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
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

    protected AddUserRequest toAddUserRequest(ProvisioningTarget target, AddRequestType spmlRequest) throws SpmlR2Exception {

        try {

            AddUserRequest req = new AddUserRequest();

            UserType spmlUser = (UserType) spmlRequest.getData();
            BeanUtils.copyProperties(spmlUser, req, new String[] {"groups"});

            if (spmlUser.getGroup() != null) {
                Group[] groups = new Group[spmlUser.getGroup().size()];

                for (int i = 0 ; i < spmlUser.getGroup().size() ; i++) {
                    GroupType spmlGroup = spmlUser.getGroup().get(i);
                    Group group = lookupGroup(target, spmlGroup.getName());
                    groups[i] = group;
                }

                req.setGroups(groups);
            }

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected UpdateUserRequest toUpdateUserRequest(ProvisioningTarget target, ModifyRequestType spmlRequest) throws SpmlR2Exception {

        UpdateUserRequest req =  new UpdateUserRequest ();

        try {
            ModificationType spmlMod = spmlRequest.getModification().get(0);

            UserType spmlUser = (UserType) spmlMod.getData();
            User user = lookupUser(target, spmlUser.getUserName());

            // Do not override null properties in the original object
            String[] ignoredProps = getNullProps(spmlUser, new String[] {"id", "groups"});

            BeanUtils.copyProperties(spmlUser, user, ignoredProps);

            if (spmlUser.getGroup() != null && spmlUser.getGroup().size() > 0) {

                Group[] groups = new Group[spmlUser.getGroup().size()];

                for (int i = 0 ; i < spmlUser.getGroup().size() ; i++) {

                    GroupType spmlGroup = spmlUser.getGroup().get(i);

                    Group group = new Group();
                    group.setId(spmlGroup.getId());
                    group.setName(spmlGroup.getName());
                    group.setDescription(spmlGroup.getDescription());

                    groups[i] = group;

                }

                user.setGroups(groups);
            }

            // Copy password if found
            user.setUserPassword(spmlUser.getUserPassword());

            req.setUser(user);

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
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
            BeanUtils.copyProperties(user, spmlUser, new String[] {"groups"});
            if (user.getGroups() != null) {
                for (int i = 0; i < user.getGroups().length; i++) {
                    Group group = user.getGroups()[i];
                    GroupType spmlGroup = new GroupType();

                    spmlGroup.setId(group.getId());
                    spmlGroup.setName(group.getName());
                    spmlGroup.setDescription(group.getDescription());

                    spmlUser.getGroup().add(spmlGroup);
                }
            }

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

    protected User lookupUser(ProvisioningTarget target, String username) throws ProvisioningException {
        FindUserByUsernameRequest req = new FindUserByUsernameRequest ();
        req.setUsername(username);
        FindUserByUsernameResponse res = target.findUserByUsername(req);

        return res.getUser();

    }

    protected Group lookupGroup(ProvisioningTarget target, String groupname) throws ProvisioningException {
        FindGroupByNameRequest req = new FindGroupByNameRequest ();
        req.setName(groupname);
        FindGroupByNameResponse res = target.findGroupByName(req);

        return res.getGroup();

    }

    protected String[] getNullProps(Object o, String[] otherProps) {

        PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(o.getClass());
        List<String> nullProps = new ArrayList<String>();

        for (String otherProp : otherProps)
            nullProps.add(otherProp);

        for (PropertyDescriptor prop : props) {

            Method getter = prop.getReadMethod();
            if (getter == null)
                continue;

            try {
                Object result = getter.invoke(o);
                if (result == null)
                    nullProps.add(prop.getName());

            } catch (InvocationTargetException e) {
                logger.warn(e.getMessage(), e);

            } catch (IllegalAccessException e) {
                logger.warn(e.getMessage(), e);

            }

        }

        return nullProps.toArray(new String[nullProps.size()]);

    }


}
