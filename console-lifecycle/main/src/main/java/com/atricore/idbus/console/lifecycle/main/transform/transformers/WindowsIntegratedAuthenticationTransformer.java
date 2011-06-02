package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationService;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.WindowsAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.WindowsIntegratedAuthentication;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.addPropertyBeansAsRefs;


/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WindowsIntegratedAuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(WindowsIntegratedAuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {

        if (!(event.getData() instanceof WindowsAuthentication))
            return false;

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();
        AuthenticationService authnService = idp.getDelegatedAuthentication().getAuthnService();

        return authnService != null && authnService instanceof WindowsIntegratedAuthentication;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        String idauPath = (String) event.getContext().get("idauPath");

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();
        WindowsIntegratedAuthentication wia = (WindowsIntegratedAuthentication) idp.getDelegatedAuthentication().getAuthnService();

        // TODO : For now user veolicty , but we MUST use blueprint xml bunding, like we do with spring!

        String spn = wia.getServiceClass() +
                "/" + wia.getHost() + ":" + wia.getPort() +
                (wia.getServiceName() != null && !"".equals(wia.getServiceName()) ? "/" + wia.getServiceName() : "") +
                "@" + wia.getDomain();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("realmName", wia.getName());
        params.put("servicePrincipalName", spn);


        IdProjectModule module = event.getContext().getCurrentModule();

        IdProjectResource<String> agentConfig = new IdProjectResource<String>(idGen.generateId(),
                "OSGI-INF/blueprint/", "kerberos-jaas", "kerberos", "jaas");
        agentConfig.setClassifier("velocity");
        agentConfig.setExtension("xml");
        agentConfig.setParams(params);
        agentConfig.setScope(IdProjectResource.Scope.RESOURCE);
        module.addResource(agentConfig);


    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        return null;
    }

}

