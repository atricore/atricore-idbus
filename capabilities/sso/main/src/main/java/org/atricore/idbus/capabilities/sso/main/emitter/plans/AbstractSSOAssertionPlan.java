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

package org.atricore.idbus.capabilities.sso.main.emitter.plans;

import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.planning.jbpm.AbstractJbpmIdentityPlan;

import java.util.Set;

/**
 * @org.apache.xbean.XBean element="abstract-saml-assertion-plan"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AbstractSSOAssertionPlan.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public abstract class AbstractSSOAssertionPlan extends AbstractJbpmIdentityPlan {

    private SSOIdentityManager identityManager;

    private Set<SubjectNameIDBuilder> nameIDBuilders;

    private SubjectNameIDBuilder defaultNameIDBuilder;

    private boolean ignoreRequestedNameIDPolicy = false;

    /**
     * @org.apache.xbean.Property alias="identity-manager"
     *
     * @return
     */
    public SSOIdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setIdentityManager(SSOIdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public Set<SubjectNameIDBuilder> getNameIDBuilders() {
        return nameIDBuilders;
    }

    public void setNameIDBuilders(Set<SubjectNameIDBuilder> nameIDBuilders) {
        this.nameIDBuilders = nameIDBuilders;
    }

    public SubjectNameIDBuilder getDefaultNameIDBuilder() {
        return defaultNameIDBuilder;
    }

    public void setDefaultNameIDBuilder(SubjectNameIDBuilder defaultNameIDBuilder) {
        this.defaultNameIDBuilder = defaultNameIDBuilder;
    }

    public boolean isIgnoreRequestedNameIDPolicy() {
        return ignoreRequestedNameIDPolicy;
    }

    public void setIgnoreRequestedNameIDPolicy(boolean ignoreRequestedNameIDPolicy) {
        this.ignoreRequestedNameIDPolicy = ignoreRequestedNameIDPolicy;
    }
}
