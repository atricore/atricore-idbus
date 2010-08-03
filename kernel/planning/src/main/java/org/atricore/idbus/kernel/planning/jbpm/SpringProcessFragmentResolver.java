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

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.JpdlException;
import org.dom4j.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 236 $ $Date: 2008-12-13 22:17:26 -0200 (Sat, 13 Dec 2008) $
 */

public class SpringProcessFragmentResolver implements ProcessFragmentResolver {

    protected final transient Log logger = LogFactory.getLog(getClass());

    private ProcessFragmentRegistry processFragmentRegistry;

    public SpringProcessFragmentResolver(ProcessFragmentRegistry processFragmentRegistry) {
        this.processFragmentRegistry = processFragmentRegistry;
    }

    public ProcessFragment findProcessFragment(Element processFragmentElement) {
        ProcessDefinition processFragmentDefinition = null;
        Collection<ProcessFragment> boundProcessFragments = null;

        String processFragmentLifecycle = processFragmentElement.attributeValue("lifecycle");
        String processFragmentPhase = processFragmentElement.attributeValue("phase");

        logger.debug("Looking for process fragment contributors for lifecycle ["
                + processFragmentLifecycle + "] phase [" + processFragmentPhase + "]");

        boundProcessFragments =
                processFragmentRegistry.lookupBoundProcessFragments(processFragmentLifecycle, processFragmentPhase);

        ProcessFragment boundProcessFragment = null;
        if (boundProcessFragments.size() > 0 ) {
            boundProcessFragment = boundProcessFragments.iterator().next();
            logger.debug("Fragment " + boundProcessFragment.getName() + " matched for lifecycle ["
                + processFragmentLifecycle + "] phase [" + processFragmentPhase + "]");
        } else {
            logger.debug("No Fragment bound to lifecycle ["
                + processFragmentLifecycle + "] phase [" + processFragmentPhase + "]");
        }

        if (boundProcessFragment != null) {
            try {
                java.io.InputStream is = boundProcessFragment.getProcessFragmentDescriptor().getInputStream();
                if (is == null)
                    throw new JpdlException("Process fragment not found : " + boundProcessFragment.getProcessFragmentDescriptor());

                processFragmentDefinition = ProcessDefinition.parseXmlInputStream(is);
                boundProcessFragment.loadDefinition(processFragmentDefinition);
                
            } catch (IOException e) {
                throw new JpdlException("cannot load process definition for process fragment: " +
                        processFragmentElement.asXML() + ", " + e.getMessage(), e);
            }
        }

        return boundProcessFragment;
    }
}
