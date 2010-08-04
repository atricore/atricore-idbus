package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityVault;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;

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
