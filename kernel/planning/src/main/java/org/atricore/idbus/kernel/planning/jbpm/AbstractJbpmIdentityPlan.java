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

package org.atricore.idbus.kernel.planning.jbpm;

import org.atricore.idbus.kernel.planning.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.BundleContextAware;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;

/**
 * @org.apache.xbean.XBean element="identity-plan"
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 13 $ $Date: 2007-11-01 19:50:10 -0300 (Thu, 01 Nov 2007) $
 */
public abstract class AbstractJbpmIdentityPlan implements Constants, IdentityPlan,
        InitializingBean, ApplicationContextAware, BundleContextAware {

    protected static final Log logger = LogFactory.getLog(AbstractJbpmIdentityPlan.class);

    private String processDescriptorName;

    private BPMSManager bpmsManager;

    private String processType;

    private boolean init = false;

    private ApplicationContext applicationContext;
    protected BundleContext bundleContext;

    protected abstract String getProcessDescriptorName();

    public BPMSManager getBpmsManager() {
        return bpmsManager;
    }

    public void setBpmsManager(BPMSManager bpmsManager) {
        this.bpmsManager = bpmsManager;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;

    }


    public void afterPropertiesSet() throws Exception {
        init();
    }

    /**
     * @org.apache.xbean.InitMethod
     *
     * @throws IdentityPlanningException
     */
    public void init() throws IdentityPlanningException {

        try {
            logger.info("Init JBPM Identity plan ");

            if (bpmsManager == null)
                throw new IllegalStateException("No BPMS Manager configured for plan with rocess descriptor name : "  +
                        processDescriptorName);
            processType = bpmsManager.deployProcessDefinition(getProcessDescriptorName());

            init = true;
            logger.info("Init JBPM Identity Plan OK, Process type " + processType);

        } catch (IdentityPlanningException e) {
            throw new IdentityPlanningException(e);
        } catch (Exception e) {
            throw new IdentityPlanningException(e);
        }
    }

    public IdentityPlanExecutionExchange prepare(IdentityPlanExecutionExchange exchange) throws IdentityPlanningException {
        return exchange;
    }

    public void perform(IdentityPlanExecutionExchange exchange) throws IdentityPlanningException {
        bpmsManager.perform(processType, getProcessDescriptorName(), prepare(exchange));
    }

}
