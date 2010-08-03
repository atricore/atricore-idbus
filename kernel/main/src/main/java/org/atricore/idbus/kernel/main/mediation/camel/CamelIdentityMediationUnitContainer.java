/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.kernel.main.mediation.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.cxf.transport.CamelTransportFactory;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.atricore.idbus.kernel.main.mediation.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.*;

/**
 * This is a Apache Camel-based Identity Federation Engine that realizes mediation semantics
 * through routes and components.
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: CamelIdentityMediationUnitContainer.java 1359 2009-07-19 16:57:57Z sgonzalez $
 *
 * @org.apache.xbean.XBean element="identity-mediation-engine"
 */
public class CamelIdentityMediationUnitContainer implements IdentityMediationUnitContainer, DisposableBean, InitializingBean {

    private static final Log logger = LogFactory.getLog(CamelIdentityMediationUnitContainer.class);

    private Collection<IdentityMediator> mediators;

    private String name;
    private CamelContext context;
    private ProducerTemplate template;
    private boolean active = false;
    private boolean init = false;
    private SpringMediationUnit unit;

    // CXF Bus.  We programatically add Camel Transport to it ...
    private Bus bus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpringMediationUnit getUnit() {
        return unit;
    }

    public void setUnit(SpringMediationUnit unit) {
        this.unit = unit;
    }

    public CamelIdentityMediationUnitContainer() {
    }


    public void init() {
        if (init)
            return;

        init = true;
    }

    public void init(IdentityMediationUnit u) throws IdentityMediationException {

        try {

            // We need a spring/camel IMU
            unit = (SpringMediationUnit) u;

            logger.info("Initializing Camel based Identity Mediation Engine with " + getMediators().size() + " mediators.");

            // ---------------------------------------------------------
            // Setup CXF, adding Camel transport programatically
            // ---------------------------------------------------------
            // TODO : THIST SHOULD BE DONE DECLARATIVE, USING SPRING DM!
            logger.debug("Configuring Camel Transport for CXF ... ");
            CamelTransportFactory camelTransportFactory = new CamelTransportFactory();
            camelTransportFactory.setTransportIds(new ArrayList<String>());
            camelTransportFactory.getTransportIds().add(CamelTransportFactory.TRANSPORT_ID);
            camelTransportFactory.setCamelContext(context);
            camelTransportFactory.setBus(bus);

            // register the conduit initiator
            ConduitInitiatorManager cim = bus.getExtension(ConduitInitiatorManager.class);
            cim.registerConduitInitiator(CamelTransportFactory.TRANSPORT_ID, camelTransportFactory);

            // register the destination factory
            DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
            dfm.registerDestinationFactory(CamelTransportFactory.TRANSPORT_ID, camelTransportFactory);

            // set the bus as default
            // BusFactory.setDefaultBus(bus);

            ApplicationContext applicationContext = unit.getApplicationContext();
            logger.debug("Binding Application Context [" + applicationContext.getDisplayName() + "] to JNDI Registry");

            ((JndiRegistry) context.getRegistry()).bind("applicationContext", applicationContext);
            template = context.createProducerTemplate();

            for (IdentityMediator mediator : getMediators()) {
                // pass on camel context
                mediator.init(this);
            }

        } catch (Exception e) {
            throw new IdentityMediationException(e);
        }
    }

    public void start() throws IdentityMediationException {

        if (isActive())
            return;

        try {

            for (IdentityMediator mediator : getMediators()) {
                // nop
                mediator.start();
            }

            startCamelContext();
            if (logger.isDebugEnabled())
                logger.debug("Federation Routing Rules are: " + context.getRoutes());

            active = true;

            logger.info("IDBus Camel based Identity Mediation Engine started OK");
        } catch (Exception e) {
            throw new IdentityMediationException(e);
        }
    }

    public ProducerTemplate getTemplate() {
        return template;
    }

    public CamelContext getContext() {
        return context;
    }

    public ApplicationContext getApplicationContext() {
        return unit.getApplicationContext();
    }

    public Bus getCxfBus() {
        return bus;
    }

    public void setCxfBus(Bus bus) {
        this.bus = bus;
    }

    protected CamelContext createCamelContext() throws Exception {
        return new DefaultCamelContext(createRegistry());
    }

    protected JndiRegistry createRegistry() throws Exception {
        return new JndiRegistry(createJndiContext());
    }

    protected Context createJndiContext() throws Exception {
        return createInitialContext();
    }

    protected Context createInitialContext() throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "org/atricore/idbus/kernel/main/mediation/camel/jndi.properties"
        );
        Properties properties = new Properties();
        if (in != null)
            properties.load(in);
        else
            logger.warn("Camel Identity Mediation Engine wasn't able to load jndi.properties");

        return new InitialContext(new Hashtable(properties));
    }

    protected void startCamelContext() throws Exception {
        if (context instanceof DefaultCamelContext) {
            DefaultCamelContext defaultCamelContext = (DefaultCamelContext) context;
            if (!defaultCamelContext.isStarted()) {
                defaultCamelContext.start();
            }
        } else {
            context.start();
        }
    }

    public void stop() throws IdentityMediationException {

        if (!isActive())
            return;

        try {
            for (IdentityMediator mediator : getMediators()) {
                if (logger.isDebugEnabled())
                    logger.debug("Stopping mediationr " + mediator);
                mediator.stop();
            }
        } catch (Exception e) {
            throw new IdentityMediationException(e);
        } finally {
            try {

                if (logger.isDebugEnabled())
                    logger.debug("Stopping Camel context " + context);

                context.stop();
                active = false;
                logger.debug("Stopped Engine Camel Context [" + context.getName() + "]");
            } catch (Exception e) {
                if (logger.isDebugEnabled())
                    logger.debug("Error while stopping Camel context " + e.getMessage(), e);
            }
        }
    }

    public Collection<IdentityMediator> getMediators() {
        if (mediators == null)
            mediators = new java.util.ArrayList<IdentityMediator>();
        
        return mediators;
    }

    /**
     * @org.apache.xbean.Property alias="mediators" nestedType="org.josso.federation.mediation.FederationMediator"
     *
     * @param mediators
     */
    public void setMediators(Collection<IdentityMediator> mediators) {
        this.mediators = mediators;
    }

    protected boolean isActive() {
        return active;
    }

    public void destroy() throws Exception {

    }

    public void afterPropertiesSet() throws Exception {
        context = createCamelContext();
        context.getRegistry();
    }

    @Override
    public String toString() {
        return super.toString() + "[name="+name+"]";
    }
}
