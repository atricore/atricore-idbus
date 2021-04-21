package org.atricore.idbus.capabilities.spmlr2.main.common.producers;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.atricore.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Exception;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SPMLR2MessagingConstants;
import org.atricore.idbus.capabilities.spmlr2.main.common.AbstractSpmlR2Mediator;
import org.atricore.idbus.capabilities.spmlr2.main.common.plans.SPMLR2PlanningConstants;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.auditing.core.AuditingServer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.provisioning.domain.AttributeType;
import org.atricore.idbus.kernel.main.provisioning.domain.*;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.atricore.idbus.kernel.main.store.exceptions.IdentityProvisioningException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.BeanUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
            BeanUtils.copyProperties(spmlUser, req, new String[] {"groups", "attrs", "accountExpirationDate", "passwordExpirationDate"});

            if (spmlUser.getAccountExpirationDate() != null)
                req.setAccountExpirationDate(spmlUser.getAccountExpirationDate().toGregorianCalendar().getTime());

            if (spmlUser.getPasswordExpirationDate() != null)
                req.setPasswordExpirationDate(spmlUser.getPasswordExpirationDate().toGregorianCalendar().getTime());

            if (spmlUser.getGroup() != null) {
                Group[] groups = new Group[spmlUser.getGroup().size()];

                for (int i = 0 ; i < spmlUser.getGroup().size() ; i++) {
                    GroupType spmlGroup = spmlUser.getGroup().get(i);
                    Group group = lookupGroup(target, spmlGroup.getName());
                    groups[i] = group;
                }

                req.setGroups(groups);
            }

            if (spmlUser.getAttributeValue() != null) {
                UserAttributeValue[] attrs = new UserAttributeValue[spmlUser.getAttributeValue().size()];

                for (int i = 0 ; i < spmlUser.getAttributeValue().size() ; i++) {
                    AttributeValueType spmlAttr = spmlUser.getAttributeValue().get(i);
                    UserAttributeValue attr = new UserAttributeValue();
                    attr.setName(spmlAttr.getName());
                    attr.setValue(spmlAttr.getValue());
                    attrs[i] = attr;
                }

                req.setAttrs(attrs);
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
            User user = lookupUser(target, spmlUser.getId());

            // Do not override null properties in the original object
            String[] ignoredProps = getNullProps(spmlUser, new String[] {"id", "groups", "attrs", "accountExpirationDate", "passwordExpirationDate"});

            BeanUtils.copyProperties(spmlUser, user, ignoredProps);

            if (spmlUser.getAccountExpirationDate() != null)
                user.setAccountExpirationDate(spmlUser.getAccountExpirationDate().toGregorianCalendar().getTimeInMillis());

            if (spmlUser.getPasswordExpirationDate() != null)
                user.setPasswordExpirationDate(spmlUser.getPasswordExpirationDate().toGregorianCalendar().getTimeInMillis());

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
            } else {
                user.setGroups(new Group[0]);
            }

            if (spmlUser.getAttributeValue() != null) {
                UserAttributeValue[] attrs = new UserAttributeValue[spmlUser.getAttributeValue().size()];

                for (int i = 0 ; i < spmlUser.getAttributeValue().size() ; i++) {
                    AttributeValueType spmlAttr = spmlUser.getAttributeValue().get(i);
                    UserAttributeValue attr = new UserAttributeValue();
                    attr.setId(spmlAttr.getId());
                    attr.setName(spmlAttr.getName());
                    attr.setValue(spmlAttr.getValue());
                    attrs[i] = attr;
                }

                user.setAttrs(attrs);
            } else {
                user.setAttrs(new UserAttributeValue[0]);
            }
            
            // Copy password if found
            user.setUserPassword(spmlUser.getUserPassword());

            req.setUser(user);

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected AddGroupRequest toAddGroupRequest(ProvisioningTarget target, AddRequestType spmlRequest) throws SpmlR2Exception {

        try {

            AddGroupRequest req = new AddGroupRequest();

            GroupType spmlGroup = (GroupType) spmlRequest.getData();

            req.setName(spmlGroup.getName());
            req.setDescription(spmlGroup.getDescription());

            if (spmlGroup.getAttributeValue() != null) {
                GroupAttributeValue[] attrs = new GroupAttributeValue[spmlGroup.getAttributeValue().size()];

                for (int i = 0 ; i < spmlGroup.getAttributeValue().size() ; i++) {
                    AttributeValueType spmlAttr = spmlGroup.getAttributeValue().get(i);
                    GroupAttributeValue attr = new GroupAttributeValue();
                    attr.setName(spmlAttr.getName());
                    attr.setValue(spmlAttr.getValue());
                    attrs[i] = attr;
                }

                req.setAttrs(attrs);
            }

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected UpdateGroupRequest toUpdateGroupRequest(ProvisioningTarget target, ModifyRequestType spmlRequest) throws SpmlR2Exception {

        UpdateGroupRequest req = new UpdateGroupRequest();

        try {
            ModificationType spmlMod = spmlRequest.getModification().get(0);

            GroupType spmlGroup = (GroupType) spmlMod.getData();

            req.setId(spmlGroup.getId());
            req.setName(spmlGroup.getName());
            req.setDescription(spmlGroup.getDescription());

            if (spmlGroup.getAttributeValue() != null) {
                GroupAttributeValue[] attrs = new GroupAttributeValue[spmlGroup.getAttributeValue().size()];

                for (int i = 0 ; i < spmlGroup.getAttributeValue().size() ; i++) {
                    AttributeValueType spmlAttr = spmlGroup.getAttributeValue().get(i);
                    GroupAttributeValue attr = new GroupAttributeValue();
                    attr.setId(spmlAttr.getId());
                    attr.setName(spmlAttr.getName());
                    attr.setValue(spmlAttr.getValue());
                    attrs[i] = attr;
                }

                req.setAttrs(attrs);
            }

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected AddUserAttributeRequest toAddUserAttributeRequest(ProvisioningTarget target, AddRequestType spmlRequest) throws SpmlR2Exception {
        try {
            AddUserAttributeRequest req = new AddUserAttributeRequest();

            UserAttributeType spmlUserAttribute = (UserAttributeType) spmlRequest.getData();
            BeanUtils.copyProperties(spmlUserAttribute, req, new String[] {"type"});
            req.setType(AttributeType.fromValue(spmlUserAttribute.getType().value()));

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected UpdateUserAttributeRequest toUpdateUserAttributeRequest(ProvisioningTarget target, ModifyRequestType spmlRequest) throws SpmlR2Exception {

        UpdateUserAttributeRequest req = new UpdateUserAttributeRequest();

        try {
            ModificationType spmlMod = spmlRequest.getModification().get(0);

            UserAttributeType spmlUserAttribute = (UserAttributeType) spmlMod.getData();
            UserAttributeDefinition userAttribute = lookupUserAttribute(target, spmlUserAttribute.getId());

            // Do not override null properties in the original object
            String[] ignoredProps = getNullProps(spmlUserAttribute, new String[] {"id", "type"});

            BeanUtils.copyProperties(spmlUserAttribute, userAttribute, ignoredProps);
            userAttribute.setType(AttributeType.fromValue(spmlUserAttribute.getType().value()));

            req.setUserAttribute(userAttribute);

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected AddGroupAttributeRequest toAddGroupAttributeRequest(ProvisioningTarget target, AddRequestType spmlRequest) throws SpmlR2Exception {
        try {
            AddGroupAttributeRequest req = new AddGroupAttributeRequest();

            GroupAttributeType spmlGroupAttribute = (GroupAttributeType) spmlRequest.getData();
            BeanUtils.copyProperties(spmlGroupAttribute, req, new String[] {"type"});
            req.setType(AttributeType.fromValue(spmlGroupAttribute.getType().value()));

            return req;
        } catch (Exception e) {
            throw new SpmlR2Exception(e);
        }
    }

    protected UpdateGroupAttributeRequest toUpdateGroupAttributeRequest(ProvisioningTarget target, ModifyRequestType spmlRequest) throws SpmlR2Exception {

        UpdateGroupAttributeRequest req = new UpdateGroupAttributeRequest();

        try {
            ModificationType spmlMod = spmlRequest.getModification().get(0);

            GroupAttributeType spmlGroupAttribute = (GroupAttributeType) spmlMod.getData();
            GroupAttributeDefinition groupAttribute = lookupGroupAttribute(target, spmlGroupAttribute.getId());

            // Do not override null properties in the original object
            String[] ignoredProps = getNullProps(spmlGroupAttribute, new String[] {"id", "type"});

            BeanUtils.copyProperties(spmlGroupAttribute, groupAttribute, ignoredProps);
            groupAttribute.setType(AttributeType.fromValue(spmlGroupAttribute.getType().value()));

            req.setGroupAttribute(groupAttribute);

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

        if (group.getAttrs() != null) {
            for (int i = 0; i < group.getAttrs().length; i++) {
                GroupAttributeValue attr = group.getAttrs()[i];
                AttributeValueType spmlAttr = new AttributeValueType();

                spmlAttr.setId(attr.getId());
                spmlAttr.setName(attr.getName());
                spmlAttr.setValue(attr.getValue());

                spmlGroup.getAttributeValue().add(spmlAttr);
            }
        }

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
            BeanUtils.copyProperties(user, spmlUser, new String[] {"groups", "attrs", "accountExpirationDate", "passwordExpirationDate"});

            if (user.getAccountExpirationDate() != null) {

                GregorianCalendar gCalendar = new GregorianCalendar();
                gCalendar.setTime(new Date(user.getAccountExpirationDate()));
                XMLGregorianCalendar xmlCalendar = null;
                try {
                    xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
                    spmlUser.setAccountExpirationDate(xmlCalendar);
                } catch (DatatypeConfigurationException e) {
                    logger.error(e.getMessage(), e);
                }

            }

            if (user.getPasswordExpirationDate() != null) {
                GregorianCalendar gCalendar = new GregorianCalendar();
                gCalendar.setTime(new Date(user.getPasswordExpirationDate()));
                XMLGregorianCalendar xmlCalendar = null;
                try {
                    xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
                    spmlUser.setPasswordExpirationDate(xmlCalendar);
                } catch (DatatypeConfigurationException e) {
                    logger.error(e.getMessage(), e);
                }

            }

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

            if (user.getAttrs() != null) {
                for (int i = 0; i < user.getAttrs().length; i++) {
                    UserAttributeValue attr = user.getAttrs()[i];
                    AttributeValueType spmlAttr = new AttributeValueType();

                    spmlAttr.setId(attr.getId());
                    spmlAttr.setName(attr.getName());
                    spmlAttr.setValue(attr.getValue());

                    spmlUser.getAttributeValue().add(spmlAttr);
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

    protected PSOType toSpmlUserAttribute(ProvisioningTarget target, UserAttributeDefinition userAttribute) {
        UserAttributeType spmlUserAttribute = new UserAttributeType();

        BeanUtils.copyProperties(userAttribute, spmlUserAttribute, new String[] {"type"});
        spmlUserAttribute.setType(oasis.names.tc.spml._2._0.atricore.AttributeType.fromValue(userAttribute.getType().value()));
        
        PSOIdentifierType psoUserAttributeId = new PSOIdentifierType();
        psoUserAttributeId.setTargetID(target.getName());
        psoUserAttributeId.setID(userAttribute.getId() + "");
        psoUserAttributeId.getOtherAttributes().put(SPMLR2Constants.userAttributeAttr, "true");

        PSOType psoUserAttribute = new PSOType();
        psoUserAttribute.setData(spmlUserAttribute);
        psoUserAttribute.setPsoID(psoUserAttributeId);

        return psoUserAttribute;
    }

    protected PSOType toSpmlGroupAttribute(ProvisioningTarget target, GroupAttributeDefinition groupAttribute) {
        GroupAttributeType spmlGroupAttribute = new GroupAttributeType();

        BeanUtils.copyProperties(groupAttribute, spmlGroupAttribute, new String[] {"type"});
        spmlGroupAttribute.setType(oasis.names.tc.spml._2._0.atricore.AttributeType.fromValue(groupAttribute.getType().value()));
        
        PSOIdentifierType psoGroupAttributeId = new PSOIdentifierType();
        psoGroupAttributeId.setTargetID(target.getName());
        psoGroupAttributeId.setID(spmlGroupAttribute.getId() + "");
        psoGroupAttributeId.getOtherAttributes().put(SPMLR2Constants.groupAttributeAttr, "true");

        PSOType psoGroupAttribute = new PSOType();
        psoGroupAttribute.setData(spmlGroupAttribute);
        psoGroupAttribute.setPsoID(psoGroupAttributeId);

        return psoGroupAttribute;
    }

    protected User lookupUser(ProvisioningTarget target, String userId) throws ProvisioningException {
        FindUserByIdRequest req = new FindUserByIdRequest();
        req.setId(userId);
        FindUserByIdResponse res = target.findUserById(req);

        return res.getUser();
    }


    protected User lookupUserByName(ProvisioningTarget target, String username) throws ProvisioningException {
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

    protected UserAttributeDefinition lookupUserAttribute(ProvisioningTarget target, String id) throws ProvisioningException {
        FindUserAttributeByIdRequest req = new FindUserAttributeByIdRequest();
        req.setId(id);
        FindUserAttributeByIdResponse res = target.findUserAttributeById(req);

        return res.getUserAttribute();
    }

    protected UserAttributeDefinition lookupUserAttributeByName(ProvisioningTarget target, String name) throws ProvisioningException {
        FindUserAttributeByNameRequest req = new FindUserAttributeByNameRequest();
        req.setName(name);
        FindUserAttributeByNameResponse res = target.findUserAttributeByName(req);

        return res.getUserAttribute();
    }

    protected GroupAttributeDefinition lookupGroupAttribute(ProvisioningTarget target, String id) throws ProvisioningException {
        FindGroupAttributeByIdRequest req = new FindGroupAttributeByIdRequest();
        req.setId(id);
        FindGroupAttributeByIdResponse res = target.findGroupAttributeById(req);

        return res.getGroupAttribute();
    }

    protected GroupAttributeDefinition lookupGroupAttributeByName(ProvisioningTarget target, String name) throws ProvisioningException {
        FindGroupAttributeByNameRequest req = new FindGroupAttributeByNameRequest();
        req.setName(name);
        FindGroupAttributeByNameResponse res = target.findGroupAttributeByName(req);

        return res.getGroupAttribute();
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

    protected void recordInfoAuditTrail(String action, ActionOutcome actionOutcome, String principal, CamelMediationExchange exchange, Properties otherProps) {

        AbstractSpmlR2Mediator mediator = (AbstractSpmlR2Mediator) channel.getIdentityMediator();
        AuditingServer aServer = mediator.getAuditingServer();

        if (aServer == null) return;

        Properties props = new Properties();

        String remoteAddr = (String) exchange.getIn().getHeader("org.atricore.idbus.http.RemoteAddress");
        if (remoteAddr != null) {
            props.setProperty("remoteAddress", remoteAddr);
        }

        String session = (String) exchange.getIn().getHeader("org.atricore.idbus.http.Cookie.JSESSIONID");
        if (session != null) {
            props.setProperty("httpSession", session);
        }

        if (otherProps != null) {
            props.putAll(otherProps);
        }

        aServer.processAuditTrail(mediator.getAuditCategory(), "INFO", action, actionOutcome, principal != null ? principal : "UNKNOWN", new Date(), null, props);
    }
}
