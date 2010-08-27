package com.atricore.idbus.console.lifecycle.main.test.util;

import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.Phase;
import com.atricore.idbus.console.lifecycle.main.transform.TransformationEngine;
import com.atricore.idbus.console.lifecycle.main.transform.TransformerVisitor;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class TestTransformationEngine extends TransformationEngine {

    @Override
    protected TransformerVisitor doMakeVisitor(IdApplianceTransformationContext ctx, Phase phase) {

        TestIdApplianceLoggerVisitor v = new TestIdApplianceLoggerVisitor();
        v.setContext(ctx);
        v.getTransformers().addAll(phase.getTransformers());
        return v;

    }

}
