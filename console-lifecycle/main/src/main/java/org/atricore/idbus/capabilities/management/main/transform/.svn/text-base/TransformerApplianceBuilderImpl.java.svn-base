package org.atricore.idbus.capabilities.management.main.transform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceDeployment;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceBuilder;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class TransformerApplianceBuilderImpl implements IdentityApplianceBuilder {

    private static final Log logger = LogFactory.getLog(TransformerApplianceBuilderImpl.class);

    private TransformationEngine engine;

    public TransformationEngine getEngine() {
        return engine;
    }

    public void setEngine(TransformationEngine engine) {
        this.engine = engine;
    }

    public IdentityAppliance build(IdentityAppliance appliance) {

        IdentityApplianceDeployment deployment = appliance.getIdApplianceDeployment();
        if (deployment == null) {

            if (logger.isDebugEnabled())
                logger.debug("Creating new IdentityApplianceDeployment instance");
            deployment = new IdentityApplianceDeployment();
            appliance.setIdApplianceDeployment(deployment);
        }

        IdApplianceTransformationContext ctx = engine.transform(appliance);

        return ctx.getProject().getIdAppliance();
    }


}
