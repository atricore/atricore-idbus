package org.atricore.idbus.kernel.main.mediation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MediationEngineImpl implements MediationEngine, MediationUnitLifecycleListener,
        InitializingBean, DisposableBean, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(MediationEngineImpl.class);

    private IdentityMediationUnitRegistry registry;

    private ApplicationContext applicationContext;

    public void afterPropertiesSet() throws Exception {

        for (IdentityMediationUnit unit : registry.getIdentityMediationUnits()) {

            if (unit instanceof OsgiIdentityMediationUnit)
                startUnit((OsgiIdentityMediationUnit) unit);
            else {
                logger.error("Unsupported Identity Mediation Unit type " + unit.getClass().getName());
            }
        }
    }

    public Collection<IdentityMediationUnit> getAllUnits() {
        return registry.getIdentityMediationUnits();
    }

    public IdentityMediationUnit lookupUnit(String name) {
        return registry.lookupUnit(name);
    }

    public void destroy() throws Exception {

    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public IdentityMediationUnitRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(IdentityMediationUnitRegistry registry) {
        this.registry = registry;
    }

    public void notify(MediationUnitLifecycleEvent event) {

        try {

            String unitName = event.getUnitName();

            if (logger.isTraceEnabled())
                logger.trace("Procesing event " + event.getType() + " for unit " + event.getUnitName());
            
            IdentityMediationUnit unit = registry.lookupUnit(unitName);
            if (unit == null) {
                logger.error("Cannot resolve unit for " + unitName);
                return;
            }

            if (event.getType().equals("REGISTERED")) {
                startUnit((OsgiIdentityMediationUnit) unit);
            } else if (event.getType().equals("UNREGISTERED")) {
                stopUnit((OsgiIdentityMediationUnit) unit);
            } else {
                logger.warn("Unhandled event " + event.getType());
            }
        } catch (IdentityMediationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void startUnit(OsgiIdentityMediationUnit unit) throws IdentityMediationException {
        // TODO : For classloader issues that need to be solved, the unit is tarted by spring
    }


    protected void stopUnit(OsgiIdentityMediationUnit unit) throws IdentityMediationException {
        Collection<Channel> channels = unit.getChannels();

        // initialize mediation mediation engines (e.g. create  context)
        for (Channel channel : channels) {
            // TODO : Stop engines only once
            if (channel.getUnitContainer() != null) {
                IdentityMediationUnitContainer unitContainer = channel.getUnitContainer();
                unitContainer.stop();
            }
        }

    }
}
