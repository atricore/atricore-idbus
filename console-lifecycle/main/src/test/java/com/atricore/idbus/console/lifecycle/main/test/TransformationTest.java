/*
 * Copyright (c) 2009., Atricore Inc.
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

package com.atricore.idbus.console.lifecycle.main.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class TransformationTest {


    private static final Log logger = LogFactory.getLog(TransformationTest.class);

    private ApplicationContext applicationContext;

    @Before
	public void setUp() {
        applicationContext = new ClassPathXmlApplicationContext(
                "/com/atricore/idbus/console/lifecycle/main/test/transform-test-beans.xml"
        );
    }

    @Test
    public void simpleDump() {
        
        IdentityAppliance appliance = newApplianceInstance("ida1");
        IdentityApplianceDefinition applianceDef1 = appliance.getIdApplianceDefinition();

        logger.info("idbus.name        : " + applianceDef1.getName());
        logger.info("idbus.description : " + applianceDef1.getDescription());
        logger.info("idbus.location    : " + applianceDef1.getLocation());

        for (Provider provider : applianceDef1.getProviders()) {

            logger.info("provider.name        : " + provider.getName());
            logger.info("provider.description : " + provider.getDescription());
            logger.info("provider.role        : " + provider.getRole().name());
            if (provider instanceof LocalProvider) {

                LocalProvider lp = (LocalProvider) provider;

                for (Binding b : lp.getActiveBindings()) {
                    logger.info("provider.binding     : " + b.getFullName());
                }

                for (Profile p : lp.getActiveProfiles()) {
                    logger.info("provider.profile     : " + p.getName());
                }

                if (lp.getIdentityLookup() != null) {

                    IdentityLookup idl = lp.getIdentityLookup();
                    logger.info("provider.identityLookup.name               : " + idl.getName());
                    logger.info("provider.identityLookup.provider.name      : " + idl.getProvider().getName());

                    IdentitySource ids = idl.getIdentitySource();

                    logger.info("provider.identityLookup.identitySourc.name     : " + ids.getName());
                    logger.info("provider.identityLookup.identitySourc.embedded : " + ids.isEmbedded());
                    logger.info("provider.identityLookup.identitySourc.type     : " + ids.getType());
                    logger.info("provider.identityLookup.identitySourc.class    : " + ids.getClass().getSimpleName());

                }

            }

            if (provider instanceof FederatedProvider) {
                FederatedProvider fp = (FederatedProvider) provider;

                if (fp.getFederatedConnectionsA() != null) {

                    for (FederatedConnection fc : fp.getFederatedConnectionsA()) {

                        logger.info("provider.federatedConnectionsA.name                           : "
                                + fc.getName());
                        logger.info("provider.federatedConnectionsA.roleA.name                     : "
                                + fc.getRoleA().getName());
                        logger.info("provider.federatedConnectionsA.name                           : "
                                + fc.getName());
                        logger.info("provider.federatedConnectionsA.roleB.name                     : "
                                + fc.getRoleB().getName());

                        logger.info("provider.federatedConnectionsA.channelA.name                  : "
                                + fc.getChannelA().getName());

                        logger.info("provider.federatedConnectionsA.channelA.overrideProviderSetup : "
                                + fc.getChannelA().isOverrideProviderSetup());

                        logger.info("provider.federatedConnectionsA.roleB.name                     : "
                                + fc.getRoleB().getName());

                        logger.info("provider.federatedConnectionsA.channelB.name                  : "
                                + fc.getChannelB().getName());

                        logger.info("provider.federatedConnectionsA.channelB.overrideProviderSetup : "
                                + fc.getChannelB().isOverrideProviderSetup());

                    }
                }

                if (fp.getFederatedConnectionsB() != null) {

                    for (FederatedConnection fc : fp.getFederatedConnectionsB()) {

                        logger.info("provider.federatedConnectionsB.name                           : "
                                + fc.getName());
                        logger.info("provider.federatedConnectionsB.roleA.name                     : "
                                + fc.getRoleA().getName());
                        logger.info("provider.federatedConnectionsB.name                           : "
                                + fc.getName());
                        logger.info("provider.federatedConnectionsB.roleB.name                     : "
                                + fc.getRoleB().getName());

                        logger.info("provider.federatedConnectionsB.channelA.name                  : "
                                + fc.getChannelA().getName());

                        logger.info("provider.federatedConnectionsB.channelA.overrideProviderSetup : "
                                + fc.getChannelA().isOverrideProviderSetup());

                        logger.info("provider.federatedConnectionsB.roleB.name                     : "
                                + fc.getRoleB().getName());

                        logger.info("provider.federatedConnectionsB.channelB.name                  : "
                                + fc.getChannelB().getName());

                        logger.info("provider.federatedConnectionsB.channelB.overrideProviderSetup : " 
                                + fc.getChannelB().isOverrideProviderSetup());

                    }
                }

            }

        }


    }

    //@Test
    public void transformTest() throws Exception {
        IdentityApplianceBuilder builder = (IdentityApplianceBuilder) applicationContext.getBean("applianceBuilder");
        IdentityApplianceDefinition iadef = (IdentityApplianceDefinition) applicationContext.getBean("idApplianceDef1");

        IdentityAppliance appliance = new IdentityAppliance();

        appliance.setIdApplianceDefinition(iadef);
        appliance = builder.build(appliance);
    }

    protected IdentityAppliance newApplianceInstance(String name) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/atricore/idbus/console/lifecycle/main/test/appliance-model-beans.xml");
        return (IdentityAppliance) ctx.getBean(name);
    }


}
