/*
 * Copyright (c) 2010., Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.josso.main.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoMediator;
import org.atricore.idbus.capabilities.josso.main.PartnerAppMapping;
import org.atricore.idbus.capabilities.samlr2.support.core.util.ProtocolUtils;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.federation.SubjectAttribute;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.SubjectRole;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractJossoProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(AbstractJossoProducer .class);

    public AbstractJossoProducer(Endpoint endpoint) {
        super(endpoint);
    }

    protected SSOUser toSSOUser(Subject subject) {

        Set<SubjectNameID> ids = subject.getPrincipals(SubjectNameID.class);

        if (ids.size() != 1) {
            logger.error("Invalid subjectNameID count " + ids.size());
            return null;
        }

        SubjectNameID id = ids.iterator().next();
        BaseUserImpl user = new BaseUserImpl(id.getName());

        Set<SubjectAttribute> attrs = subject.getPrincipals(SubjectAttribute.class);
        for (SubjectAttribute attr : attrs) {
            // TODO : Make this configurable ?! perhaps the JOSSO Assertion should already be modified ?!
            String name = attr.getName();
            if (name.lastIndexOf(":") > 0)
                name = name.substring(name.lastIndexOf(':') + 1);
            name = name.replace('.', '_');

            user.addProperty(new SSONameValuePair(name, attr.getValue()));
        }

        return user;
    }

    protected Collection<SSORole> toSSORoles(Subject subject) {
        Set<SubjectRole> roles = subject.getPrincipals(SubjectRole.class);
        Set<SSORole> ssoRoles = new HashSet<SSORole>(roles.size());
        for (SubjectRole role : roles) {
            ssoRoles.add(new BaseRoleImpl(role.getName()));
        }

        return ssoRoles;
    }


    protected FederatedLocalProvider getProvider() {
        if (channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getProvider();
        } else if (channel instanceof BindingChannel) {
            return ((BindingChannel) channel).getProvider();
        } else if (channel instanceof ClaimChannel) {
            return ((ClaimChannel) channel).getProvider();
        } else {
            throw new IllegalStateException("Configured channel does not support Federated Provider : " + channel);
        }
    }

    protected BindingChannel resolveSpBindingChannel(BindingChannel bChannel, String appId) {

        PartnerAppMapping mapping = resolveAppMapping(bChannel, appId);
        if (mapping == null) {
            logger.error("Unknown Application Id " + appId);
            throw new RuntimeException("Unknown Application Id " + appId);
        }

        String spAlias = mapping.getSpAlias();

        CircleOfTrust cot = getProvider().getCircleOfTrust();

        for (Provider p : cot.getProviders()) {

            if (p instanceof ServiceProvider) {

                ServiceProvider sp = (ServiceProvider)p;
                for (CircleOfTrustMemberDescriptor m : sp.getMembers()) {
                    if (m.getAlias().equals(spAlias)) {
                        if (logger.isDebugEnabled())
                            logger.debug("Found Service Provider " + p.getName() + " for alias " + spAlias);

                        return ((ServiceProvider) p).getBindingChannel();

                    }
                }
                
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("No Service Provider found for alias " + spAlias);

        return null;

    }

    protected PartnerAppMapping resolveAppMapping(BindingChannel bChannel,
                                                          String appId) {

        JossoMediator mediator = ((JossoMediator)bChannel.getIdentityMediator());
        PartnerAppMapping mapping = mediator.getPartnerAppMapping(appId);

        if (mapping != null) {
            if (logger.isDebugEnabled())
                logger.debug("Partner App mappig found for application ID " + appId + "=" + mapping.getPartnerAppACS());
        } else {
            if (logger.isDebugEnabled())
                logger.debug("No Partner App mappig found for application ID " + appId);
        }

        return mapping;

    }

    protected SubjectType toSubjectType(Subject subject) {
        return ProtocolUtils.toSubjectType(subject);
    }

    protected Subject toSubject(SubjectType subjectType) {
        return ProtocolUtils.toSubject(subjectType);
    }

}
