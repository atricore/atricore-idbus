package com.atricore.idbus.console.lifecycle.main.transform.transformers.selfservices;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.Activation;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.InternalSaml2ServiceProvider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.SelfServicesResource;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoMediator;
import org.atricore.idbus.kernel.main.mediation.provider.BindingProviderImpl;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.getBeansOfType;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/28/13
 */
public class SelfServicesResourceBindingTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(SelfServicesResourceBindingTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof SelfServicesResource &&
                event.getContext().getParentNode() instanceof Activation;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        // Define partner apps in Binding provider
        SelfServicesResource selfServicesResource = (SelfServicesResource) event.getData();
        InternalSaml2ServiceProvider sp = selfServicesResource.getServiceConnection().getSp();

        Beans bpBeans = (Beans) event.getContext().get("bpBeans");
        Collection<Bean> bpMediators = getBeansOfType(bpBeans, JossoMediator.class.getName());
        if (bpMediators.size() != 1) {
            throw new TransformException("Too many/few Josso Mediators found for " + selfServicesResource.getName());
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