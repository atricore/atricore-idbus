package com.atricore.idbus.console.lifecycle.main.transform.transformers.josso1;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoMediator;
import org.atricore.idbus.capabilities.josso.main.PartnerAppMapping;
import org.atricore.idbus.kernel.main.mediation.provider.BindingProviderImpl;

import java.util.ArrayList;
import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JOSSO1ResourceBindingTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(JOSSO1ResourceBindingTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        if (logger.isTraceEnabled())
            logger.trace("JOSSO1ResourceBinding: " + event.getData() + ", " + event.getContext().getParentNode()  );

        if (logger.isTraceEnabled()) {
            if (event.getData() instanceof Activation) {
                logger.trace("JOSSO1ResourceBinding: activation resource = " + ((Activation)event.getData()).getResource());
            }
        }

        return event.getData() instanceof Activation  &&
               ((Activation)event.getData()).getResource() instanceof JOSSO1Resource;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {
        if (logger.isTraceEnabled())
            logger.trace("Entered JOSSO1ResourceBindingTransformer::before");

        // Define partner apps in Binding provider
        JOSSO1Resource josso1Resource = (JOSSO1Resource) ((Activation)event.getData()).getResource();
        InternalSaml2ServiceProvider sp = josso1Resource.getServiceConnection().getSp();

        Beans bpBeans = (Beans) event.getContext().get("bpBeans");
        Collection<Bean> bpMediators = getBeansOfType(bpBeans, JossoMediator.class.getName());
        if (bpMediators.size() != 1) {
            throw new TransformException("Too many/few Josso Mediators found for " + josso1Resource.getName());
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
        partnerappKeyValue.getContent().add(sp.getName());
        Key partnerappKeyBean = new Key();
        partnerappKeyBean.getBeenAndRevesAndIdreves().add(partnerappKeyValue);

        //setConstructorArg(partnerappKeyBean, 0, "java.lang.String", provider.getName());

        Bean partnerappBean = newAnonymousBean(PartnerAppMapping.class);
        partnerappBean.setName(bpBean.getName() + "-" + josso1Resource.getName() + "-partnerapp-mapping");

        setPropertyValue(partnerappBean, "partnerAppId", sp.getName());

        IdentityProviderChannel preferredIdpChannel = null;
        for (FederatedConnection fc : sp.getFederatedConnectionsA()) {
            IdentityProviderChannel idpc = (IdentityProviderChannel) fc.getChannelA();
            if (idpc.isPreferred()) {
                preferredIdpChannel = idpc;
                break;
            }
        }

        if (preferredIdpChannel == null) {
            for (FederatedConnection fc : sp.getFederatedConnectionsB()) {
                IdentityProviderChannel idpc = (IdentityProviderChannel) fc.getChannelB();
                if (idpc.isPreferred()) {
                    preferredIdpChannel = idpc;
                    break;
                }
            }
        }

        // TODO : Maybe we can get this value from the context ..
        String spAlias = resolveLocationUrl(sp, preferredIdpChannel) + "/SAML2/MD";
        setPropertyValue(partnerappBean, "spAlias", spAlias);

        setPropertyValue(partnerappBean, "partnerAppSLO", resolveSLOLocationUrl(josso1Resource));
        setPropertyValue(partnerappBean, "partnerAppACS", resolveACSLocationUrl(josso1Resource));

        Entry partnerappMapping = new Entry();
        partnerappMapping.setKey(partnerappKeyBean);
        partnerappMapping.getBeenAndRevesAndIdreves().add(partnerappBean);

        addEntryToMap(bindingMediator, "partnerAppMappings", partnerappMapping);

        logger.trace("JOSSO1ResourceBinding: agentBean = " + event.getContext().get("agentBean"));

        // Add Partner app config, if necessary
        if (event.getContext().get("agentBean") != null) {

            Bean agentBean = (Bean) event.getContext().get("agentBean");
            Bean cfgBean = getPropertyBean(agentBean, "configuration");
            Bean agentAppBean = newAnonymousBean("org.josso.agent.SSOPartnerAppConfig");

            setPropertyValue(agentAppBean, "id", sp.getName());
            setPropertyValue(agentAppBean, "vhost", josso1Resource.getPartnerAppLocation().getHost());
            setPropertyValue(agentAppBean, "context",
                    (!josso1Resource.getPartnerAppLocation().getContext().startsWith("/") ? "/" : "") +
                    josso1Resource.getPartnerAppLocation().getContext());

            if (josso1Resource.getDefaultResource() != null) {
                setPropertyValue(agentAppBean, "defaultResource", josso1Resource.getDefaultResource());
            }
            // TODO : Remember me, disable auto-login, etc. ....

            // Ignored web resources (for JEE)
            if (josso1Resource.getIgnoredWebResources() != null && josso1Resource.getIgnoredWebResources().size() > 0) {
                java.util.List<String> wr = new ArrayList<String>();
                wr.addAll(josso1Resource.getIgnoredWebResources());
                setPropertyAsValues(agentAppBean, "ignoredWebResources", wr);
            }

            // TODO : Ignored URL Patters

            addPropertyBean(cfgBean, "ssoPartnerApps", agentAppBean);

        }

    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        return null;
    }

    protected String resolveACSLocationUrl(JOSSO1Resource josso1Resource) {

        // TODO : Support different execution environments like ISAPI, PHP, etc ....
        ExecutionEnvironment execEnv = josso1Resource.getActivation().getExecutionEnv();

        String appLocation = resolveLocationUrl(josso1Resource.getPartnerAppLocation());
        if (!appLocation.endsWith("/"))
            appLocation = appLocation + "/";

        String baseLocation = resolveLocationBaseUrl(josso1Resource.getPartnerAppLocation());
        if (!baseLocation.endsWith("/"))
            baseLocation += "/";

        if (execEnv == null)
            return  appLocation + "josso_security_check";

        if (execEnv instanceof Apache2ExecutionEnvironment) {
            return  appLocation + "josso_security_check";

        } else if (execEnv instanceof ApacheExecutionEnvironment) {
            return  appLocation + "josso_security_check";

        } else if (execEnv instanceof JEEExecutionEnvironment) {
            logger.error("Execution Environment NOT supported by this transformer " + execEnv.getName() + " ["+execEnv.getPlatformId()+"]");

        // TODO : Make this configurable, and default valuue should be josso/agent.sso ....
        } else if (execEnv instanceof IISExecutionEnvironment) {
            // Base location always ends with a slash:
            String isapiExtension = ((IISExecutionEnvironment) execEnv).getIsapiExtensionPath();
            if (isapiExtension.startsWith("/"))
                isapiExtension = isapiExtension.substring(1);

            return baseLocation + isapiExtension + "?josso_security_check";

        } else if (execEnv instanceof WindowsIISExecutionEnvironment) {
            // Base location always ends with a slash:
            String isapiExtension = ((WindowsIISExecutionEnvironment) execEnv).getIsapiExtensionPath();
            if (isapiExtension.startsWith("/"))
                isapiExtension = isapiExtension.substring(1);

            return baseLocation + isapiExtension  + "?josso_security_check";

        } else if (execEnv instanceof PHPExecutionEnvironment) {
            return appLocation + "josso-security-check.php";

        } else if (execEnv instanceof WeblogicExecutionEnvironment) {
            return appLocation + "josso-wls/josso_security_check.jsp";
        }

        // Defautl value
        return appLocation + "josso_security_check";

    }

    protected String resolveSLOLocationUrl(JOSSO1Resource josso1Resource) {
        return resolveLocationUrl(josso1Resource.getPartnerAppLocation());
    }

}