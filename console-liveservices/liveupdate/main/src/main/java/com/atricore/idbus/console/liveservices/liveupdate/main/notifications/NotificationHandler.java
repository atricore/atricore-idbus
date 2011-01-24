package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface NotificationHandler {

    boolean canHandle(NotificationScheme scheme);

    void notify(UpdateDescriptorType update, NotificationScheme scheme);

    Properties marshall(NotificationScheme scheme);

    NotificationScheme unmarshall(Properties props);

}
