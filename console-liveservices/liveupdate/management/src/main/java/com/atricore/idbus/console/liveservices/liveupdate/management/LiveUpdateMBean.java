package com.atricore.idbus.console.liveservices.liveupdate.management;

import javax.management.openmbean.TabularData;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface LiveUpdateMBean {

    TabularData getAvailableUpdates();

    TabularData getAvailableUpdates(String threshold);


}
