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

import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 212 $ $Date: 2008-12-05 14:53:51 -0300 (Fri, 05 Dec 2008) $
 */
public class ProcessFragmentLifecycleListener {

    protected final transient Log logger = LogFactory.getLog(getClass());

    public ProcessFragmentRegistry processFragmentRegistry;

    public ProcessFragmentRegistry getProcessFragmentRegistry() {
        return processFragmentRegistry;
    }

    public void setProcessFragmentRegistry(ProcessFragmentRegistry processFragmentRegistry) {
        this.processFragmentRegistry = processFragmentRegistry;
    }

    public void onBind(ProcessFragment processFragment, Map properties) throws Exception {

        processFragment.setProcessFragmentRegistry(processFragmentRegistry);
        processFragment.init();
        logger.info("Process Fragment [" + processFragment.getName() + "] Registered; " +
                    "Lifecycle [" + processFragment.getLifeCycle() + "] " +
                    "Phase [" + processFragment.getPhase() + "]" );
    }

    public void onUnbind(ProcessFragment processFragment, Map properties) throws Exception {
        processFragment.setProcessFragmentRegistry(processFragmentRegistry);
        logger.info("Process Fragment [" + processFragment.getName() + "] Unregistered; " +
                    "Lifecycle [" + processFragment.getLifeCycle() + "] " +
                    "Phase [" + processFragment.getPhase() + "]" );

    }


}
