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


import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @org.apache.xbean.XBean element="process-descriptor"
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 252 $ $Date: 2008-12-22 18:24:21 -0200 (Mon, 22 Dec 2008) $
 */
public class ProcessDescriptorImpl implements ProcessDescriptor {
    private String name;
    private String bootstrapProcessFragmentName;
    private Collection<String> activeProcessFragments = new ArrayList<String>();
    private ProcessConfiguration configuration;
    private ProcessFragmentRegistry processFragmentRegistry;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @org.apache.xbean.Property alias="bootstrap-fragment"
     * @return
     */
    public String getBootstrapProcessFragmentName() {
        return bootstrapProcessFragmentName;
    }

    public void setBootstrapProcessFragmentName(String bootstrapProcessFragmentName) {
        this.bootstrapProcessFragmentName = bootstrapProcessFragmentName;
    }

    public Collection getActiveProcessFragments() {
        return activeProcessFragments;
    }

    public boolean isActive(String processFragmentName) {
        boolean active = false;

        for (String f : activeProcessFragments) {
            if (f.equals(processFragmentName))
                active = true;
        }

        return active;
    }

    /**
     * @org.apache.xbean.Property alias="active-fragments" nestedType="java.lang.String"
     *
     * @param activeProcessFragments
     */
    public void setActiveProcessFragments(Collection activeProcessFragments) {
        this.activeProcessFragments = activeProcessFragments;
    }

    public ProcessConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ProcessConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setProcessFragmentRegistry(ProcessFragmentRegistry processFragmentRegistry) {
        this.processFragmentRegistry = processFragmentRegistry;
    }

    @Override
    public String toString() {
        return super.toString() + "[name=" + name +
                ",bootstrapProcessFragmentName=" + bootstrapProcessFragmentName + "]";
    }
}
