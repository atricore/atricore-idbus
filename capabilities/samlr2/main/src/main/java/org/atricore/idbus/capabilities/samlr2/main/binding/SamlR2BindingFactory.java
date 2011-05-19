/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.samlr2.main.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationBinding;
import org.atricore.idbus.kernel.main.mediation.MediationBindingFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationBinding;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @org.apache.xbean.XBean element="samlr2-binding-factory"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2BindingFactory extends MediationBindingFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(SamlR2BindingFactory.class);

    protected ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public MediationBinding createBinding(String binding, Channel channel) {
        
        SamlR2Binding b = null;
        try {
            b = SamlR2Binding.asEnum(binding);
        } catch (IllegalArgumentException e) {
                return null;
        }
        
        
        MediationBinding mb = null;
        switch (b) {
            case SAMLR2_POST:
                mb = new SamlR2HttpPostBinding(channel);
                break;
            case SAMLR2_REDIRECT: 
                mb = new SamlR2HttpRedirectBinding(channel);
                break;
            case SAMLR2_ARTIFACT:
                mb = new SamlR2HttpArtifactBinding(channel);
                break;
            case SAMLR2_SOAP: 
                mb = new SamlR2SoapBinding(channel);
                break;
            case SAMLR2_LOCAL:
                mb = new SamlR2LocalBinding(channel);
                break;
            case SAMLR2_PAOS: // TODO : Implement SAML R2 PAOS Binding
                mb = null;
                break;
            case SAMLR11_SOAP:
                mb = new SamlR11SoapBinding(channel);
                break;
            case SAMLR11_ARTIFACT:
                mb = new SamlR11HttpArtifactBinding(channel);
                break;
            case SS0_REDIRECT:
                mb = new SsoHttpRedirectBinding(channel);
                break;
            case SSO_ARTIFACT:
                mb = new SsoHttpArtifactBinding(channel);
                break;
            case SSO_POST:
                mb = new SsoHttpPostBinding(channel);
                break;
            case SSO_SOAP:
                mb = new SsoSoapBinding(channel);
                break;
            case SSO_IDP_INITIATED_SSO_HTTP_SAML2:
                mb = new SamlR2SsoIDPInitiatedHttpBinding(channel);
                break;
            case SSO_IDP_INITIATED_SSO_HTTP_SAML11:
                mb = new SamlR11SsoIDPInitiatedHttpBinding(channel);
                break;
            case SSO_LOCAL:
                mb = new SsoLocalBinding(channel);
                break;
            default:
                logger.warn("Unknown SAMLR2 Binding! " + binding);
        }
        
        if (mb != null && mb instanceof AbstractMediationBinding) {

            Map<String, ConfigurationContext> cfgs  = applicationContext.getBeansOfType(ConfigurationContext.class);
            if (cfgs.size() == 1) {
                ConfigurationContext cfg = cfgs.values().iterator().next();
                ((AbstractMediationBinding)mb).setConfigurationContext(cfg);
            }

            ((AbstractMediationBinding)mb).setStateManagerClassLoader(this.applicationContext.getClassLoader());
        }
        
        return mb;
        
    }
}
