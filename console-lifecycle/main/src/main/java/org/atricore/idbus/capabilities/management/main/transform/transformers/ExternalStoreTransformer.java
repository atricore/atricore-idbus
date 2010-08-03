package org.atricore.idbus.capabilities.management.main.transform.transformers;

import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityVault;
import org.atricore.idbus.capabilities.management.main.exception.TransformException;
import org.atricore.idbus.capabilities.management.main.transform.TransformEvent;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ExternalStoreTransformer extends AbstractTransformer {

    @Override
    public boolean accept(TransformEvent event) {
        if (event.getData() instanceof IdentityVault) {
            IdentityVault iv = (IdentityVault) event.getData();
            return !iv.isEmbedded();
        }
        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        return null;
    }
}
