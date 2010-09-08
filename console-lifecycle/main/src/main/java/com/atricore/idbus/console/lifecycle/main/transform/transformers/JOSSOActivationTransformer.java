package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.ExecutionEnvironment;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.JOSSOActivation;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.*;
import org.atricore.idbus.capabilities.josso.main.JossoMediator;
import org.atricore.idbus.capabilities.josso.main.PartnerAppMapping;
import org.atricore.idbus.kernel.main.mediation.provider.BindingProviderImpl;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JOSSOActivationTransformer extends AbstractTransformer {

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof JOSSOActivation &&
                event.getContext().getParentNode() instanceof ExecutionEnvironment;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        // Define partenr apps in Binding provider
        JOSSOActivation activation = (JOSSOActivation) event.getData();
        ExecutionEnvironment execEnv = (ExecutionEnvironment) event.getContext().getParentNode();

        Beans bpBeans = (Beans) event.getContext().get("bpBeans");
        Collection<Bean> bpMediators = getBeansOfType(bpBeans, JossoMediator.class.getName());
        if (bpMediators.size() != 1) {
            throw new TransformException("Too many/few Joss Mediators found for " + activation.getName());
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

        Value partnerappKeyValue = new Value();
        partnerappKeyValue.getContent().add(activation.getPartnerAppId());
        Key partnerappKeyBean = new Key();
        partnerappKeyBean.getBeenAndRevesAndIdreves().add(partnerappKeyValue);

        //setConstructorArg(partnerappKeyBean, 0, "java.lang.String", provider.getName());

        Bean partnerappBean = newAnonymousBean(PartnerAppMapping.class);
        partnerappBean.setName(bpBean.getName() + "-" + activation.getName() + "-partnerapp-mapping");

        setPropertyValue(partnerappBean, "partnerAppId", activation.getPartnerAppId());

        // TODO : Maybe we can get this value from the context ..
        String spAlias = resolveLocationUrl(activation.getSp()) + "/SAML2/MD";
        setPropertyValue(partnerappBean, "spAlias", spAlias);

        // TODO : Support different execution environments like ISAPI, PHP, etc ....
        setPropertyValue(partnerappBean, "partnerAppSLO", resolveLocationUrl(activation.getPartnerAppLocation()));
        setPropertyValue(partnerappBean, "partnerAppACS", resolveLocationUrl(activation.getPartnerAppLocation()) + "josso_security_check");

        Entry partnerappMapping = new Entry();
        partnerappMapping.setKey(partnerappKeyBean);
        partnerappMapping.getBeenAndRevesAndIdreves().add(partnerappBean);

        addEntryToMap(bindingMediator, "partnerAppMappings", partnerappMapping);

        // Add Partner app config, if necessary
        if (event.getContext().get("agentBean") != null) {


            Bean agentBean = (Bean) event.getContext().get("agentBean");
            java.util.List<Bean> cfgs = getPropertyBeans(agentBean, "configuration");
            Bean cfgBean = cfgs.get(0);

            Bean agentAppBean = newAnonymousBean("org.josso.agent.SSOPartnerAppConfig");
            agentAppBean.

            addPropertyBean(cfgBean, "ssoPartnerApps", agentAppBean);

        }

    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        return null;
    }
}