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

import org.w3c.dom.Element;
import org.springframework.core.io.Resource;
import org.jbpm.graph.def.ProcessDefinition;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @org.apache.xbean.XBean element="process-fragment"
 *
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 212 $ $Date: 2008-12-05 14:53:51 -0300 (Fri, 05 Dec 2008) $
 */
public class ProcessFragmentImpl implements ProcessFragment {
    private String name;
    private String processLanguage;
    private Element content;
    private ProcessFragmentRegistry processFragmentRegistry;
    private Resource processFragmentDescriptor;
    private String lifecycle;
    private String phase;
    private ProcessDefinition definition;

    private static final Log logger = LogFactory.getLog(ProcessFragmentImpl.class);


    public void init() {

    }

    public String getName() {
        return name;
    }

    public ProcessDefinition getDefinition() {
        return definition;
    }

    public void loadDefinition(ProcessDefinition definition) {
        this.definition = definition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessLanguage() {
        return processLanguage;
    }

    public String getLifeCycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setProcessLanguage(String processLanguage) {
        this.processLanguage = processLanguage;
    }

    public Resource getProcessFragmentDescriptor() {
        return processFragmentDescriptor;
    }

    /**
     * @org.apache.xbean.Property alias="fragment-descriptor"
     *
     * @param processFragmentDescriptor
     */
    public void setProcessFragmentDescriptor(Resource processFragmentDescriptor) {
        this.processFragmentDescriptor = processFragmentDescriptor;
    }

    /**
     * @org.apache.xbean.Property alias="fragments-registry"
     *
     * @return
     */
    public ProcessFragmentRegistry getProcessFragmentRegistry() {
        return processFragmentRegistry;
    }

    public void setProcessFragmentRegistry(ProcessFragmentRegistry processFragmentRegistry) {
        this.processFragmentRegistry = processFragmentRegistry;
    }

    @Override
    public String toString() {
        return super.toString() + "[name=" + name +
                ",processLanguage=" + processLanguage +
                ",lifecycle=" + lifecycle +
                ",phase=" + phase + "]";
    }
}
