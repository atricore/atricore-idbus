package com.atricore.idbus.console.lifecycle.main.transform.transformers.mstr;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoMediator;
import org.atricore.idbus.kernel.main.mediation.provider.BindingProviderImpl;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 9/24/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MstrResourceBindingTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(MstrResourceBindingTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof MicroStrategyResource &&
                event.getContext().getParentNode() instanceof Activation;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        // Define partner apps in Binding provider
        MicroStrategyResource mstrResource = (MicroStrategyResource) event.getData();
        InternalSaml2ServiceProvider sp = mstrResource.getServiceConnection().getSp();

        Beans bpBeans = (Beans) event.getContext().get("bpBeans");
        Collection<Bean> bpMediators = getBeansOfType(bpBeans, JossoMediator.class.getName());
        if (bpMediators.size() != 1) {
            throw new TransformException("Too many/few Josso Mediators found for " + mstrResource.getName());
        }

        Bean bindingMediator = bpMediators.iterator().next();

        // Add partner app definition to BP Mediator, set partnerAppMappings property.

        // BP partnerAppMappings
        Bean bpBean = null;
        Collection<Bean> bps = getBeansOfType(bpBeans, BindingProviderImpl.class.getName());
        if (bps.size() == 1) {
            bpBean = bps.iterator().next();
        } else {
            throw new TransformException("One and only one Binding Provider is expected, found " + bps.size());
        }

    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        return null;
    }

}