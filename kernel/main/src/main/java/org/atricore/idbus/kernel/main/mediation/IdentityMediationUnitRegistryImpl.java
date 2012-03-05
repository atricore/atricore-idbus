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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: IdentityMediationUnitContainer.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public class IdentityMediationUnitRegistryImpl implements IdentityMediationUnitRegistry {

    private static final Log logger = LogFactory.getLog(IdentityMediationUnitRegistryImpl.class);

    private Map<String, IdentityMediationUnit> identityMediationUnits = new ConcurrentHashMap<String, IdentityMediationUnit>();

    private Set<MediationUnitLifecycleListener> listeners;

    public Collection<IdentityMediationUnit> getIdentityMediationUnits() {
        return identityMediationUnits.values();
    }

    public void register(String idmuName, IdentityMediationUnit idmu) {

        if (logger.isTraceEnabled()) {
            logger.trace("Registering Unit ["+idmuName+"] " + idmu.getName());
        }

        if (idmu.getName() == null || !idmu.getName().equals(idmuName)) {
            logger.warn("Mediation Unit name mismatch, ["+idmu.getName()+"] trying to force name to " + idmuName);
            try {
                Method setName = idmu.getClass().getMethod("setName", String.class);
                setName.invoke(idmu, idmuName);
            } catch (Exception e) {
                logger.error("Cannot force unit name to " + idmuName + " : " + e.getMessage(), e);
            }
            
        }
        
        identityMediationUnits.put(idmuName, idmu);

        // TODO : Use enums!
        MediationUnitLifecycleEvent event = new MediationUnitLifecycleEventImpl("REGISTERED", idmuName);
        
        for (MediationUnitLifecycleListener listener : listeners) {
            try {

                if (logger.isDebugEnabled())
                    logger.debug("Notify IDMU Registration to listener : " + listener);

                listener.notify(event);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    public void unregister(String idmuName) {
        MediationUnitLifecycleEvent event = new MediationUnitLifecycleEventImpl("UNREGISTERED", idmuName);
        for (MediationUnitLifecycleListener listener : listeners) {
            try {
                if (logger.isDebugEnabled())
                    logger.debug("Notify IDMU Unregistration to listener : " + listener);
                
                listener.notify(event);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        identityMediationUnits.remove(idmuName);
    }

    public IdentityMediationUnit lookupUnit(String unitName) {
        return identityMediationUnits.get(unitName);
    }

    public Set<MediationUnitLifecycleListener> getListeners() {
        return listeners;
    }

    public void setListeners(Set<MediationUnitLifecycleListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public String toString() {
        StringBuffer st = new StringBuffer();
        for (IdentityMediationUnit u : identityMediationUnits.values()) {
            st.append("'").append(u.toString()).append("',");
        }
        return super.toString()
                + "[identityMediationUnits="+st+"]";    //To change body of overridden methods use File | Settings | File Templates.
    }


}
