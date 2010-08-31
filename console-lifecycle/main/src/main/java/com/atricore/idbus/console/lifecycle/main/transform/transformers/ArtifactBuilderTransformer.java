package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceProject;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ArtifactBuilderTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(ArtifactBuilderTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof IdentityApplianceDefinition;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        try {

            IdApplianceProject prj = event.getContext().getProject();

            if (logger.isDebugEnabled())
                logger.debug("Serializing identity appliance project " + prj.getId());

            buildArtifacts(prj);


            return null;
        } catch (Exception e) {
            throw new TransformException(e);
        }
    }

    protected void buildArtifacts(IdApplianceProject prj) throws Exception {
        // Trigger maven using this module as root module
        IdProjectModule rootModule = prj.getRootModule();

        
    }

}
