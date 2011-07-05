package com.atricore.idbus.console.liveservices.liveupdate.management.internal;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.management.LiveUpdateMBean;
import com.atricore.idbus.console.liveservices.liveupdate.management.codec.UpdateDescriptor;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.openmbean.TabularData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LiveUpdateMBeanImpl implements LiveUpdateMBean {

    private static final Log logger = LogFactory.getLog(LiveUpdateMBeanImpl.class);
    
    private LiveUpdateManager updateManager;
    
    public TabularData getAvailableUpdates() {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Listing all available updates from MBean");

            Collection<UpdateDescriptorType> updates = updateManager.getAvailableUpdates();

            List<UpdateDescriptor> jmxUpdates = new ArrayList<UpdateDescriptor>(updates.size());
            for (UpdateDescriptorType update : updates) {
                jmxUpdates.add(new UpdateDescriptor(update));
            }
            TabularData table = UpdateDescriptor.tableFrom(jmxUpdates);
            return table;
        } catch (Exception e) {
            logger.error("Cannot find updates: " + e.getMessage(), e);
        }

        return null;
    }

    public TabularData getAvailableUpdates(String threshold) {
        return null;
    }

    public LiveUpdateManager getUpdateManager() {
        return updateManager;
    }

    public void setUpdateManager(LiveUpdateManager updateManager) {
        this.updateManager = updateManager;
    }
}
