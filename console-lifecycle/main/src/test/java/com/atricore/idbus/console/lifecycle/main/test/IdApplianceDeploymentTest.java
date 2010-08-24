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
public class IdApplianceDeploymentTest {


    private static final Log logger = LogFactory.getLog(IdApplianceDeploymentTest.class);

    private ApplicationContext applicationContext;

    @Before
	public void setUp() {
        applicationContext = new ClassPathXmlApplicationContext(
                "/com/atricore/idbus/console/lifecycle/main/test/idbus-transform-test.xml"
        );
    }

    @Test
    public void simpleDump() {
        
        IdentityApplianceDefinition applianceDef1 = (IdentityApplianceDefinition) applicationContext.getBean("idApplianceDef1");

        logger.info("idbus.name        : " + applianceDef1.getName());
        logger.info("idbus.description : " + applianceDef1.getDescription());
        logger.info("idbus.location    : " + applianceDef1.getLocation());

        for (Provider provider : applianceDef1.getProviders()) {

            logger.info("provider.name        : " + provider.getName());
            logger.info("provider.description : " + provider.getDescription());
            logger.info("provider.role        : " + provider.getRole().name());
            if (provider instanceof LocalProvider) {

                LocalProvider lp = (LocalProvider) provider;

                Channel dc = null; // TODO : REFACTOR lp.getDefaultChannel();
                logger.info("default-channel.name        : " + dc.getName());
                logger.info("default-channel.description : " + dc.getDescription());

                for (Binding b : dc.getActiveBindings()) {
                    logger.info("default-channel.binding     : " + b.getFullName());
                }

                for (Profile p : dc.getActiveProfiles()) {
                    logger.info("default-channel.profile     : " + p.getName());
                }

                /* TODO : REFACTOR
                for (Channel c : ((LocalProvider) provider).getChannels()) {

                    logger.info("channel.name        : " + c.getName());
                    logger.info("channel.description : " + c.getDescription());
                    logger.info("channel.source      : " + c.getTarget().getName());

                    for (Binding b : c.getActiveBindings()) {
                        logger.info("channel.binding     : " + b.getFullName());
                    }

                    for (Profile p : c.getActiveProfiles()) {
                        logger.info("channel.profile     : " + p.getName());
                    }

                }
                */

            }

        }


    }

    @Test
    public void walkerDump() throws Exception {
    }

    @Test
    public void transformTest() throws Exception {
        IdentityApplianceBuilder builder = (IdentityApplianceBuilder) applicationContext.getBean("applianceBuilder");
        IdentityApplianceDefinition iadef = (IdentityApplianceDefinition) applicationContext.getBean("idApplianceDef1");

        IdentityAppliance appliance = new IdentityAppliance();

        appliance.setIdApplianceDefinition(iadef);
        appliance = builder.build(appliance);
    }

}
