package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EMailNotificationHandler implements NotificationHandler {

    public boolean canHandle(NotificationScheme scheme) {
        return scheme instanceof EMailNotificationScheme;
    }

    public void notify(UpdateDescriptorType update, NotificationScheme scheme) {
        // TODO : Send email if scheme is equals or above threshold
    }

    public Properties marshall(NotificationScheme scheme) {
        // Convert scheme to properties
        return null;
    }

    public NotificationScheme unmarshall(Properties props) {
        // Convert properties to scheme
        return null;
    }
}
