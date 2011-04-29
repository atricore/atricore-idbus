package com.atricore.idbus.console.liveservices.liveupdate.management;

import javax.management.openmbean.TabularData;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface LiveUpdateMBean {

    String UPDATE_DESCRIPTOR_NATURE = "Nature";

    String UPDATE_DESCRIPTOR_GROUP = "Group";

    String UPDATE_DESCRIPTOR_NAME = "Name";

    String UPDATE_DESCRIPTOR_VERSION = "Version";

    String UPDATE_DESCRIPTOR_DESCRIPTION = "Description";

    String UPDATE_DESCRIPTOR_RELEASE_DATE = "Release date";

    String[] UPDATE_DESCRIPTOR = { UPDATE_DESCRIPTOR_NATURE, UPDATE_DESCRIPTOR_GROUP, UPDATE_DESCRIPTOR_NAME,
            UPDATE_DESCRIPTOR_VERSION, UPDATE_DESCRIPTOR_DESCRIPTION, UPDATE_DESCRIPTOR_RELEASE_DATE };

    TabularData getAvailableUpdates();

    TabularData getAvailableUpdates(String threshold);
}
