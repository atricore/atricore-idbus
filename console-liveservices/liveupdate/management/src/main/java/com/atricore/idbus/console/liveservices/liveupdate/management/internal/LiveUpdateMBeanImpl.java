package com.atricore.idbus.console.liveservices.liveupdate.management.internal;

import com.atricore.idbus.console.liveservices.liveupdate.management.LiveUpdateMBean;

import javax.management.openmbean.TabularData;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LiveUpdateMBeanImpl implements LiveUpdateMBean {
    public TabularData getAvailableUpdates() {
        return null;
    }

    public TabularData getAvailableUpdates(String threshold) {
        return null;
    }
}
