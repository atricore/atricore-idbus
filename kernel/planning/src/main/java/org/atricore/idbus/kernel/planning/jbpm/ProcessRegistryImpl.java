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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.service.importer.ServiceReferenceProxy;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 278 $ $Date: 2008-12-31 19:42:22 -0200 (Wed, 31 Dec 2008) $
 * @org.apache.xbean.XBean element="process-fragment-registry"
 */
public class ProcessRegistryImpl implements ProcessFragmentRegistry, BundleContextAware, InitializingBean {

    protected final transient Log logger = LogFactory.getLog(getClass());

    //private HashMap<String, ProcessFragment> fragments = new HashMap<String, ProcessFragment>();
    private List<ProcessFragment> processFragments = new ArrayList<ProcessFragment>();
    private List<ProcessDescriptor> processDescriptors = new ArrayList<ProcessDescriptor>();

    private ProcessFragmentRegistryApplicationContext fac;
    private OsgiProcessFragmentRegistryApplicationContext osgiFac;
    private BundleContext bundleContext;
    private HashMap<Bundle, Set<ProcessAction>> bundleActions = new HashMap<Bundle, Set<ProcessAction>>();


    public List<ProcessFragment> getProcessFragments() {
        return processFragments;
    }

    public void setProcessFragments(List<ProcessFragment> processFragments) {
        this.processFragments = processFragments;
    }

    public List<ProcessDescriptor> getProcessDescriptors() {
        return processDescriptors;
    }

    public void setProcessDescriptors(List<ProcessDescriptor> processDescriptors) {
        this.processDescriptors = processDescriptors;
    }

    public void registerAction(ProcessAction processAction, Bundle actionContributor) {
        Set<ProcessAction> actions = bundleActions.get(actionContributor);
        if (actions == null) {
            actions = new HashSet<ProcessAction>();
            bundleActions.put(actionContributor, actions);
        }
        actions.add(processAction);
    }

    public ProcessFragment lookupProcessFragment(String name) {

        ProcessFragment targetPf = null;

        if (logger.isDebugEnabled())
            logger.debug("Looking for process fragment [" + name + "]");

        if (name == null)
            throw new NullPointerException("Process fragment name cannot be null");

        for (ProcessFragment processFragment : processFragments) {
            if (processFragment.getName() == null) {
                if (logger.isDebugEnabled())
                    logger.debug("Process fragment MUST have a name " + processFragment + ", ignoring fragment!");
                continue;
                // samlr2-spinitiatedauthnreq-to-samlr2authnreq-process-fragment
                //        spinitiatedauthnreq-to-samlr2authnreq-process-fragment
            }

            if (processFragment.getName().equals(name)) {
                targetPf = processFragment;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Looking for process fragment ["+name+"] " + (targetPf == null ? "NOT" : "") + " found!");

        return targetPf;
    }

    public Collection<ProcessFragment> lookupBoundProcessFragments(String lifecycle, String phase) {

        if (logger.isDebugEnabled())
            logger.debug("Looking for process fragments bound at [" + lifecycle + ":" + phase + "]");

        Collection<ProcessFragment> boundProcessFragments = new ArrayList<ProcessFragment>();
        for (ProcessFragment processFragment : processFragments) {
            if (processFragment.getLifeCycle() != null && processFragment.getPhase() != null) {
                if (processFragment.getLifeCycle().equals(lifecycle) && processFragment.getPhase().equals(phase)) {
                    boundProcessFragments.add(processFragment);
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Found " + boundProcessFragments.size() +
                    " process fragments bound at [" + lifecycle + ":" + phase + "]");

        return boundProcessFragments;
    }

    public Collection<ProcessFragment> listProcessFragments() {
        return processFragments;
    }

    public Collection<ProcessDescriptor> listProcessDescriptors() {
        return processDescriptors;
    }


    public ProcessDescriptor lookupProcessDescriptor(String name) {
        ProcessDescriptor targetPd = null;
        if (logger.isDebugEnabled())
            logger.debug("Looking for process descriptor ["+name+"]");

        for (ProcessDescriptor processDescriptor : processDescriptors) {
            if (processDescriptor.getName().equals(name)) {
                targetPd = processDescriptor;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Looking for process descriptor ["+name+"] " + (targetPd == null ? "NOT" : "") + " found!");

        return targetPd;
    }

    public Class findProcessActionClass(String qualifiedActionName) throws ClassNotFoundException {

        Class foundClass = null;
        Set<Bundle> bundleActionKeys =  bundleActions.keySet();

        if (logger.isDebugEnabled())
            logger.debug("Looking in " + bundleActions.size() + " bundles for process action class " + qualifiedActionName);

        for (Bundle bundle : bundleActionKeys) {

            Set<ProcessAction> processActions = bundleActions.get(bundle);
            if (logger.isTraceEnabled())
                logger.trace("Bundle ("+bundle.getBundleId()+") has " + processActions.size() + " actions");

            for (ProcessAction processAction : processActions) {

                if (logger.isTraceEnabled())
                    logger.trace("Bundle ("+bundle.getBundleId()+") has " + processAction.getClass().getName() + " action");

                if (processAction.getQualifiedClassName().equals(qualifiedActionName)) {
                    foundClass = bundle.loadClass(qualifiedActionName);
                }
            }
        }

        if (foundClass == null) {
            logger.debug("Class : " + qualifiedActionName + " not found, is this a process action class?");
            throw new ClassNotFoundException(qualifiedActionName);
        }

        return foundClass;
    }


    public void close() {
        if (fac != null)
            fac.close();
        else if (osgiFac != null) {
            osgiFac.close();
        }

    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void afterPropertiesSet() throws Exception {
        for (Bundle actionContributor : bundleActions.keySet()) {

            Set<ProcessAction> actions = bundleActions.get(actionContributor);

            logger.info((actions != null ? actions.size() : "<none>") + " Process Actions contributed by bundle (" +
                    actionContributor.getBundleId() + ") " + actionContributor.getSymbolicName());

            if (logger.isDebugEnabled()) {
                if (actions!=null) {
                    for (ProcessAction pa : actions) {
                        logger.debug("Process Action " + pa.getQualifiedClassName() + " contributed by bundle " +
                                actionContributor.getBundleId());
                    }
                }
            }
        }
    }

    /*
    public void afterPropertiesSet() throws Exception {

        for (ServiceReference serviceReference : processActionReferences) {

            Bundle actionContributor = serviceReference.getBundle();

            ServiceReference[] serviceReferences = actionContributor.getRegisteredServices();

            for (ServiceReference reference : serviceReferences) {

                Object service = bundleContext.getService(reference);

                if (service instanceof ProcessAction) {
                    ProcessAction action = (ProcessAction) service;

                    if (bundleActions.get(actionContributor) == null) {
                        bundleActions.put(actionContributor, new HashSet());
                    }

                    Set<ProcessAction> bundleContributedActions = bundleActions.get(actionContributor);

                    if (!bundleContributedActions.contains(action)) {
                        bundleContributedActions.add(action);

                        logger.info(
                                "Registered Process Action [" + action.getQualifiedClassName() + "] " +
                                        "contributed by Bundle [" + actionContributor.getBundleId() + "] " +
                                        "Class [" + actionContributor.loadClass(action.getQualifiedClassName()) + "] " +
                                        "Instance [" + actionContributor.loadClass(action.getQualifiedClassName()).newInstance() + "]");
                    }

                }

            }

        }

    } */
}
