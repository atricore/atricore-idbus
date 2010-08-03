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

package org.atricore.idbus.kernel.main.mediation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: IdentityMediationUnitContainer.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public class SpringMediationUnit implements IdentityMediationUnit, ApplicationContextAware,
    DisposableBean, InitializingBean {

    private static final Log logger = LogFactory.getLog(SpringMediationUnit.class);

    private String name;

    private Collection<Channel> channels = new ArrayList<Channel>();

    private ApplicationContext applicationContext;

    private IdentityMediationUnitContainer container;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IdentityMediationUnitContainer getContainer() {
        return container;
    }

    public void setContainer(IdentityMediationUnitContainer unitContainer) {
        this.container = unitContainer;
    }

    public Collection<Channel> getChannels() {
        return channels;
    }

    /**
     * @org.apache.xbean.Property alias="channels" nestedType="org.josso.federation.channel.Channel"
     *
     * @param channels
     */
    public void setChannels(Collection<Channel> channels) {
        this.channels = channels;
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("Using ApplicationContext " + (applicationContext != null ?
                applicationContext.getClass().getName() : "null"));
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void destroy() throws Exception {

    }

    public void afterPropertiesSet() throws Exception {
        
    }

    @Override
    public String toString() {
        return super.toString() + "[name='"+name+"'"+
                "]";
    }
}
