package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import javax.management.Notification;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractNotificationScheme implements NotificationScheme {

    private String name;

    private String threshold;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }
}
