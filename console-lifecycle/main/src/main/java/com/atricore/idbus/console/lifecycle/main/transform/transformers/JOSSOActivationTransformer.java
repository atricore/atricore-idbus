package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentitySource;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.JOSSOActivation;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JOSSOActivationTransformer extends AbstractTransformer {

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof JOSSOActivation;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        // Define Binding channel !
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        return null;
    }
}

