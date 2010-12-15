package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentitySource;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EmbeddedIdentitySourceTransformer extends AbstractTransformer {

    @Override
    public boolean accept(TransformEvent event) {
        if (event.getData() instanceof IdentitySource) {
            IdentitySource iv = (IdentitySource) event.getData();
            return false;
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
