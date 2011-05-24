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

package org.atricore.idbus.capabilities.josso.main.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.samlr2.main.binding.SsoHttpRedirectBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationBinding;
import org.atricore.idbus.kernel.main.mediation.MediationBindingFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationBinding;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @org.apache.xbean.XBean element="josso11-binding-factory"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JossoBindingFactory extends MediationBindingFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(JossoBindingFactory.class);

    private ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public MediationBinding createBinding(String binding, Channel channel) {
        JossoBinding b;

        try {
            b = JossoBinding.asEnum(binding);
        } catch (IllegalArgumentException e) {
            return null;
        }

        MediationBinding mb = null;

        switch (b) {
            case JOSSO_SOAP:
                mb = new JossoSoapBinding(channel);
                break;
            case JOSSO_ARTIFACT:
                mb = new JossoHttpArtifactBinding(channel);
                break;
            case JOSSO_REDIRECT:
                mb = new JossoHttpRedirectBinding(channel);
                break;
            case SSO_ARTIFACT:
                mb = new SsoHttpArtifactBinding(channel);
                break;
            case SSO_REDIRECT:
                mb = new SsoHttpRedirectBinding(channel);
                break;
            default:
                logger.warn("Unknown JOSSO Binding " + binding);
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
