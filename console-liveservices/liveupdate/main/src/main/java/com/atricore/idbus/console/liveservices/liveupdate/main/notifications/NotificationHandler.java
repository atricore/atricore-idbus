package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface NotificationHandler {

    boolean canHandle(NotificationScheme scheme);

    void notify(Collection<UpdateDescriptorType> updates, NotificationScheme scheme) throws LiveUpdateException;

    void saveNotificationScheme(NotificationScheme scheme) throws LiveUpdateException;

    void removeNotificationScheme(NotificationScheme scheme) throws LiveUpdateException;
}
